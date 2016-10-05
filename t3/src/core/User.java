package core;

import java.security.*;

public class User
{
  public User()
  {
  }

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

  public boolean isPasswordValid(String plainPassword)
  {
    try {
      byte[] data = (plainPassword + m_salt).getBytes("UTF8");
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(data);
      byte[] digest = messageDigest.digest();
      return m_password.equals(byteToHex(digest));
    }
    catch(Exception e) {
      return false;
    }
  }

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
}
