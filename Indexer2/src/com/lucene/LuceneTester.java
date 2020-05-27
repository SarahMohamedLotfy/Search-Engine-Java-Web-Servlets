package com.lucene;

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
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Vector;

public class LuceneTester {

    String indexDir = "D:\\Index";
    String dataDir = "D:\\download gded\\Indexer\\Indexer2";
    Indexer indexer;
    Searcher searcher;

    public void createIndex() throws IOException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "
                +(endTime-startTime)+" ms");
    }

    public void search(String search_word,DataFromIndexer data, String htmlPath) throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(search_word);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
                " documents found. Time :" + (endTime - startTime));


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

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);

            String filename= doc.get(LuceneConstants.FILE_NAME);
            filename = filename.replaceAll("\\D+","");

            // TO GET Name of HTML document
            File dir = new File(htmlPath);
            File[] directoryListing = dir.listFiles();
            int i=Integer.parseInt(filename);
            int count = 1;

            String[] searched_words = search_word.split(" ");

            // Get the file include each word in searched words
            for (File child : directoryListing) {
                if (count ==i) {

                    System.out.println("File name: " + child.getName());
                    documentsName.add(child.getName());

                    File file = new File(doc.get(LuceneConstants.FILE_PATH));
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String st;
                    String text = "";
                    while ((st = br.readLine()) != null)
                        text = text + st;

                    documentsBody.add(text);

                    String[] arr = text.split(" ");

                    //Get position of commas
                    List<Integer> commaPositions=new ArrayList<Integer>();
                    int commaPosition =0;
                    for (String ss : arr) {
                        if (new String(",").equals(ss) ) {
                            commaPositions.add(commaPosition);
                        }
                        commaPosition ++;
                    }
                    for ( String word : searched_words) {

                        // Get number of occurence in each document
                        int countWord = 0;
                        int position=0;
                        List<Integer> positions=new ArrayList<Integer>();
                        for (String ss : arr) {
                            if (new String(word).equals(ss) ) {
                                countWord++;
                                positions.add(position);
                            }
                            position ++;
                        }

                        // Get type of searched word
                        int titleCount =0;
                        int headerCount=0;
                        int plainTextCount =0;
                        for (int n=0;n < positions.size();n++) {
                            if(positions.get(n)< commaPositions.get(0)) {
                                titleCount ++;
                            }
                            else if(positions.get(n)< commaPositions.get(1)) {
                                headerCount ++;
                            }
                            else
                                plainTextCount++;
                        }

                        System.out.println("Number of occurence of word " + word + " = " + countWord);
                        occurencesOfWordsCount.add(countWord);
                        occurencesOfWordsInTitle.add(titleCount);
                        occurencesOfWordsInHeader.add(headerCount);
                        occurencesOfWordsInPlainText.add(plainTextCount);
                        System.out.println("Type of word   " + word + " :  " + " Title Count = "+titleCount + " Header Count = "+headerCount + "  Plain Text Count = "+plainTextCount );


                        //image search
                        String multihtml = new String(Files.readAllBytes(Paths.get(child.getPath())));
                        String[] htmlParts = multihtml.split("(?<=</html>)");
                        org.jsoup.nodes.Document htmllDoc;

                        // Get all img tags
                        for (String part : htmlParts) {
                            htmllDoc = Jsoup.parse(part);
                            Elements img = htmllDoc.getElementsByTag("img");
                            // Loop through img tags
                            for (Element el : img) {
                                images.add( el.attr("src") );
                                System.out.println("image tag: " + el.attr("src") + " Alt: " + el.attr("alt"));
                            }
                            //Title
                            String title = "";
                            title = htmllDoc.title();
                            //Headers
                            Element body = htmllDoc.body();
                            Elements paragraphs = body.getElementsByTag("header");
                            String header ="";
                            for (Element paragraph : paragraphs) {
                                header = header+paragraph.text();
                            }
                            // Plain text
                            String allBody = htmllDoc.body().text();
                            String titleheader = title +" "+ header + " ";
                            String plaintext = allBody.replaceAll(titleheader, "");
                            titleslist.add(title);
                            headerslist.add(header);
                            plaintextlist.add(plaintext);
                        }
                    }
                    break;
                }
                count ++;
            }
        }
        data.SetDataFromIndexer(documentsName,documentsBody,occurencesOfWordsCount,occurencesOfWordsInTitle,occurencesOfWordsInHeader,occurencesOfWordsInPlainText,images,titleslist,headerslist,plaintextlist);
        searcher.close();
    }
}