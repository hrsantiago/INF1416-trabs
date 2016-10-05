import core.Manager;
import core.User;
import view.Window;

public class AuthSystem
{
  public static void main(String[] args)
  {
    Manager manager = Manager.getInstance();
    if(!manager.initialize())
      return;

    Window window = new Window();
    window.setTitle("Auth System");
    window.setVisible(true);

    try {
      //manager.loadSchema();
      manager.addRegistry(1001,-1,"");
      manager.loadGroups();

      // test password
      User user = manager.getUser(1);
      System.out.println(user.isPasswordValid("123456"));

      // do stuff

      manager.addRegistry(1002,-1,"");
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
