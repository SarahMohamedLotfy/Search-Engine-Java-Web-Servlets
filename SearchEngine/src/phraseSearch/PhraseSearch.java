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
			System.out.println("no word");
			return false;
		}
	}

	public void countPhrase(){
		// this variable is for knowing this phrase is in each index in the DataFromIndex.occurencesOfWordsCount
		int indexOfPhraseWordInGeneralSearch = 0;
		int countOfPhraseWords = wordsToBeSearch.size();
		// knowing which index from comparing the first word in wordsToBeSearch with its index in searchSentence
		String[] arr = searchSentence.split(" ");

		int searchSentenceWordsCount = arr.length;
		for ( String ss : arr) {
//			  wordsToBeSearch.add(ss);
			if(ss.equals("\"" + 	wordsToBeSearch.get(0))){
//			  	System.out.println(indexOfPhraseWordInGeneralSearch);
				break;
			}
			indexOfPhraseWordInGeneralSearch ++;
		}

		int countOccurrence = 0;

		// we will loop on the first word only of the phrase
		// because if the first word doesn't exist, this means that there is no this phrase in the link
//		  for (int i = indexOfPhraseWordInGeneralSearch * documentsName.size(); i < wordsToBeSearch.size() + indexOfPhraseWordInGeneralSearch; ++i){
		for (int i = indexOfPhraseWordInGeneralSearch; i < documentsName.size(); ++i){
			// search for occurrence
			boolean ifAllWordsExist = true;
			// loop on the links, in the list of occurencesOfWordsCount
//			  for (int j = 0; j < wordsToBeSearch.size(); j++){
//			  	if (occurencesOfWordsCount.get(j + i) == 0){
//			  		ifAllWordsExist = false;
//			  		foundWords[j] = 0;
//			  		break;
//				}
//			  }
//
//			  if (!ifAllWordsExist)
//			  	continue;

			// loop on the links which have the phrase
			// and get the count of occurrence of the whole phrase the link
//			  for (int j = 0; j < wordsToBeSearch.size(); ++j){
			try {
				countOccurrence = 0;
				String path = "E:\\Study\\2nd Semester\\APT\\Eclipse\\search_engine\\html2\\";
				File myObj = new File(path + documentsName.get(i));
				Scanner myReader = new Scanner(myObj);
//				  System.out.println("read a file");
				while (myReader.hasNextLine()) {
					String line = myReader.nextLine();
//					  System.out.println(line);

					boolean isFound = (line.toLowerCase()).contains(searchPhrase.toLowerCase());
					if (isFound) {
						countOccurrence++;
					}

//					  countOccurrence = countOccurences(line, searchPhrase);
					countOccurrence = countPhrase(line, searchPhrase);
				}
				foundWords[i] = countOccurrence;
//				  System.out.println(searchPhrase + " " + documentsName.get(i) + i + " " + countOccurrence + " line length " + lines);
				myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
//			  }
		}

		computeMostRelevantPhraseLinks();

	}

	public void computeMostRelevantPhraseLinks(){
		int[] visited = new int[documentsName.size()];
		int min = 0;
		for (int j = 0; j < documentsName.size(); ++j)
		{
			visited[j] = 0;
			if (foundWords[j] < foundWords[min])
				min = j;
		}
		for (int j = 0; j < documentsName.size(); ++j) {
			int maxIndex = min;
			for (int k = 0; k < documentsName.size(); ++k) {
				if (foundWords[k] > foundWords[maxIndex] && visited[k] == 0)
					maxIndex = k;
			}
			visited[maxIndex] = 1;
			Data.AddFromPhraseSearch(Data.urlsFromIndexer.toArray()[maxIndex].toString());
		}
	}

	// count number of the phrase in a string

	// Count occurrences of a word in string

	static int countOccurences(String str, String word)
	{
		// split the string by spaces in a
		String a[] = str.split(" ");

		// search for pattern in a
		int count = 0;
		for (int i = 0; i < a.length; i++)
		{
			// if match found increase count
			if (word.equals(a[i]))
				count++;
		}

		count = 0;
		for (int i = 0; i < str.length() - word.length(); i++){
			boolean occure = true;
			for (int j = i; j < word.length(); j++){
				if (word.charAt(j) != str.charAt(j)){
					occure = false;
					break;
				}
			}
			if (occure) {
				count++;
				i += word.length() - 1;
			}
		}

		return count;
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