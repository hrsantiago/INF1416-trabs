package core;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.xml.bind.DatatypeConverter;

import java.sql.*;

public class User
{
	public static final int MAX_ERRORS = 3;
	public static final String[] FONEMAS = {
		"BA", "BE", "BO",
		"CA", "CE", "CO",
		"DA", "DE", "DO",
		"FA", "FE", "FO",
		"GA", "GE", "GO"
	};
	
	private int m_id;
	private String m_name;
	private String m_login;
	private Group m_group;
	private String m_password;
	private String m_salt;
	private String m_certificate;
	private String m_privateKey;
	private String m_directory;
	private int m_numAccesses;
	private int m_numQueries;

	private int m_passwordErrors = 0;
	private long m_blockEnd = 0;

	private ArrayList<TanValue> m_tanList = new ArrayList<TanValue>();

	public class TanValue {
		public int index;
		public String password;
	}
	
	public User(){}
	public User(String name, String login, Group group, String plainPassword, String certificatePath) {
		m_name = name;
		m_login = login;
		m_group = group;
		m_salt = generateSalt();
		m_password = encodePassword(plainPassword);
		m_certificate = certificatePath;
	}

	public void setId(int id) { m_id = id; }
	public void setName(String name) { m_name = name; }
	public void setLogin(String login) { m_login = login; }
	public void setGroup(Group group) { m_group = group; }
	public void setPassword(String password) { m_password = password; }
	public void setSalt(String salt) { m_salt = salt; }
	public void setCertificate(String certificate) { m_certificate = certificate; }
	public void setPrivateKey(String privateKey) { m_privateKey = privateKey; }
	public void setDirectory(String directory) { m_directory = directory; }
	public void setNumAccesses(int accesses) { m_numAccesses = accesses; }
	public void setNumQueries(int queries) { m_numQueries = queries; }

	public int getId() { return m_id; }
	public String getName() { return m_name; }
	public String getLogin() { return m_login; }
	public Group getGroup() { return m_group; }
	public String getPassword() { return m_password; }
	public String getSalt() { return m_salt; }
	public String getCertificate() { return m_certificate; }
	public String getPrivateKey() { return m_privateKey; }
	public String getDirectory() { return m_directory; }
	public int getNumAccesses() { return m_numAccesses; }
	public int getNumQueries() { return m_numQueries; }

