package indexer;

import Stemmer.Stemmer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Extractor {


    public void extract_from_html(String htmlPath, int numberOfUrls) throws IOException, SQLException, InterruptedException {

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path pathh = Paths.get(htmlPath);
        pathh.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

        boolean poll = true;
        int i=1;
        String date="";
        int countfiles=0;
        while (poll) {
            WatchKey key = watchService.take();
            String lastfilepath ="";
            for (WatchEvent<?> event : key.pollEvents()) {
                Path file = pathh.resolve((Path)event.context());
                System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                String filePath = pathh + "\\" + event.context();
                if (event.kind() ==ENTRY_MODIFY && !filePath.equals(lastfilepath)) {
                    countfiles++;
                    System.out.println("file path   " + filePath+"    "+file.toFile().lastModified());
                    String multihtml = new String(Files.readAllBytes(Paths.get(filePath)));
                    String[] htmlParts = multihtml.split("(?<=</html>)");
                    org.jsoup.nodes.Document doc;
                    for (String part : htmlParts) {
                        doc = Jsoup.parse(part);

                        //Title
                        String title = "";
                        title = doc.title();

                        //Remove stop words by stemmer
                        Stemmer S = new Stemmer();
                        title = S.stem(title);

                        System.out.println("title : " + title);
                        System.out.println("");
                        //Headers
                        Element body = doc.body();
                        Elements paragraphs = body.getElementsByTag("header");
                        String header = "";
                        for (Element paragraph : paragraphs) {
                            header = header + paragraph.text();
                            //System.out.println(paragraph.text());
                        }

                        //Remove stop words
                        Stemmer S2 = new Stemmer();
                        header = S2.stem(header);

                        System.out.println("header:   " + header);
                        // Plain text
                        String string = doc.body().text();
                        String allBody = string;

                        //Remove stop words
                        Stemmer S3 = new Stemmer();
                        string = S3.stem(string);
                        String tempWord = title + " " + header + " ";
                        string = string.replaceAll(tempWord, "");

                        Stemmer S4 = new Stemmer();
                        allBody = S4.stem(allBody);


                        tempWord = " " + title + " " + header;
                        string = string.replaceAll(tempWord, "");

                        System.out.println("body:  " + string);

                        if (!title.isEmpty() || !header.isEmpty() || !string.isEmpty()) {
                            // Write in files and database
                            try {
                                String filename = "filename" + Integer.toString(i) + ".txt";
                                System.out.println("paaaaaaaath  "+i+"  "+ filename);

                                FileWriter myWriter = new FileWriter("filename" + Integer.toString(i) + ".txt");
                                myWriter.write(title + " , " + header + " , " + string);
                                myWriter.close();
                                i++;
                                System.out.println("Successfully wrote to the file.");
                                 lastfilepath = filePath;
                            } catch (IOException e) {
                                System.out.println("An error occurred.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            if (countfiles==numberOfUrls)
            {
                break;
            }
            }
            if (countfiles==numberOfUrls)
            {
                break;
            }
            poll = key.reset();
        }
    }
}
