package core;

public class Group
{
	public static final String ADMIN = "Administrador";
	public static final String USER = "Usuario";
	
	private String m_name;
	private int m_groupId;
	
	public Group() {}
	public Group(String name){
		m_name = name;
		if(name.equals(ADMIN))
			m_groupId = 1;
		else
			m_groupId = 2;
	}
	
	public void setId(int id) { m_groupId = id; }

	public int getId() { return m_groupId; }	

	public void setName(String name) { m_name = name; }

	public String getName() { return m_name; }	
}
