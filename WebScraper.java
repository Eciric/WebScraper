import java.util.Scanner;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.regex.*;
import java.io.FileWriter; 

class WebScraper {

    public static void main(String[] args) throws MalformedURLException, IOException, ParseException {
        String emailRegex = "[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*";
        String linkRegex = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
        
        //Fetching HTML
        URL siteURL = new URL(args[0]);
        Scanner nScanner = new Scanner(siteURL.openStream());
        StringBuffer HTML = new StringBuffer();
        while (nScanner.hasNext()) {
            HTML.append(nScanner.nextLine());
        }
        
        //Scraping links
        System.out.println("Found links:");
        scrapeData(linkRegex, HTML.toString());
        
        //Scraping emails
        System.out.println("Found emails:");
        scrapeData(emailRegex, HTML.toString());
        
        //Scraping connection information
        InetAddress address = InetAddress.getByName(siteURL.getHost());
        String addressSplit[] = address.toString().split("/");
        String ipAddress = addressSplit[addressSplit.length-1];
        String head = getHeadFromHTML(HTML.toString());

        //Saving connection information to file
        String fileName = new String(siteURL.getHost() + ".txt");
        if (saveDataToFile(fileName, ipAddress, head)) {
            System.out.println("Data sucessfully saved");
        } else {
            System.out.println("Failed to save data");
        }
    }

    public static String getHeadFromHTML(String HTML) {
        String headRegex = new String("<\\/?head>");
        Pattern hPattern = Pattern.compile(headRegex);
        Matcher hMatcher = hPattern.matcher(HTML);

        int firstHeadEnd = 0;
        int secondHeadStart = 0;
        if (hMatcher.find()) {
            firstHeadEnd = hMatcher.end();
            if (hMatcher.find()) {
                secondHeadStart = hMatcher.start();
            } else {
                System.out.println("Error occured at finding second head tag");
                return null;
            }
        } else {
            System.out.println("Error occured at finding first head tag");
            return null;
        }

        if (firstHeadEnd > 0 && secondHeadStart > 0 && secondHeadStart > firstHeadEnd) {
            return HTML.substring(firstHeadEnd, secondHeadStart);
        } else {
            System.out.println("Head locations were read incorrectly");
        }
        return null;
    }

    public static boolean saveDataToFile(String fileName, String ipAddress, String head) {
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(fileName + "\n");
            myWriter.write(ipAddress + "\n");
            myWriter.write(head + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("IOException occured in function saveDataToFile");
            return false;
        }

        return true;
    }

    public static void scrapeData(String Regex, String HTML) {
        Pattern nPattern = Pattern.compile(Regex);
        Matcher nMatcher = nPattern.matcher(HTML);
        while(nMatcher.find()) {
            System.out.println(nMatcher.group());  
        }
    }
}