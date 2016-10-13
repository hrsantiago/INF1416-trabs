package core;

public class Registry
{

	private Message m_message;
	private User m_user;
	private String m_filename;
	private String m_created;

	void setMessage(Message message) { m_message = message; }
	void setUser(User user) { m_user = user; }
	void setFilename(String filename) { m_filename = filename; }
	void setCreated(String created) { m_created = created; }

	Message getMessage() { return m_message; }
	User getUser() { return m_user; }
	String getFilename() { return m_filename; }
	String getCreated() { return m_created; }

	public String getText()
	{
		String ret = "[" + m_created.toString() + "]: ";
		ret += m_message.getText(m_user, m_filename);
		return ret;
	}

	
}
