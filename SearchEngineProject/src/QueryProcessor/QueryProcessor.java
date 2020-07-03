package QueryProcessor;

import Data.Data;
import Stemmer.Stemmer;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryProcessor {

    public static String searchSentence;
    public List<String> documentsName;

    final String PATH = "C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\html2\\";

    public QueryProcessor(String searchSentence){
        this.searchSentence = searchSentence;
        this.documentsName =new ArrayList<String>();
        this.documentsName = documentsName;
    }

    ////// functions

    public String getNewSearchSentence(){

        String result = "";
        Stemmer stemmer = new Stemmer();
        String[] arr = searchSentence.split(" ");

        List<String> searchList = new ArrayList<String>();

        for ( String ss : arr)
            searchList.add(ss);

        for (int i = 0; i < searchList.size(); ++i)
            result += ' ' + stemmer.stem(searchList.get(i));

        searchList.clear();

        String[] arr2 = result.split(" ");

        List<String> allWords = new ArrayList<String>();
        List<String> stemmWords = new ArrayList<String>();
        try {
            FileReader fr = new FileReader("C:\\Users\\hi\\IdeaProjects\\18_Sarah_Mohamed_Ahmed_Lotfy\\SearchEngineProject\\src\\QueryProcessor\\words_alpha.txt");
            BufferedReader br = new BufferedReader(fr);
            char c[] = new char[20];

            String line;
            while ((line = br.readLine()) != null) {
                allWords.add(line);

                // pass to stemmer
                stemmWords.add(stemmer.stem(line));
            }

        } catch (IOException e) {
            System.out.println("error");
        }

        for (int j = 1; j < arr2.length; ++j){
            String ss = arr2[j];

            for (int i = 0; i < allWords.size(); ++i) {
//                Pattern pattern = Pattern.compile(ss, Pattern.CASE_INSENSITIVE);
//                Matcher matcher = pattern.matcher(allWords.get(i));
//                if (matcher.find())
//                {
//                    searchList.add(allWords.get(i));
//                }
                if (stemmWords.get(i).equals(ss)){
                    searchList.add(allWords.get(i));
                }
            }
        }



        result = "";

        result = String.join(" ", searchList);

        return result;
    }

}

