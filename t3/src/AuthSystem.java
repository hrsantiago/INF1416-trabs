import core.Manager;

public class AuthSystem
{
  public static void main(String[] args)
  {
    Manager manager = Manager.getInstance();
    if(!manager.initialize())
      return;

    try {
      //manager.loadSchema();
      manager.addRegistry(1001,-1,"");
      manager.loadGroups();

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
