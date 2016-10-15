package core;

public class Group
{
	public static final String ADMIN = "Administrador";
	public static final String USER = "Usuario";
	
	private int m_id;
	private String m_name;
	
	public Group() {}
	public Group(String name){
		m_name = name;
		if(name.equals(ADMIN))
			m_id = 1;
		else
			m_id = 2;
	}
	
	public void setId(int id) { m_id = id; }
	public void setName(String name) { m_name = name; }

	public int getId() { return m_id; }
	public String getName() { return m_name; }	
}