	public static String byteToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(0x0100 + (data[i] & 0x00FF)).substring(1);
			buf.append((hex.length() < 2 ? "0" : "") + hex);
		}
		return buf.toString();
	}

	public void addPasswordError() {
		m_passwordErrors++;
		if(m_passwordErrors >= MAX_ERRORS)
			block(2 * 60 * 1000);
	}
	
	public void resetPasswordErrors() {
		m_passwordErrors = 0;
	}

	public int getPasswordErrors() {
		return m_passwordErrors;
	}

	public void block(long duration) {
		m_passwordErrors = 0;
		m_blockEnd = System.currentTimeMillis() + duration;
	}

	public boolean isBlocked() {
		return m_blockEnd != 0 && m_blockEnd >= System.currentTimeMillis();
	}

	public boolean isPasswordValid(String plainPassword) {
		return m_password.equals(encodePassword(plainPassword));
	}
	
	private String generateSalt() {
		SecureRandom rnd = new SecureRandom();
		String chars = "0123456789";

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < 9; i++) 
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		
		return sb.toString();
	}
	
	private String encodePassword(String plainPassword) {
		try {
			byte[] data = (plainPassword + m_salt).getBytes("UTF8");
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(data);
			byte[] digest = messageDigest.digest();
			return byteToHex(digest);
		} catch(Exception ex) {
			return null;
		}
	}

	public static boolean checkPasswordIntegrity(String password) {
		List<String> fonemas = Arrays.asList(FONEMAS);
		HashMap<String, Boolean> fonemaMap = new HashMap<String, Boolean>();
		
		if (password.length() != 6) return false;
		
		for(int i = 0; i < 3; i++){
			String fon = password.substring(2*i, 2*i + 2);
			if (fonemaMap.containsKey(fon))
				return false;
			
			if(fonemas.indexOf(fon) == -1)
				return false;
			
			fonemaMap.put(fon, true);
		}
		
		return true;
	}
	
	public TanValue getTanValue() {
		try {
			if(m_tanList.isEmpty()) {
				if(!loadTanList())
					createTanList();
			}

			SecureRandom rnd = new SecureRandom();
			return m_tanList.get(rnd.nextInt(m_tanList.size()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean useTanValue(TanValue tanValue) {
		Manager manager = Manager.getInstance();
		
		try {
			PreparedStatement statement = manager.getConnection().prepareStatement("UPDATE tanlist SET used = 1 WHERE user_id = ? AND id = ?");
			statement.setQueryTimeout(30);
			statement.setInt(1, m_id);
			statement.setInt(2, tanValue.index);
			int ret = statement.executeUpdate();
			if(ret == 1) {
				m_tanList.remove(tanValue);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean loadTanList() throws SQLException {
		boolean ret = false;
		Manager manager = Manager.getInstance();
		PreparedStatement statement = manager.getConnection().prepareStatement("SELECT * FROM tanlist WHERE used = 0 AND user_id = ?");
		statement.setQueryTimeout(30);
		statement.setInt(1, m_id);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			TanValue tanValue = new TanValue();
			tanValue.index = rs.getInt("id");
			tanValue.password = rs.getString("password");
			m_tanList.add(tanValue);
			ret = true;
		}
		return ret;
	}

	public void createTanList() throws SQLException {
		Manager manager = Manager.getInstance();
		PreparedStatement delstatement = manager.getConnection().prepareStatement("DELETE FROM tanlist WHERE user_id = ?");
		delstatement.setQueryTimeout(30);
		delstatement.setInt(1, m_id);
		delstatement.executeUpdate();

		Statement statement = manager.getConnection().createStatement();
		SecureRandom rnd = new SecureRandom();
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for(int i = 0; i < 10; i++) {
			TanValue tanValue = new TanValue();
			tanValue.index = i+1;
			tanValue.password = "";
			for(int j = 0; j < 4; j++)
				tanValue.password += chars.charAt(rnd.nextInt(chars.length()));

			System.out.println(tanValue.index + " " + tanValue.password);

			m_tanList.add(tanValue);
			statement.addBatch("INSERT INTO tanlist(id,user_id,password) VALUES("+tanValue.index+","+m_id+",'"+tanValue.password+"')");
		}
		statement.executeBatch();
	}
	
	public String getTanListText() {
		String str = "";
		for(int i = 0; i < m_tanList.size(); i++) {
			TanValue tanValue = m_tanList.get(i);
			str += String.format("%d %s\n", i, tanValue.password);
		}
		return str;
	}
	
	public void saveTanList(String path) {
		String filename = m_login + "-tan.txt";
		PrintWriter writer;
		try {
			writer = new PrintWriter(path + "/" + filename, "US-ASCII");
			for(int i = 0; i < m_tanList.size(); i++) {
				TanValue tanValue = m_tanList.get(i);
				writer.printf("%d %s\n", i, tanValue.password);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PrivateKey getPrivateKeyObject(String keyPath, String passphrase) {
		
		try {
			Path pathPrivateKey = Paths.get(keyPath);
			byte[] keybytes = Files.readAllBytes(pathPrivateKey);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
	        random.setSeed(passphrase.getBytes());
	       
	        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
	        keyGen.init(56, random);
	        Key key = keyGen.generateKey();
	       
	        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	       
	        byte[] privKeyBytesBase64 = cipher.doFinal(keybytes);
	        String keyString = new String(privKeyBytesBase64, "UTF8");
	        keyString = keyString.replace("-----BEGIN PRIVATE KEY-----", "");
	        keyString = keyString.replace("-----END PRIVATE KEY-----", "");
	        System.out.println(keyString.trim());
	       
	        byte[] privKeyB = DatatypeConverter.parseBase64Binary(keyString.trim());
	        
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PrivateKey privkey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privKeyB));
	        return privkey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
