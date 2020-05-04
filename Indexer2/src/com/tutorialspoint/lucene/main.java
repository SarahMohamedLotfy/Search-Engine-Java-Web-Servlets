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
       // Extractor extract= new Extractor();
        //extract.extract_from_html();

        // Get words in html document
        String document = "rehab.html" ;
        GetWords getWords = new GetWords(document);
        getWords.get();

        //Indexer
        LuceneTester tester;
        try {
            String search_sentence ="rehab sarah akbar";
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(search_sentence);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
