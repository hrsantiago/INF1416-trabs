package core;

public class Message
{
	private int m_code;
	private String m_text;
	
	public Message(int code, String text)
	{
		m_code = code;
		m_text = text;
	}
	
	public int getCode() { return m_code; }

	public String getText(String userLogin, String filename)
	{
		String result = m_text;
		if(result.contains("<login_name>"))
			result = result.replaceAll("<login_name>", userLogin);
		if(result.contains("<arq_name>"))
			result = result.replaceAll("<arq_name>", filename);
		return result;
	}
}
