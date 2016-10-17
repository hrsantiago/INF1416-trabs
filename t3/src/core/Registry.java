package core;

public class Registry
{

	private Message m_message;
	private String m_userLogin;
	private String m_filename;
	private String m_created;

	void setMessage(Message message) { m_message = message; }
	void setUserLogin(String userLogin) { m_userLogin = userLogin; }
	void setFilename(String filename) { m_filename = filename; }
	void setCreated(String created) { m_created = created; }

	Message getMessage() { return m_message; }
	String getUser() { return m_userLogin; }
	String getFilename() { return m_filename; }
	String getCreated() { return m_created; }

	public String getText()
	{
		String ret = "[" + m_created.toString() + "]: ";
		ret += m_message.getText(m_userLogin, m_filename);
		return ret;
	}

	
}
