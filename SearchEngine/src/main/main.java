package main;


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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static String word = "";
    public static boolean wantToFindWord = false;
    public static String location = "Egypt";

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        // Crawler //
        int numberOFurls = 5;
        int numberOfThreads = 3;
        String htmlPath = System.getProperty("user.dir") + "\\html";
        String filenamePath = System.getProperty("user.dir");
		String search_sentence ="Douglas featured collabor red important";
       // String search_sentence = "\"wikipedia free\" encyclopedia";
        // remove quotes from the sentence
        StringBuilder sb = new StringBuilder(search_sentence);
        String resultString = sb.toString();
        while (resultString.contains("\"")) {
            sb.deleteCharAt(resultString.indexOf("\""));
            resultString = sb.toString();
        }
        String search_phrase = search_sentence;
        search_sentence = resultString;


        Thread crawlerThreads[] = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            crawlerThreads[i] = new Thread(new WebCrawler("https://en.wikipedia.org/", numberOFurls));
            crawlerThreads[i].start();
        }


        //Extractor ///
        Data dataa = new Data();
        Extractor extract = new Extractor();
        extract.extract_from_html(htmlPath, numberOFurls);

        // Get words in html document
        //String document = "NameGenderForm.html" ;
        //GetWords getWords = new GetWords(document);
        //getWords.get( htmlPath);

        //Indexer
        LuceneTester tester;
        try {
            long startTimeIndexer = System.currentTimeMillis();
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(search_sentence, dataa, htmlPath, dataa.urlsFromCrawler);
            tester.delete(htmlPath);
            long endTimeIndexer = System.currentTimeMillis();
            System.out.println("Indexer, time taken: " + (endTimeIndexer - startTimeIndexer) + " ms");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        //Ranker
        Boolean wantLocationScore = false;
        Boolean wantDateScore = false;
        Ranker ranker = new Ranker(dataa, search_sentence, location, wantLocationScore, wantDateScore);


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

            FileWriter myWriter4 = new FileWriter("data\\documentsbody.txt");
            for(int i: ranker.rankIndices(dataa, dataa.urlsFromIndexer)) {
                myWriter4.write(dataa.plaintextlist.get(i) + " * ");
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
           //Image search
        for(String str: dataa.urlsFromIndexer) {
            ImageSearch im = new ImageSearch();
            im.extractImage(str);
        }

        ////////////// Phrase Search ///////////////////
        System.out.println("**********************");
        PhraseSearch phraseSearch = new PhraseSearch(search_phrase, dataa.occurencesOfWordsCount, dataa.documentsName);
        boolean phraseSearchTest = phraseSearch.checkPhraseSearch();
        if (phraseSearchTest) {
            phraseSearch.countPhrase();
            System.out.println(phraseSearch.wordsToBeSearch);
            for (int i = 0; i < phraseSearch.foundWords.length; ++i)
                System.out.println(" index " + i + " = " + phraseSearch.foundWords[i]);
        } else {
            System.out.println("no phrase search");
        }
        ////////////// Phrase Search ///////////////////

    }

    public static void saveImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String fileName = url.getFile();
        String destName = "./figures" + fileName.substring(fileName.lastIndexOf("/"));
        System.out.println(destName);

        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destName);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
}