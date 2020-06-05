package QueryProcessor;

import Data.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryProcessor {

    public static String searchSentence;
    public List<String> documentsName;

    public QueryProcessor(String searchSentence, List<String> documentsName){
        this.searchSentence = searchSentence;
        this.documentsName =new ArrayList<String>();
        this.documentsName = documentsName;
    }

    public void extractSimilarWords(){
        String[] arr = searchSentence.split(" ");

        for ( String ss : arr) {
            Data.originalWord.add(ss);
            System.out.println("word is " + ss);
            for (int i = 0; i < documentsName.size(); ++i){
                try {
                    String path = "E:\\Study\\2nd Semester\\APT\\Eclipse\\search_engine\\html2\\";
                    File myObj = new File(path + documentsName.get(i));
                    Scanner myReader = new Scanner(myObj);
//				  System.out.println("read a file");
                    while (myReader.hasNextLine()) {
                        String line = myReader.nextLine();

                        irrespective(line, ss);
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

        }

    }

    ////// functions

    // Java regex to match word irrespective of boundaries
    public void irrespective(String data1, String regex){
//        String data1 = "Searching in words : java javap myjava myjavaprogram";
//
//        String regex = "java";

        data1 = data1.toLowerCase();
        regex = regex.toLowerCase();

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data1);
        while (matcher.find())
        {
//            System.out.print("Start index: " + matcher.start());
//            System.out.print(" End index: " + matcher.end() + " ");
//            System.out.println(matcher.group());

            String result = "";
            char quotations[] = {' ', ',', ';', '.', '\'', '\"', '/', '-', '_', ':', '<', '>', '[', ']', '=', '+', ')', '(', '*', '&', '^', '%', '$', '#', '@', '!', '?'};
            String letters = "abcdefghijklmnopqrstuvwxyz";
            int iStart = matcher.start();
            while(true){
                boolean checkQuote = false;
                for (int i = 0; i < quotations.length; ++i)
                    if (data1.charAt(iStart) == quotations[i]){
                        checkQuote = true;
//                        System.out.println(matcher.group() + " " + data1.charAt(iStart)+ " " + quotations[i]);
                        iStart++;
                        break;
                    }
//                boolean checkLetter = false;
//                for (int i = 0; i < letters.length(); ++i){
//                    if (data1.charAt(iStart) == letters.charAt(i)){
//                        checkLetter = true;
//                        break;
//                    }
//
//                }
                if (iStart == 0 || checkQuote)
                {
                    break;
                }

                iStart--;
            }
            int iEnd = matcher.end() - 1;
            while (true){
                boolean checkQuote = false;
                for (int i = 0; i < quotations.length; ++i)
                    if (data1.charAt(iEnd) == quotations[i]){
                        checkQuote = true;
                        iEnd--;
                        break;
                    }
//                boolean checkLetter = false;
//                for (int i = 0; i < letters.length(); ++i){
//                    if (data1.charAt(iStart) == letters.charAt(i)){
//                        checkLetter = true;
//                        break;
//                    }
//
//                }
                if (iEnd == (data1.length() - 1) || checkQuote)
                {
                    break;
                }
                iEnd++;
            }
//            System.out.println(data1.charAt(iStart-1)+data1.charAt(iStart)+data1.charAt(iStart+1)+ " " +data1.charAt(iEnd-1)+data1.charAt(iEnd)+data1.charAt(iEnd+1));
            for (int i = iStart; i < iEnd + 1; ++i){
                if (data1.charAt(i) == ' ')
                    continue;
                result += data1.charAt(i);
            }
//            result = "";
//            for (int i = matcher.start(); i < matcher.end(); ++i)
//                result += data1.charAt(i);
//
            if (!Data.similarWords.contains(result)) {
                System.out.println(result);
                Data.similarWords.add(result);
            }
        }
    }

    // removing quotation marks from string


    // remove Stop words from a String
//    String removeStopWords(String str){
//
//        String data = str;
//        data = data.toLowerCase();
//        data = data.replaceAll("\\p{Punct}", "");
//        stopwords = Files.readAllLines(Paths.get("./assets/stopwords.txt"));
//        stopwordsRegex = stopwords.stream().collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
//        String result  = removeAll();
//        return result;
//
//        String result = "";
//        return result;
//    }


    // remove stop words
//    String setup (String s)throws IOException
//    {
//        data = s;
//        data = data.toLowerCase();
//        data = data.replaceAll("\\p{Punct}", "");
//        stopwords = Files.readAllLines(Paths.get("./assets/stopwords.txt"));
//        stopwordsRegex = stopwords.stream().collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
//        String result  = removeAll();
//        return result;
//    }
//
    public String removeAll(String data) {

        ArrayList<String> allWords =
                Stream.of(data.split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        //stopwords.add(" ");
//        allWords.removeAll(stopwords);
        return allWords.stream().collect(Collectors.joining(" "));
    }
}

