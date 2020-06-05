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

    ////// functions

    public void extractSimilarWords(){
        String[] arr = searchSentence.split(" ");

        for ( String ss : arr) {
            Data.originalWord.add(ss);
            for (int i = 0; i < documentsName.size(); ++i){
                try {
                    String path = "E:\\Study\\2nd Semester\\APT\\Eclipse\\search_engine\\html2\\";
                    File myObj = new File(path + documentsName.get(i));
                    Scanner myReader = new Scanner(myObj);
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

    // Java regex to match word irrespective of boundaries
    public void irrespective(String data1, String regex){

        data1 = data1.toLowerCase();
        regex = regex.toLowerCase();

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data1);
        while (matcher.find())
        {

            String result = "";
            char quotations[] = {' ', ',', ';', '.', '\'', '\"', '/', '-', '_', ':', '<', '>', '[', ']', '=', '+', ')', '(', '*', '&', '^', '%', '$', '#', '@', '!', '?'};
            String letters = "abcdefghijklmnopqrstuvwxyz";
            int iStart = matcher.start();
            while(true){
                boolean checkQuote = false;
                for (int i = 0; i < quotations.length; ++i)
                    if (data1.charAt(iStart) == quotations[i]){
                        checkQuote = true;
                        iStart++;
                        break;
                    }
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
                if (iEnd == (data1.length() - 1) || checkQuote)
                {
                    break;
                }
                iEnd++;
            }
            for (int i = iStart; i < iEnd + 1; ++i){
                if (data1.charAt(i) == ' ')
                    continue;
                result += data1.charAt(i);
            }

            if (!Data.similarWords.contains(result)) {
                Data.similarWords.add(result);
            }
        }
    }

    // removing quotation marks from string

}

