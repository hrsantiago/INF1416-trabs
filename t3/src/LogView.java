import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import src.*;

public class LogView
{
  static Map<Integer,Message> m_messages = new HashMap<Integer,Message>();
  static ArrayList<Registry> m_registries = new ArrayList<Registry>();

  static void loadMessages(Statement statement) throws SQLException
  {
    ResultSet rs = statement.executeQuery("select * from messages");
    while(rs.next())
    {
      int code = rs.getInt("id");
      String text = rs.getString("text");
      Message message = new Message(code, text);
      m_messages.put(code, message);
    }
  }

  static void loadRegistries(Statement statement) throws SQLException
  {
    ResultSet rs = statement.executeQuery("select * from registries");
    while(rs.next())
    {
      int code = rs.getInt("id");
      int messageId = rs.getInt("message_id");
      int userId = rs.getInt("user_id");
      String filename = rs.getString("filename");
      int created = rs.getInt("created");

      Message message = m_messages.get(messageId);
      Registry registry = new Registry(message);
      m_registries.add(registry);

      System.out.println(registry.getText());
    }
  }

  static String readFile(String path, Charset encoding) throws IOException
  {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

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
