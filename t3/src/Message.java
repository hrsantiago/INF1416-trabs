package src;

public class Message
{
  public Message(int code, String text)
  {
    m_code = code;
    m_text = text;
  }

  public String getText()
  {
    String result = m_text; // TODO
    if(result.contains("<login_name>")) {
      String loginName = "USER";
      result = result.replaceAll("<login_name>", loginName);
    }
    if(result.contains("<arq_name>")) {
      String fileName = "FILE";
      result = result.replaceAll("<arq_name>", fileName);
    }
    return result;
  }

  private int m_code;
  private String m_text;
}
