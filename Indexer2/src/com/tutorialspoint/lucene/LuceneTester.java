package com.tutorialspoint.lucene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


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

public class LuceneTester {

    String indexDir = "D:\\Index";
    String dataDir = "C:\\Users\\hi\\IdeaProjects\\Extractor";
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

    public void search(String searchQuery) throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
                " documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
                    + doc.get(LuceneConstants.FILE_PATH));

            String filename= doc.get(LuceneConstants.FILE_NAME);
            filename = filename.replaceAll("\\D+","");

            File dir = new File("D:\\html files");
            File[] directoryListing = dir.listFiles();
            int i=Integer.parseInt(filename);
            int count = 1;
            for (File child : directoryListing) {
                if (count ==i) {
                    System.out.println("File name: " +child.getName());
                    break;
                }
                count ++;
            }
        }
        searcher.close();
    }
}