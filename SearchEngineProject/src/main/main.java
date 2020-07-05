package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import QueryProcessor.QueryProcessor;
import imageSearch.ImageSearch;
import indexer.Extractor;
import indexer.LuceneTester;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import phraseSearch.PhraseSearch;
import ranker.Ranker;
import webCrawler.WebCrawler;
import Data.Data;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static String word = "";
    public static boolean wantToFindWord = false;
    public static String location = "Egypt";
    public static String linksToCrawl[] = {"https://stackoverflow.com/", "https://en.wikipedia.org/", "https://www.geeksforgeeks.org/", "https://www.w3schools.com/","https://en.wikipedia.org/wiki/FIFA","https://en.wikipedia.org/wiki/FIFA_World_Cup","https://www.bbc.co.uk/"};

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {

        int numberOFurls = 10;
        int numberOfThreads = 4;
        String htmlPath = System.getProperty("user.dir") + "\\html";

         // LuceneTester tester2= new LuceneTester();
         //tester2.delete(htmlPath, imagePath);

        //Crawler
        /*Thread crawlerThreads[] = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            crawlerThreads[i] = new Thread(new WebCrawler(linksToCrawl[2], numberOFurls, wantToFindWord,word));
            crawlerThreads[i] = new Thread(new WebCrawler(linksToCrawl[2], numberOFurls, wantToFindWord,word));
            crawlerThreads[i].start();
        }*/
        //Crawler
       /* Thread crawlerThreads[] = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            String trying = "https://www.instagram.com/sophiet/?hl=en";
            crawlerThreads[i] = new Thread(new WebCrawler(linksToCrawl[i], numberOFurls, wantToFindWord,word));
//            crawlerThreads[i] = new Thread(new WebCrawler(linksToCrawl[0], numberOFurls, wantToFindWord,word));
            crawlerThreads[i].start();
        }*/

        //Extractor ///
      //  Extractor extract = new Extractor();
       // extract.extractFromHtml(htmlPath, numberOFurls);

       /* String urll= "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\urldfromCrawler.txt";
        FileWriter myWriter = new FileWriter(urll);
        for(String i:WebCrawler.returUrl()) {
            myWriter.write(i + System.lineSeparator());
        }
        myWriter.close();*/

      long startTimeIndexer = System.currentTimeMillis();
        LuceneTester tester;
        tester = new LuceneTester();
        tester.createIndex();
        long endTimeIndexer = System.currentTimeMillis();
        System.out.println("Indexer, time taken: " + (endTimeIndexer - startTimeIndexer) + " ms");


    }

}



