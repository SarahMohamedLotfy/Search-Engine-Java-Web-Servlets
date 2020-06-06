package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import imageSearch.ImageSearch;
import indexer.Extractor;
import indexer.LuceneTester;
import org.apache.lucene.queryParser.ParseException;
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
    public static String linksToCrawl[] = {"https://stackoverflow.com/", "https://en.wikipedia.org/", "https://www.w3schools.com/", "https://www.geeksforgeeks.org/"};

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {

        int numberOFurls = 5;
        int numberOfThreads = linksToCrawl.length;
        String htmlPath = System.getProperty("user.dir") + "\\html";
        String filenamePath = System.getProperty("user.dir");
		String searchSentence ="Douglas featured collabor red important";
       // String search_sentence = "\"wikipedia free\" encyclopedia";
        // remove quotes from the sentence
        StringBuilder sb = new StringBuilder(searchSentence);
        String resultString = sb.toString();
        while (resultString.contains("\"")) {
            sb.deleteCharAt(resultString.indexOf("\""));
            resultString = sb.toString();
        }
        String search_phrase = searchSentence;
        searchSentence = resultString;


        //Crawler
        Thread crawlerThreads[] = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            crawlerThreads[i] = new Thread(new WebCrawler(linksToCrawl[i], numberOFurls));
            crawlerThreads[i].start();
        }


        //Extractor ///
        Data dataa = new Data();
        Extractor extract = new Extractor();
        extract.extractFromHtml(htmlPath, numberOFurls);


        //Indexer
        LuceneTester tester;
        try {
            long startTimeIndexer = System.currentTimeMillis();
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(searchSentence, dataa, htmlPath, dataa.urlsFromCrawler);
            tester.delete(htmlPath);
            long endTimeIndexer = System.currentTimeMillis();
            System.out.println("Indexer, time taken: " + (endTimeIndexer - startTimeIndexer) + " ms");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        //Ranker
        Boolean wantLocationScore = false;
        Boolean wantDateScore = false;
        Ranker ranker = new Ranker(dataa, searchSentence, location, wantLocationScore, wantDateScore);


        System.out.println("**********************");
        System.out.println(dataa.documentsName);
        System.out.println(dataa.documentsBody);
        System.out.println(dataa.occurencesOfWordsCount);
        System.out.println(dataa.occurencesOfWordsInTitle);
        System.out.println(dataa.occurencesOfWordsInHeader);
        System.out.println(dataa.occurencesOfWordsInPlainText);
        System.out.println(dataa.titlelist);

        System.out.println(dataa.urlsFromCrawler);
        System.out.println(dataa.urlsFromIndexer);
        //ranker output
        System.out.println(ranker.rank(dataa, dataa.urlsFromIndexer));
        System.out.println(ranker.rankIndices(dataa, dataa.urlsFromIndexer));


        //Write Data in txt files
        try {
            String documentsName = "data\\documentsName"+ ".txt";
            FileWriter myWriter = new FileWriter(documentsName);
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                myWriter.write(dataa.documentsName.get(i) + System.lineSeparator());
            }
            myWriter.close();

            List<String> documentstitles=new ArrayList<String> ();
            FileWriter myWriter2 = new FileWriter("data\\documentstitle.txt");
            for(int i=0;i< dataa.titlelist.size();i=i+5) {
                documentstitles.add(dataa.titlelist.get(i));
            }
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                myWriter2.write(documentstitles.get(i) + System.lineSeparator());
            }

            myWriter2.close();

            FileWriter myWriter3 = new FileWriter("data\\documentsurls.txt");
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                myWriter3.write(dataa.urlsFromIndexer.get(i)+ System.lineSeparator());
            }
            myWriter3.close();

            FileWriter myWriter4 = new FileWriter("data\\documentsplaintext.txt");
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                myWriter4.write(dataa.plaintextlist.get(i) +  System.lineSeparator());
            }
            myWriter4.close();


            FileWriter myWriter5 = new FileWriter("data\\documentsheader.txt");
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                if (dataa.headerslist.get(i)==null) {
                    myWriter5.write(dataa.headerslist.get(i) + " * ");
                }
            }
            myWriter5.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        //Write to DataBase
        for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
            String sql = "INSERT INTO documentsUrl(url) VALUES(?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dataa.urlsFromIndexer.get(i));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> documentstitles=new ArrayList<String> ();
        for(int i=0;i< dataa.titlelist.size();i=i+5) {
            documentstitles.add(dataa.titlelist.get(i));
        }
        for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
            String sql = "INSERT INTO documentsTitle(title) VALUES(?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, documentstitles.get(i));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
            String sql = "INSERT INTO documentsParagraphs(paragraph) VALUES(?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dataa.plaintextlist.get(i));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


        //Image search
        //Ranked urls
        List<String> rankedurls=new ArrayList<String> ();
        for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
            rankedurls.add(dataa.urlsFromIndexer.get(i));
            }
        for(String str: rankedurls) {
            ImageSearch im = new ImageSearch();
            im.extractImage(str, searchSentence);
        }

        ////////////// Phrase Search ///////////////////
        System.out.println("**********************");
        PhraseSearch phraseSearch = new PhraseSearch(search_phrase, dataa.occurencesOfWordsCount, dataa.documentsName);
        boolean phraseSearchTest = phraseSearch.checkPhraseSearch();
        if (phraseSearchTest){
            phraseSearch.countPhrase();
            System.out.println(phraseSearch.wordsToBeSearch);
        }
        ////////////// Phrase Search ///////////////////

        ////////////// query processor //////////////////
        QueryProcessor.QueryProcessor queryProcessor = new QueryProcessor.QueryProcessor(searchSentence, dataa.documentsName);
        queryProcessor.extractSimilarWords();
        //////////// query processor ///////////////////////

    }

    public static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:"+System.getProperty("user.dir")+ "\\DataBase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}