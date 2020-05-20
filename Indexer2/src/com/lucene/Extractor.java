package com.lucene;

import Stemmer.Stemmer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Extractor {


    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:\\Users\\hi\\IdeaProjects\\Indexer2\\src\\db\\DataBaseIndexer.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Succefuly connected");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public void extract_from_html(String htmlPath) throws IOException, SQLException {

        /////  Extractor ///////////
        File dir = new File(htmlPath);
        File[] directoryListing = dir.listFiles();
        int i=1;

        for (File child : directoryListing) {

            String path = child.getAbsolutePath();
            String multihtml = new String(Files.readAllBytes(Paths.get(path)));
            String[] htmlParts = multihtml.split("(?<=</html>)");
            org.jsoup.nodes.Document doc;
            for (String part : htmlParts) {
                doc = Jsoup.parse(part);
                System.out.println("");
                //Title
                String title = "";
                title = doc.title();

                //Remove stop words by stemmer
                Stemmer S = new Stemmer();
                title = S.stem(title);

                System.out.println("title : " + title);
                System.out.println("");
                //Headers
                Element body = doc.body();
                Elements paragraphs = body.getElementsByTag("header");
                String header ="";
                for (Element paragraph : paragraphs) {
                    header = header+paragraph.text();
                    //System.out.println(paragraph.text());
                }

                //Remove stop words
                Stemmer S2 = new Stemmer();
                header = S2.stem(header);

                System.out.println("header:   "+ header);
                // Plain text
                String string = doc.body().text();
                String allBody = string;

                //Remove stop words
                Stemmer S3 = new Stemmer();
                string = S3.stem(string);
                String tempWord = title +" "+ header + " ";
                string = string.replaceAll(tempWord, "");

                Stemmer S4 = new Stemmer();
                allBody = S4.stem(allBody);


                tempWord = " " + title + " " + header;
                string = string.replaceAll(tempWord, "");

                System.out.println("body:  "+string);
                System.out.println("******************************************");

                if ( !title.isEmpty() || !header.isEmpty() ||!string.isEmpty()  ) {
                    // Write in files and database
                    try
                    {
                        String htmlFileName= child.getName();
                        String[] allbodyy = allBody.split(" ");
                        Arrays.sort(allbodyy, String.CASE_INSENSITIVE_ORDER);
                        allBody = Arrays.toString(allbodyy);
                        String sql = "INSERT INTO Words_in_htmldocs (Name,body) VALUES(?,?)";
                        try (Connection conn = this.connect();
                             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, htmlFileName);
                            pstmt.setString(2,allBody);
                            pstmt.executeUpdate();
                            System.out.println("Successfully Wrote to table");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        //Make words table
                        String sql2 = "INSERT INTO Word (DocNumber,WordName, HTMLDocs,,NoOfDocuments,Position) VALUES(?,?,?,?,?)";
                        for(int k =0;k<allbodyy.length;k++)
                        {
                            try (Connection conn = this.connect();
                                 PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                                pstmt.setInt(1, i);
                                pstmt.setString(2,allbodyy[k]);
                                pstmt.setString(3, htmlFileName);
                                pstmt.setString(4,allBody);
                                pstmt.setString(5, htmlFileName);
                                pstmt.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        }


                        String filename = "filename" + Integer.toString(i) + ".txt";
                        FileWriter myWriter = new FileWriter(filename);
                        myWriter.write(title + " , " + header + " , " + string);
                        myWriter.close();
                        System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
            }
            i++;
        }
    }
}
