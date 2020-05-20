package com.lucene;

import org.apache.lucene.queryParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws IOException, SQLException {
    

        String htmlPath = "D:\\html files";
        String search_sentence ="sarah rehab github important ";
    
    // Crawler //
      int numberOfThreads = 3;
      Thread crawlerThreads[] = new Thread[numberOfThreads];
      
      for (int i = 0; i < numberOfThreads; ++i) {
        crawlerThreads[i] = new Thread(new WebCrawler("https://en.wikipedia.org/"));
        crawlerThreads[i].start();
      }


        //Extractor ///
         DataFromIndexer data = new DataFromIndexer ();
         Extractor extract= new Extractor();
         extract.extract_from_html(htmlPath);

        // Get words in html document
        String document = "NameGenderForm.html" ;
        GetWords getWords = new GetWords(document);
        getWords.get();

        //Indexer
        LuceneTester tester;
        try {
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(search_sentence,  data, htmlPath);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("**********************");
        System.out.println(data.documentsName);
        System.out.println(data.documentsBody);
        System.out.println(data.occurencesOfWordsCount);
        System.out.println(data.occurencesOfWordsInTitle);
        System.out.println(data.occurencesOfWordsInHeader);
        System.out.println(data.occurencesOfWordsInPlainText);

    }
}
