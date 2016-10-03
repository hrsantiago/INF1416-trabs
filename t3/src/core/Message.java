package core;

public class Message
{
  public Message(int code, String text)
  {
    m_code = code;
    m_text = text;
  }

  public String getText(User user, String filename)
  {
    String result = m_text;
    if(result.contains("<login_name>"))
      result = result.replaceAll("<login_name>", user.getLogin());
    if(result.contains("<arq_name>"))
      result = result.replaceAll("<arq_name>", filename);
    return result;
  }

  private int m_code;
  private String m_text;
}
