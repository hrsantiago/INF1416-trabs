import core.Manager;

public class LogView
{
	public static void main(String[] args)
	{
		Manager manager = Manager.getInstance();
		if(!manager.initialize())
			return;

		try {
			//manager.loadSchema();
			manager.loadMessages();
			manager.loadRegistries();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				manager.terminate();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
}
