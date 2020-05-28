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
	public static String word = "";
	public static boolean wantToFindWord = false;

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
    	// Crawler //
		  int numberOFurls =5;
    	  int numberOfThreads = 3;
    	   Thread crawlerThreads[] = new Thread[numberOfThreads];
    	   for (int i = 0; i < numberOfThreads; ++i) {
    	    crawlerThreads[i] = new Thread(new WebCrawler("https://en.wikipedia.org/", numberOFurls));
    	    crawlerThreads[i].start();
    	   }
         String  htmlPath= "C:\\Users\\hi\\Documents\\GitHub\\APT-Search-Engine\\Indexer2\\html";
    	   String filenamePath ="C:\\Users\\hi\\Documents\\GitHub\\APT-Search-Engine\\Indexer2";
         String search_sentence ="Douglas featured collabor important";

        //Extractor ///
         DataFromIndexer data = new DataFromIndexer ();
         Extractor extract= new Extractor();
         extract.extract_from_html(htmlPath,numberOFurls);

		// Get words in html document
		String document = "NameGenderForm.html" ;
		GetWords getWords = new GetWords(document);
		getWords.get( htmlPath);

		//Indexer
		LuceneTester tester;
		try {
			tester = new LuceneTester();
			tester.createIndex();
			tester.search(search_sentence,data,htmlPath,data.urlsFromCrawler);
			//tester.delete(htmlPath,filenamePath);
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		System.out.println("**********************");
		System.out.println(data.documentsName);
		System.out.println(data.documentsBody);
		System.out.println(data.occurencesOfWordsCount);
		System.out.println(data.occurencesOfWordsInTitle);
		System.out.println(data.occurencesOfWordsInHeader);
		System.out.println(data.occurencesOfWordsInPlainText);
		System.out.println(data.urlsFromCrawler);
		System.out.println(data.urlsFromIndexer);

	}
}
