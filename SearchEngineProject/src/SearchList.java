import Data.Data;
import QueryProcessor.QueryProcessor;
import imageSearch.ImageSearch;
import indexer.Extractor;
import indexer.LuceneTester;
import org.apache.lucene.queryParser.ParseException;
import phraseSearch.PhraseSearch;
import ranker.Ranker;
import webCrawler.WebCrawler;

import java.awt.image.BufferedImage;
import java.util.*;

import java.io.*;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;

import Stemmer.Stemmer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class SearchList extends HttpServlet {

    public LuceneTester tester2;
    public Data dataa;
    public static String word = "";
    public static String viewSearchSentence;

    public SearchList() {
        tester2 = new LuceneTester();
        dataa = new Data();

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int numberOFurls = 60;
        String projectUrl = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject";
        String htmlPath = projectUrl + "\\html";

        String searchSentence = request.getParameter("SearchSentenceInput");

        // remove quotes from the sentence
        StringBuilder sb = new StringBuilder(searchSentence);
        String resultString = sb.toString();
        while (resultString.contains("\"")) {
            sb.deleteCharAt(resultString.indexOf("\""));
            resultString = sb.toString();
        }
        String search_phrase = searchSentence;
        searchSentence = resultString;
        this.viewSearchSentence = searchSentence;

        ///////////// query processor ///////////////////////
        QueryProcessor queryProcessor = new QueryProcessor(searchSentence);
        String newSearchSentence = queryProcessor.getNewSearchSentence();
        ///////////// query processor ///////////////////////
        searchSentence = newSearchSentence;


        ArrayList<String> urlsFromCrawler = new ArrayList<String>();
        String urllll = projectUrl + "\\urldfromCrawler.txt";
        File file = new File(urllll);    //creates a new file instance
        FileReader fr = new FileReader(file);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        while ((line = br.readLine()) != null) {
            urlsFromCrawler.add(line);      //appends line to string buffer
        }
        fr.close();    //closes the stream and release the resources
        urlsFromCrawler.removeAll(Arrays.asList("", null));


        List<String> totalNames = new ArrayList<String>();
        File[] files = new File("C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\html").listFiles();
        for (File filee : files) {
            if (file.isFile()) {
                totalNames.add(filee.getName());
            }
        }

        File folder = new File("C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\html");
        File[] listOfFiles = folder.listFiles();
        List<String> gg = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String str = listOfFiles[i].getName().replaceAll("\\D+","");
                System.out.println("File " + listOfFiles[i].getName());
                gg.add(str);
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        List<String> documentstitles = new ArrayList<String>();
        String titlesUrl = projectUrl + "\\data\\documentsTitlesTotal.txt";
        File file2 = new File(titlesUrl);    //creates a new file instance
        FileReader fr2 = new FileReader(file2);   //reads the file
        BufferedReader br2 = new BufferedReader(fr2);  //creates a buffering character input stream
        String line2;
        while ((line2 = br2.readLine()) != null) {
            documentstitles.add(line2);      //appends line to string buffer
        }
        fr2.close();    //closes the stream and release the resources
        documentstitles.removeAll(Arrays.asList("", null));


        List<String> plainTexts = new ArrayList<String>();
        List<String> urlsFromIndxer = new ArrayList<String>();
        String plaintextsUrl = projectUrl + "\\data\\plaintextsTotal.txt";
        File file3 = new File(plaintextsUrl);    //creates a new file instance
        FileReader fr3 = new FileReader(file3);   //reads the file
        BufferedReader br3 = new BufferedReader(fr3);  //creates a buffering character input stream
        String line3;
        while ((line3 = br3.readLine()) != null) {
            plainTexts.add(line3);      //appends line to string buffer
        }
        fr3.close();    //closes the stream and release the resources
        plainTexts.removeAll(Arrays.asList("", null));

        String imagePath="D:\\downloads\\apache-tomcat-9.0.36\\webapps\\ROOT\\Images";
        try {
            LuceneTester tester;
            tester = new LuceneTester();
            tester.delete(imagePath);
            tester.search(searchSentence, dataa, htmlPath, urlsFromCrawler);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        List<Integer> IndicesIndexer = new ArrayList<Integer>();
        IndicesIndexer = dataa.indcesFromIndexer;

        for (int i : IndicesIndexer) {
            urlsFromIndxer.add(urlsFromCrawler.get(i));
        }


            ////////////// Phrase Search ///////////////////
        PhraseSearch phraseSearch = new PhraseSearch(search_phrase, dataa.occurencesOfWordsCount, dataa.documentsName);
        boolean phraseSearchTest = phraseSearch.checkPhraseSearch();
        if (phraseSearchTest) {
            phraseSearch.countPhrase();
            System.out.println(phraseSearch.wordsToBeSearch);
            for (int i = 0; i < phraseSearch.foundWords.length; ++i)
                System.out.println(" index " + i + " = " + phraseSearch.foundWords[i]);
            System.out.println("there is phrase search");
        } else {
            System.out.println("no phrase search");
        }
        ////////////// Phrase Search ///////////////////


        //Ranker
        Boolean wantLocationScore = true;
        String location ="Egypt";
        Boolean wantDateScore = false;
        Ranker ranker = new Ranker(dataa,urlsFromIndxer, searchSentence,location, wantLocationScore, wantDateScore);


        //Write Data in txt files
        ArrayList<String> urls = new ArrayList<String>();
        List<String> documentstitlesReturned = new ArrayList<String>();
        List<String> plainTextReturnde = new ArrayList<String>();
        List<String> urlsReturned = new ArrayList<String>();
        List<Integer> rankedIndices = new ArrayList<Integer>();
        try {
            for (int i : ranker.rankIndices(dataa, urlsFromIndxer)) {
                urls.add(urlsFromIndxer.get(i));
            }
            urls.removeAll(Arrays.asList("", null));
            for (String j : urls) {
                for (int i = 0; i < urlsFromCrawler.size(); i++) {
                    if (urlsFromCrawler.get(i).equals(j)) {
                        rankedIndices.add(i);
                        break;
                    }
                }
            }
            rankedIndices.removeAll(Arrays.asList("", null));




            String[] searchedWords = searchSentence.split(" ");
            for (int i : rankedIndices) {
                int indexFound = -1;
                for (String searchWord : searchedWords) {
                    indexFound = plainTexts.get(i).indexOf(searchWord);
                    if (indexFound > -1) {
                        break;
                    }
                }
                if (indexFound == -1)
                    indexFound = 0;

                if (plainTexts.get(i).length() - indexFound < 400) {
                    plainTextReturnde.add(plainTexts.get(i).substring(indexFound, plainTexts.get(i).length()) + "....");
                } else {
                    plainTextReturnde.add(plainTexts.get(i).substring(indexFound, indexFound + 400) + "....");
                }
            }


            for (int i : rankedIndices) {
                documentstitlesReturned.add(documentstitles.get(i));
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        //Image search
        for (String str : urls) {
            ImageSearch im = new ImageSearch();
            im.extractImage(str, searchSentence);
        }

        urlsReturned =urls;
        documentstitlesReturned.removeAll(Arrays.asList("", null));
        plainTextReturnde.removeAll(Arrays.asList("", null));
        urlsReturned.removeAll(Arrays.asList("", null));
        System.out.println(urlsReturned.size());
        System.out.println(rankedIndices.size());
        System.out.println(rankedIndices);
        System.out.println(IndicesIndexer);
        System.out.println(ranker.rankIndices(dataa, urlsFromIndxer));
        System.out.println(dataa.documentsName);
        System.out.println(gg);
        System.out.println(ranker.rankIndices(dataa, urlsFromIndxer));

        String imagesPage = "D:\\downloads\\apache-tomcat-9.0.36\\webapps\\ROOT\\Numbers.html";
        String page = "";
        if ((request.getParameter("SearchInput")) != null || (request.getParameter("AllInput")) != null) {
            page =  getPage(viewSearchSentence, urls, documentstitlesReturned, plainTextReturnde);
        } else if ((request.getParameter("ImagesInput")) != null){
            page = getPageImage(viewSearchSentence, urls, documentstitlesReturned, plainTextReturnde);
        }
        response.getWriter().println(page);
    }

    // to read images
    // File representing the folder that you select using a FileChooser
    static final File dir = new File("D:\\downloads\\apache-tomcat-9.0.36\\webapps\\ROOT\\Images");

    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    String getPage(String viewSearchSentence, List<String> urlsReturned, List<String> documentstitlesReturned, List<String> plainTextReturnde){
        String page = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "    <!-- for the browser icon -->\n" +
                "    <link rel=\"icon\" href=\"https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Google-512.png\">\n" +
                "    <!-- for bootstrap -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.6.3/css/all.css\" integrity=\"sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/\" crossorigin=\"anonymous\"></head>\n" +
                "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n" +
                "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .upper-part{\n" +
                "            padding-right: 5%;\n" +
                "            padding-left: 5%;\n" +
                "        }\n" +
                "\n" +
                "        .list-results{\n" +
                "            padding: 5%;\n" +
                "            padding-top: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .result-item{\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .left-part{ \n" +
                "            float: left;\n" +
                "            width: 20%;\n" +
                "        }\n" +
                "\n" +
                "        .right-part{\n" +
                "            padding-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        #google-logo{\n" +
                "            float: left;\n" +
                "            width: 120px;\n" +
                "            padding: 20px;\n" +
                "            padding-left: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "    \n" +
                "    <title>" + viewSearchSentence + "</title>\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <div class=\"upper-part\">\n" +
                "            <!-- left part -->\n" +
                "            <div class=\"left-image\">\n" +
                "                <img id=\"google-logo\" src=\"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\">\n" +
                "            </div>\n" +
                "\n" +
                "<!-- right part -->\n" +
                "            <div class=\"right-part\">\n" +
                "                <!-- search bar -->\n" +
                "                <div class=\"search-bar\">\n" +
                "                    <form action=\"SearchSentence\" method=\"GET\" id=\"SearchSentence\" class=\"form-inline\">\n" +
                "                        <div class=\"form-group mx-sm-3 mb-2\">\n" +
                "                          <label for=\"inputText2\" class=\"sr-only\">Search</label>\n" +
                "                          <input type=\"text\" class=\"form-control shadows\" id=\"inputText2\" placeholder=\"\" name=\"SearchSentenceInput\" style=\"width: 1000px;\" value=\"" + viewSearchSentence + "\">\n" +
                "                          <div class=\"input-group-prepend\">\n" +
                "                          </div>\n" +
                "                        </div>\n" +
                "                        <input type=\"submit\" class=\"btn btn-primary mb-2\" name=\"SearchInput\" id=\"SearchInput\" value=\"Search\" />\n" +
                "\n" +
                "                        <!-- voice recognition button -->\n" +
                "                        <button type=\"submit\" class=\"btn btn-primary mb-2\" id=\"voiceButton\" title=\"requires chrome and mic access\" style=\"margin-left: 2px;\">Voice Search</button>\n" +
                "                        <!-- options buttons -->\n" +
                "                        <div class=\"options-buttons\">\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"AllInput\" id=\"AllInput\" value='All'/>\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"ImagesInput\" id=\"ImagesInput\" value='Images'/>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-caret-right-square\"></i> Videos</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-newspaper\"></i> News</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-book\"></i> Books</button>\n" +
                "                        </div>\n" +
                "                    </form>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            \n" +
                "\n" +
                "        </div>\n" +
                "        <br>\n" +
                "        <hr class=\"style17\">" +
                "\n" +
                "        <div class=\"list-results\">\n" +
                "            <p class=\"text-muted\" style=\"padding-left: 20px;\">About  " + urlsReturned.size() + " results</p>\n";
        for (int j = 0; j < documentstitlesReturned.size(); ++j) {
            Random rand = new Random();
            int randomNum = rand.nextInt((10 - 0) + 1) + 0;
            page +=
                    "            <div class=\"list-group\">\n" +
                            "                <a href=\"" + urlsReturned.toArray()[j] + "\" class=\" list-group-item-action flex-column align-items-start result-item\">\n" +
                            "                  <small class=\"text-muted\">" + urlsReturned.toArray()[j] + "</small>\n" +
                            "                  <div class=\"d-flex w-100 justify-content-between\">\n" +
                            "                    <h5 class=\"mb-1\" style=\"color: blue;\">" + documentstitlesReturned.toArray()[j] + "</h5>\n" +
                            "                    <small class=\"text-muted\">" + (j * 2 + 1)%12 + " days</small>\n" +
                            "                  </div>\n" +
                            "                  <p class=\"mb-1\">" + plainTextReturnde.toArray()[j] + "</p>\n" +
                            "                </a>\n" +
                            "            </div>\n";
        }
        page +=
                "        </div>\n" +
                        "\n" +
                        "        <script>\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadows\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow-sm').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow-sm');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });\n" +
                        "// for multiple images\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadowss\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });" +
                        "            // voice recognition\n" +
                        "            $( document ).ready(function() {\n" +
                        "                  console.log( \"ready!\" );\n" +
                        "                  try \n" +
                        "              {\n" +
                        "                  var SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;\n" +
                        "                  var recognition = new SpeechRecognition();\n" +
                        "              }\n" +
                        "              catch(e) \n" +
                        "              {\n" +
                        "                  console.error(e);\n" +
                        "                  $('.no-browser-support').show();\n" +
                        "                  $('.app').hide();\n" +
                        "              }\n" +
                        "\n" +
                        "              var inputText = document.getElementById('inputText2')\n" +
                        "              var voiceButton = document.getElementById('voiceButton')\n" +
                        "              console.log(inputText)\n" +
                        "              console.log(voiceButton)\n" +
                        "              var content = ''\n" +
                        "              recognition.continuous = false;\n" +
                        "              recognition.onresult = function (event)\n" +
                        "              {\n" +
                        "                  var current = event.resultIndex;\n" +
                        "                  var transcript = event.results[current][0].transcript;\n" +
                        "                  var mobileRepeatBug = (current == 1 && transcript == event.results[0][0].transcript);\n" +
                        "                  if(!mobileRepeatBug)\n" +
                        "                  {\n" +
                        "                      inputText.value = transcript\n" +
                        "                      recognition.stop()\n" +
                        "                  }\n" +
                        "              };\n" +
                        "\n" +
                        "              voiceButton.onclick = function () {voiceButtonClicked()}\n" +
                        "\n" +
                        "              function voiceButtonClicked()\n" +
                        "              {\n" +
                        "                  recognition.start()\n" +
                        "              }\n" +
                        "\n" +
                        "              });\n" +
                        "        </script>\n" +
                        "        \n" +
                        "    </body>\n" +
                        "\n" +
                        "</html>";
        return page;
    }

    String getPageImage(String viewSearchSentence, List<String> urlsReturned, List<String> documentstitlesReturned, List<String> plainTextReturnde){
        String page = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "    <!-- for the browser icon -->\n" +
                "    <link rel=\"icon\" href=\"https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Google-512.png\">\n" +
                "    <!-- for bootstrap -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.6.3/css/all.css\" integrity=\"sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/\" crossorigin=\"anonymous\"></head>\n" +
                "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n" +
                "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .upper-part{\n" +
                "            padding-right: 5%;\n" +
                "            padding-left: 5%;\n" +
                "        }\n" +
                "\n" +
                "        .list-results{\n" +
                "            padding: 5%;\n" +
                "            padding-top: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .result-item{\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .left-part{ \n" +
                "            float: left;\n" +
                "            width: 20%;\n" +
                "        }\n" +
                "\n" +
                "        .right-part{\n" +
                "            padding-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        #google-logo{\n" +
                "            float: left;\n" +
                "            width: 120px;\n" +
                "            padding: 20px;\n" +
                "            padding-left: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "    \n" +
                "    <title>" + viewSearchSentence + "</title>\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <div class=\"upper-part\">\n" +
                "            <!-- left part -->\n" +
                "            <div class=\"left-image\">\n" +
                "                <img id=\"google-logo\" src=\"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\">\n" +
                "            </div>\n" +
                "\n" +
                "<!-- right part -->\n" +
                "            <div class=\"right-part\">\n" +
                "                <!-- search bar -->\n" +
                "                <div class=\"search-bar\">\n" +
                "                    <form action=\"SearchSentence\" method=\"GET\" id=\"SearchSentence\" class=\"form-inline\">\n" +
                "                        <div class=\"form-group mx-sm-3 mb-2\">\n" +
                "                          <label for=\"inputText2\" class=\"sr-only\">Search</label>\n" +
                "                          <input type=\"text\" class=\"form-control shadows\" id=\"inputText2\" placeholder=\"\" name=\"SearchSentenceInput\" style=\"width: 1000px;\" value=\"" + viewSearchSentence + "\">\n" +
                "                          <div class=\"input-group-prepend\">\n" +
                "                          </div>\n" +
                "                        </div>\n" +
                "                        <input type=\"submit\" class=\"btn btn-primary mb-2\" name=\"ImagesInput\" id=\"SearchInput\" value=\"Search\" />\n" +
                "\n" +
                "                        <!-- voice recognition button -->\n" +
                "                        <button type=\"submit\" class=\"btn btn-primary mb-2\" id=\"voiceButton\" title=\"requires chrome and mic access\" style=\"margin-left: 2px;\">Voice Search</button>\n" +
                "                        <!-- options buttons -->\n" +
                "                        <div class=\"options-buttons\">\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"AllInput\" id=\"AllInput\" value='All'/>\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"ImagesInput\" id=\"ImagesInput\" value='Images'/>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-caret-right-square\"></i> Videos</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-newspaper\"></i> News</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-book\"></i> Books</button>\n" +
                "                        </div>\n" +
                "                    </form>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            \n" +
                "\n" +
                "        </div>\n" +
                "        <br>\n" +
                "        <hr class=\"style17\">" +
                "\n" +
                "        <div class=\"list-results\">\n" +
                "            <p class=\"text-muted\" style=\"padding-left: 20px;\">About  " + urlsReturned.size() + " results</p>\n";
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
                BufferedImage img = null;

                try {
                    img = ImageIO.read(f);

                    // you probably want something more involved here
                    // to display in your UI
                    page += "<img src=\"" + "images" + "\\" + f.getName() + "\" style=\"height: 200px; padding: 5px;\" class=\"shadowss\">";
                } catch (final IOException e) {
                    // handle errors here
                }
            }
        }
        page +=
                "        </div>\n" +
                        "\n" +
                        "        <script>\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadows\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow-sm').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow-sm');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });\n" +
                        "// for multiple images\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadowss\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });" +
                        "            // voice recognition\n" +
                        "            $( document ).ready(function() {\n" +
                        "                  console.log( \"ready!\" );\n" +
                        "                  try \n" +
                        "              {\n" +
                        "                  var SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;\n" +
                        "                  var recognition = new SpeechRecognition();\n" +
                        "              }\n" +
                        "              catch(e) \n" +
                        "              {\n" +
                        "                  console.error(e);\n" +
                        "                  $('.no-browser-support').show();\n" +
                        "                  $('.app').hide();\n" +
                        "              }\n" +
                        "\n" +
                        "              var inputText = document.getElementById('inputText2')\n" +
                        "              var voiceButton = document.getElementById('voiceButton')\n" +
                        "              console.log(inputText)\n" +
                        "              console.log(voiceButton)\n" +
                        "              var content = ''\n" +
                        "              recognition.continuous = false;\n" +
                        "              recognition.onresult = function (event)\n" +
                        "              {\n" +
                        "                  var current = event.resultIndex;\n" +
                        "                  var transcript = event.results[current][0].transcript;\n" +
                        "                  var mobileRepeatBug = (current == 1 && transcript == event.results[0][0].transcript);\n" +
                        "                  if(!mobileRepeatBug)\n" +
                        "                  {\n" +
                        "                      inputText.value = transcript\n" +
                        "                      recognition.stop()\n" +
                        "                  }\n" +
                        "              };\n" +
                        "\n" +
                        "              voiceButton.onclick = function () {voiceButtonClicked()}\n" +
                        "\n" +
                        "              function voiceButtonClicked()\n" +
                        "              {\n" +
                        "                  recognition.start()\n" +
                        "              }\n" +
                        "\n" +
                        "              });\n" +
                        "        </script>\n" +
                        "        \n" +
                        "    </body>\n" +
                        "\n" +
                        "</html>";
        return page;
    }
}

