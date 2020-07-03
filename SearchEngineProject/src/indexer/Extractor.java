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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
public class Extractor {


    public void extractFromHtml(String htmlPath, int numberOfUrls) throws IOException, InterruptedException {

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path pathh = Paths.get(htmlPath);
        String titlesPath = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\data\\documentsTitlesTotal.txt";
        String plaintextPath = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\data\\plaintextsTotal.txt";

        FileWriter myWriter2 = new FileWriter(titlesPath);
        FileWriter myWriter3 = new FileWriter(plaintextPath);

        pathh.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        boolean poll = true;
        int i=1;
        int countFiles=0;
        while (poll) {
            WatchKey key = watchService.take();
            String lastfilepath ="";
            for (WatchEvent<?> event : key.pollEvents()) {
                Path file = pathh.resolve((Path)event.context());
               // System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                String filePath = pathh + "\\" + event.context();
                if (event.kind() ==ENTRY_MODIFY && !filePath.equals(lastfilepath)) {
                    countFiles++;
                    String multihtml = new String(Files.readAllBytes(Paths.get(filePath)));
                    String[] htmlParts = multihtml.split("(?<=</html>)");
                    org.jsoup.nodes.Document doc;
                    for (String part : htmlParts) {
                        doc = Jsoup.parse(part);

                        //Title
                        String title = "";
                        title = doc.title();

                        //Headers
                        Element body = doc.body();
                        Elements paragraphs = body.getElementsByTag("header");
                        String header = "";
                        for (Element paragraph : paragraphs) {
                            header = header + paragraph.text();
                        }

                        // Plain text
                        String string = doc.body().text();
                        String tempWord = title + " " + header + " ";
                        string = string.replaceAll(tempWord, "");

                        if (!title.isEmpty() || !header.isEmpty() || !string.isEmpty()) {
                            // Write in files
                            try {
                                String filename = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\filename" + Integer.toString(i) + ".txt";
                                FileWriter myWriter = new FileWriter(filename );
                                myWriter.write(title + " , " + header + " , " + string);
                                myWriter2.write(title+ System.lineSeparator());
                                myWriter3.write(doc.body().text().replaceAll(title,"")+ System.lineSeparator());
                                System.out.println(title+ System.lineSeparator());
                                System.out.println(doc.body().text().replaceAll(title,"")+ System.lineSeparator());
                                myWriter.close();
                                i++;
                                 lastfilepath = filePath;
                            } catch (IOException e) {
                                System.out.println("An error occurred.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            if (countFiles==numberOfUrls)
            {
                break;
            }
            }
            if (countFiles==numberOfUrls)
            {
                break;
            }
            poll = key.reset();
        }
        myWriter2.close();
        myWriter3.close();
    }
}
