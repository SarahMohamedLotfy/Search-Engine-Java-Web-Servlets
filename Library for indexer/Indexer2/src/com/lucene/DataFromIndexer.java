package com.lucene;
import java.util.ArrayList;
import java.util.List;

public class DataFromIndexer<integer> {

    public List<String>  documentsName ;
    //public List<String> documentsAddresss;
    public List<String> documentsBody;
    public List<Integer> occurencesOfWordsCount;
    public List<Integer> occurencesOfWordsInTitle;
    public List<Integer> occurencesOfWordsInHeader;
    public List<Integer> occurencesOfWordsInPlainText;
    public List<String> images;


    public  DataFromIndexer() {
        documentsName =new ArrayList<String>();
        //documentsAddresss=new ArrayList<String>();
        documentsBody=new ArrayList<String>();
        occurencesOfWordsCount=new ArrayList<Integer>();
        occurencesOfWordsInTitle=new ArrayList<Integer>();
        occurencesOfWordsInHeader=new ArrayList<Integer>();
        occurencesOfWordsInPlainText=new ArrayList<Integer>();
        images=new ArrayList<String>();

    }
    public void SetDataFromIndexer(List<String>  documentsNametemp,List<String> documentsBodyemp,
                           List<Integer> occurencesOfWordsCounttemp,List<Integer> occurencesOfWordsInTitletemp,List<Integer> occurencesOfWordsInHeadertemp,
                           List<Integer> occurencesOfWordsInPlainTexttemp,List<String>  imagestemp) {
        this.documentsName = documentsNametemp;
       // this.documentsAddresss = documentsAddressstemp;
        this.documentsBody = documentsBodyemp;
        this.occurencesOfWordsCount = occurencesOfWordsCounttemp;
        this.occurencesOfWordsInTitle =occurencesOfWordsInTitletemp ;
        this.occurencesOfWordsInHeader= occurencesOfWordsInHeadertemp;
        this.occurencesOfWordsInPlainText = occurencesOfWordsInPlainTexttemp;
        this.images = imagestemp;
    }

}
