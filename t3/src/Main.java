package src;

import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException
  {
    Class.forName("org.sqlite.JDBC");

    Connection connection = null;
    try
    {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);

      // TODO: comment this line on release
      statement.executeUpdate(readFile("schema.sql", StandardCharsets.UTF_8));
      loadMessages(statement);
      loadRegistries(statement);
      
    }
    catch(SQLException e)
    {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    catch(IOException e)
    {
      System.err.println(e.getMessage());
    }
    finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        System.err.println(e);
      }
    }
  }
}