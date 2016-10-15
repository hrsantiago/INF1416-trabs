package core;

import core.Group;
import core.Message;
import core.Registry;
import core.User;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;

public class Manager
{
	private static Manager m_instance = null;
	protected Manager() {}

	public static Manager getInstance() {
		if(m_instance == null)
			m_instance = new Manager();
		return m_instance;
	 }

	Connection m_connection = null;
	Map<Integer,User> m_users = new HashMap<Integer,User>();
	Map<Integer,Group> m_groups = new HashMap<Integer,Group>();
	Map<Integer,Message> m_messages = new HashMap<Integer,Message>();
	ArrayList<Registry> m_registries = new ArrayList<Registry>();
	User m_currentUser = null;

	public boolean initialize()
	{
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch(ClassNotFoundException e) {
			System.err.println("SQLITE class not found.");
			return false;
		}

		try {
			getConnection();
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void terminate()
	{
		try {
			if(m_connection != null)
				m_connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException
	{
		if(m_connection == null || m_connection.isClosed())
			m_connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		return m_connection;
	}

	public void loadSchema() throws SQLException, IOException
	{
		Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(30);
		statement.executeUpdate(readFile("schema.sql", StandardCharsets.UTF_8));
	}

	public void loadGroups() throws SQLException
	{
		Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(30);
		ResultSet rs = statement.executeQuery("SELECT * FROM groups");
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			Group group = new Group();
			group.setName(name);
			m_groups.put(id, group);
		}
	}  

	public void loadMessages() throws SQLException
	{
		Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(30);
		ResultSet rs = statement.executeQuery("SELECT * FROM messages");
		while(rs.next()) {
			int code = rs.getInt("id");
			String text = rs.getString("text");
			Message message = new Message(code, text);
			m_messages.put(code, message);
		}
	}

	public void addRegistry(int messageId, int userId, String filename)
	{
		try {
			Statement statement = getConnection().createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate("INSERT INTO registries(message_id,user_id,filename) VALUES("+messageId+","+userId+",'"+filename+"')");
			// TODO escape filename
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void addRegistry(int messageId)
	{
		addRegistry(messageId, -1, null);
	}

	public void addRegistry(int messageId, int userId) 
	{
		addRegistry(messageId, userId, null);
	}

	public void loadRegistries() throws SQLException
	{
		Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(30);
		ResultSet rs = statement.executeQuery("SELECT * FROM registries");
		while(rs.next()) {
			//int code = rs.getInt("id");
			int messageId = rs.getInt("message_id");
			int userId = rs.getInt("user_id");
			String filename = rs.getString("filename");
			String created = rs.getString("created");

			Message message = m_messages.get(messageId);
			Registry registry = new Registry();
			registry.setMessage(message);
			registry.setUser(getUser(userId));
			registry.setFilename(filename);
			registry.setCreated(created);
			m_registries.add(registry);

			System.out.println(registry.getText());
		}
	}

	public void loadUser(int id) throws SQLException
	{
		Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(30);
		ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE id = " + id);
		while(rs.next()) {
			String name = rs.getString("name");
			String login = rs.getString("login");
			int groupId = rs.getInt("group_id");
			String password = rs.getString("password");
			String salt = rs.getString("salt");
			String certificate = rs.getString("certificate");
			String privateKey = rs.getString("private_key");
			String directory = rs.getString("directory");
			int accesses = rs.getInt("num_accesses");
			int queries = rs.getInt("num_queries");

			User user = new User();
			user.setId(id);
			user.setName(name);
			user.setLogin(login);
			user.setGroup(m_groups.get(groupId));
			user.setPassword(password);
			user.setSalt(salt);
			user.setCertificate(certificate);
			user.setPrivateKey(privateKey);
			user.setDirectory(directory);
			user.setNumAccesses(accesses);
			user.setNumQueries(queries);

			m_users.put(id, user);
		}
	}

	public User getUser(int id)
	{
		try {
			User user = m_users.get(id);
			if(user == null) {
				loadUser(id);
				user = m_users.get(id);
			}
			return user;
		} catch(SQLException ex) {
			return null;
		}
		
	}

	public int getUserId(String login)
	{
		try {
			Statement statement = getConnection().createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("SELECT id FROM users WHERE login = '" + login + "'"); // TODO escape login
			while(rs.next()) {
				return rs.getInt("id");
			}
			return -1;
		} catch (SQLException ex) {
			return -1;
		}
	}

	static String readFile(String path, Charset encoding) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public boolean createNewUser(User user){
		try {
			String insertQuery = "INSERT INTO users(name, login, group_id, password, salt,"
					+ "certificate, private_key, directory) VALUES ("
					+ "'" + user.getName() + "',"
					+ "'" + user.getLogin() + "',"
					+ user.getGroup().getId() + ","
					+ "'" + user.getPassword() + "',"
					+ "'" + user.getSalt() + "',"
					+ "'',"
					+ "'" + user.getPrivateKey() + "',"
					+ "'" + user.getDirectory() + "')";
			
			System.out.println("Insert query: " + insertQuery);
			Statement statement = getConnection().createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(insertQuery);
			
			return true;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
