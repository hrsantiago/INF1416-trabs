package src;

public class Registry
{
  public Registry(Message message)
  {
    m_message = message;
  }

  public String getText()
  {
    // TODO concat time
    return m_message.getText();
  }

  private Message m_message;
}
