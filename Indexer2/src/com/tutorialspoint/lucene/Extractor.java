package com.tutorialspoint.lucene;

import org.apache.lucene.queryParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Extractor {

    public void extract_from_html()throws IOException
    {
        /////  Extractor ///////////
        File dir = new File("D:\\html files");
        File[] directoryListing = dir.listFiles();
        int i=1;
        for (File child : directoryListing) {

            String path = child.getAbsolutePath();
            String multihtml = new String(Files.readAllBytes(Paths.get(path)));
            String[] htmlParts = multihtml.split("(?<=</html>)");
            org.jsoup.nodes.Document doc;

            for (String part : htmlParts) {
                doc = Jsoup.parse(part);
                System.out.println("");
                //Title
                String title = "";
                title = doc.title();
                System.out.println("title : " + title);
                System.out.println("");

                //Headers
                Element body = doc.body();
                Elements paragraphs = body.getElementsByTag("header");
                String header ="";
                for (Element paragraph : paragraphs) {
                    header = header+paragraph.text();
                    System.out.println(paragraph.text());
                }
                // Plain text
                String string = doc.body().text();
                String tempWord = title +" "+ header + " ";
                string = string.replaceAll(tempWord, "");

                tempWord = " " + title + " " + header;
                string = string.replaceAll(tempWord, "");

                System.out.println(string);
                System.out.println("******************************************");

                // Write in files
                try {
                    String filename = "filename"+Integer.toString(i)+".txt";
                    FileWriter myWriter = new FileWriter(filename);
                    myWriter.write(title + " , " + header +" , " + string);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }


            }
            i++;
        }
    }
}
