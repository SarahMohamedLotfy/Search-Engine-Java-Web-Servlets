package net.sqlitetutorial;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Connect {
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:\\Users\\hi\\IdeaProjects\\Indexer2\\src\\db\\DataBase.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE WordOfHTMLdocs " +

                    "(docName TEXT  NOT NULL," +
                    " body TEXT NOT NULL, " ;

            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        connect();
    }
}