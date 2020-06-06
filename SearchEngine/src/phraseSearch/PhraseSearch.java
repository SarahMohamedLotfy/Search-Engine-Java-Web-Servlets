package phraseSearch;

import Data.Data;

import java.util.ArrayList;
import java.util.List;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class PhraseSearch {
	public static String searchSentence;
	public static String searchPhrase;
	public static List<String> wordsToBeSearch;
	public List<Integer> occurencesOfWordsCount;
	public List<String>  documentsName;
	public static int[] foundWords;
	final String PATH = System.getProperty("user.dir") + "\\html2\\";

	public PhraseSearch(String searchSentence, List<Integer> occurencesOfWordsCount, List<String>  documentsName) {
		this.searchSentence = searchSentence;
		wordsToBeSearch = new ArrayList<String>();
		this.occurencesOfWordsCount=new ArrayList<Integer>();
		this.occurencesOfWordsCount = occurencesOfWordsCount;
		this.documentsName =new ArrayList<String>();
		this.documentsName = documentsName;
		this.foundWords = new int[documentsName.size()];
	}

	public boolean checkPhraseSearch() {

		boolean quotMarks = searchSentence.contains("\"");
		String searchWord = "";

		int count = 0;
		if (quotMarks) {

			for (int i = 0; i < searchSentence.length(); ++i) {
				if (searchSentence.charAt(i) == '"') {

					count ++;
					i++;
				}
				if (count == 1) {
					searchWord += searchSentence.charAt(i);
				}
				if (count == 2 && i < searchSentence.length() - 1){
					return false;
				}
				if (count == 2 && i == searchSentence.length() - 1){
					return true;
				}
			}
		}
		if (count >= 2) {
			searchPhrase = searchWord;
			// split the sentence phrase to put each word in its index in the List<String>
			String[] arr = searchWord.split(" ");

			for ( String ss : arr) {
				wordsToBeSearch.add(ss);
			}

			return true;

		}
		else {
			return false;
		}
	}

	public void countPhrase() {
		// this variable is for knowing this phrase is in each index in the DataFromIndex.occurencesOfWordsCount

		int countOccurrence = 0;

		// we will loop on the first word only of the phrase
		// because if the first word doesn't exist, this means that there is no this phrase in the link
		for (int i = 0; i < wordsToBeSearch.size(); ++i) {
			// search for occurrence


			try {
				countOccurrence = 0;
				File myObj = new File(PATH + documentsName.get(i));
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {
					String line = myReader.nextLine();

					countOccurrence = countPhrase(line, searchPhrase);
				}
				foundWords[i] = countOccurrence;
				Data.countPhraseSearch.add(countOccurrence);
				myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}


	}

	public int countPhrase(String doc, String word){
		int count = 0;

		String a[] = doc.split("/");
		for (int i = 0; i < a.length; i++)
		{
			// if match found increase count
			if (a[i].toLowerCase().contains(word.toLowerCase()))
				count++;
		}

		return count;
	}


}