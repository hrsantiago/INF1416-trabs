import core.Manager;
import view.Window;

public class AuthSystem
{
	public static void main(String[] args)
	{
		Manager manager = Manager.getInstance();
		if(!manager.initialize())
			return;

		try {
			//manager.loadSchema();
			manager.addRegistry(1001);
			manager.loadGroups();

			Window window = new Window();
			window.setTitle("Auth System");
			window.setVisible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		    	manager.addRegistry(1002);
				manager.terminate();
		    }
		});
		
	}
}
