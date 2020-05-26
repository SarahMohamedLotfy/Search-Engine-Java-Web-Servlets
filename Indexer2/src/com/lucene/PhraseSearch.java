package com.lucene;

public class PhraseSearch {
	public static String search;
	
	public PhraseSearch(String search) {
		this.search = search;
	}
	
	public static void main(String[] args) {
	    
	    boolean quotMarks = search.contains("\"");
    	String searchWord = "";

    	int count = 0;	
	    if (quotMarks) {
	    	    	
	    	for (int i = 0; i < search.length(); ++i) {
	    		if (search.charAt(i) == '"') {
	    			count ++;
	    			i++;
	    		}
	    		if (count == 1) {
	    			searchWord += search.charAt(i);
	    		}
	    	}
	    }
	    if (count >= 2) {
	    	main.wantToFindWord = true;
	    	main.word = searchWord;
	    }
	    else {
	    	System.out.println("no word");
	    	main.wantToFindWord = false;
	    	main.word = "";
	    }
	  }

}
