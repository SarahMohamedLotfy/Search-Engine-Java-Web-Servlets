package indexer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import Stemmer.Stemmer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.io.*;
import org.jsoup.Jsoup;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Data.Data;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuceneTester {

    String indexDir ="C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\Index";
    String dataDir = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject";
    Indexer indexer;
    Searcher searcher;

    public void createIndex() throws IOException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        indexer.close();
    }

    public void search(String search_word, Data data, String htmlPath, List<String> urlsFromCrawler) throws IOException, ParseException {
        searcher = new Searcher(indexDir);

        TopDocs hits = searcher.search(search_word);


        // List of names of html documents found
        List<String> documentsName=new ArrayList<String>();
        List<String> documentsBody=new ArrayList<String>();
        List<Integer> occurencesOfWordsCount=new ArrayList<Integer>();
        List<Integer> occurencesOfWordsInTitle=new ArrayList<Integer>();
        List<Integer> occurencesOfWordsInHeader=new ArrayList<Integer>();
        List<Integer> occurencesOfWordsInPlainText=new ArrayList<Integer>();
        List<String> images=new ArrayList<String>();
        List<String> titleslist=new ArrayList<String>();
        List<String> headerslist=new ArrayList<String>();
        List<String> plaintextlist=new ArrayList<String>();
        List<Integer> indcesFromIndexer=new ArrayList<Integer>();
        List<String> filenamesnames=new ArrayList<String>();
        FileWriter myWriter3 = new FileWriter("C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\data\\documentsnamesssssssss.txt");
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);

            String filename= doc.get(LuceneConstants.FILE_NAME);
            filename = filename.replaceAll("\\D+","");

            // TO GET Name of HTML document
            File dir = new File(htmlPath);
            File[] directoryListing = dir.listFiles();
            try {
                System.out.println(filename);
                int i = Integer.parseInt(filename);
                indcesFromIndexer.add(i - 1);
                int count = 1;
                myWriter3.write(i + System.lineSeparator());

                String[] searchedWords = search_word.split(" ");
                // Get the file include each word in searched words
                for (File child : directoryListing) {
                    String str = child.getName().replaceAll("\\D+", "");
                    if (str.equals(Integer.toString(i - 1))) {
                        String url = urlsFromCrawler.get(count - 1);
                        documentsName.add(child.getName());
                        myWriter3.write(child.getName() + System.lineSeparator());

                        File file = new File(doc.get(LuceneConstants.FILE_PATH));
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String st;
                        String text = "";
                        while ((st = br.readLine()) != null)
                            text = text + st;

                        documentsBody.add(text);

                        String[] arr = text.split(" ");

                        //Get position of commas
                        List<Integer> commaPositions = new ArrayList<Integer>();
                        int commaPosition = 0;
                        for (String ss : arr) {
                            if (new String(",").equals(ss)) {
                                commaPositions.add(commaPosition);
                            }
                            commaPosition++;
                        }
                        for (String word : searchedWords) {
                            int countWord = 0;

                            // Get number of occurence in each document
                            int position = 0;
                            List<Integer> positions = new ArrayList<Integer>();
                            for (String ss : arr) {
                                if (new String(word).equals(ss)) {
                                    positions.add(position);
                                }
                                position++;
                            }

                            // Get type of searched word
                            int titleCount = 0;
                            int headerCount = 0;
                            int plainTextCount = 0;
                            for (int n = 0; n < positions.size(); n++) {
                                if (positions.get(n) < commaPositions.get(0)) {
                                    titleCount++;
                                } else if (positions.get(n) < commaPositions.get(1)) {
                                    headerCount++;
                                } else
                                    plainTextCount++;
                            }
                            //image search
                            String allBody = " ";
                            String multihtml = new String(Files.readAllBytes(Paths.get(child.getPath())));
                            String[] htmlParts = multihtml.split("(?<=</html>)");
                            org.jsoup.nodes.Document htmllDoc;
                            // Get all img tags
                            for (String part : htmlParts) {
                                htmllDoc = Jsoup.parse(part);
                                Elements img = htmllDoc.getElementsByTag("img");
                                // Loop through img tags
                                for (Element el : img) {
                                    images.add(el.attr("src"));
                                    //  System.out.println("image tag: " + el.attr("src") + " Alt: " + el.attr("alt"));
                                }

                                //Title
                                String title = htmllDoc.title();
                                Element body = htmllDoc.body();
                                //header
                                Elements headers = body.getElementsByTag("header");
                                String header = "";
                                for (Element paragraph : headers) {
                                    header = header + paragraph.text();
                                }
                                allBody = htmllDoc.body().text();
                            }

                            int fromIndex = 0;
                            while ((fromIndex = allBody.indexOf(word, fromIndex)) != -1) {
                                System.out.println("Found at index: " + fromIndex);
                                countWord++;
                                fromIndex++;
                            }
                            occurencesOfWordsCount.add(countWord);
                            myWriter3.write(word + " " + countWord + System.lineSeparator());
                        }
                        break;
                    }
                    count++;
                }
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        data.SetDataFromIndexer(documentsName,documentsBody,occurencesOfWordsCount,occurencesOfWordsInTitle,occurencesOfWordsInHeader,occurencesOfWordsInPlainText,images,titleslist,headerslist,plaintextlist,indcesFromIndexer);
        searcher.close();
        myWriter3.close();

    }

    public void delete( String imagePath) throws IOException
    {
        File folder2 = new File(imagePath);
        for (File f : folder2.listFiles()) {
            f.delete();
        }
    }
}