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

public class main {
    public static void main(String[] args)throws IOException {


        //Extractor ///
         DataFromIndexer data = new DataFromIndexer ();
         Extractor extract= new Extractor();
         extract.extract_from_html();

        // Get words in html document
        String document = "NameGenderForm.html" ;
        GetWords getWords = new GetWords(document);
        getWords.get();

        //Indexer
        LuceneTester tester;
        try {
            String search_sentence ="sarah rehab important ";
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(search_sentence,  data);

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
