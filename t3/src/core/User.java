package core;

public class User
{
  public User()
  {
  }

  public void setName(String name) { m_name = name; }
  public void setLogin(String login) { m_login = login; }

  public String getName() { return m_name; }
  public String getLogin() { return m_login; }

  private int m_id;
  private String m_name;
  private String m_login;
}
