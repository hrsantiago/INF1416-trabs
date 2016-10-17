package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import core.*;

/**
 * RestrictedArea class
 * Classe auditavel - Logs 50XX
 */
public class RestrictedArea implements PanelCloseListener {

	public enum State {
	    MAIN, REGISTER, UPLOAD_KEY, SECRET_FILES, EXIT
	}
	
	private JFrame frame;
	private JLabel m_headerLabel;
	private User m_currentUser;
	private Manager m_manager;
	private RestrictedAreaExitListener m_exitListener;
	
	private NewUserPanel m_newUserPanel;
	private UploadPrivateKeyPanel m_uploadKeyPanel;
	private SecretFilesPanel m_secretFilesPanel;
	private ExitPanel m_exitPanel;
	
	public RestrictedArea(User user, RestrictedAreaExitListener exitListener){
		m_currentUser = user;
		m_manager = Manager.getInstance();
		m_exitListener = exitListener;
		
		m_currentUser.setNumAccesses(m_currentUser.getNumAccesses() + 1);
		m_manager.updateUserCounters(m_currentUser);
		
		frame = new JFrame("Area restrita - " + m_currentUser.getGroup().getName());
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				m_exitListener.onRestrictedAreaExit();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 575);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
		
		m_headerLabel = new JLabel();
		frame.add(m_headerLabel);
	}
	
	public void show() {
		JMenuBar menu = new JMenuBar();
		JMenu mainMenu = new JMenu("Menu Principal");
		
		JMenuItem cadastroItem = new JMenuItem("Cadastrar usuario");
		cadastroItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5002, m_currentUser.getLogin());
				showNewUserPanel();
			}
		});
		
		JMenuItem privadaItem = new JMenuItem("Carregar chave privada");
		privadaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5003, m_currentUser.getLogin());
				showUploadKeyPanel();
			}
		});
		
		JMenuItem consultarItem = new JMenuItem("Consultar diretorio secreto");
		consultarItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5004, m_currentUser.getLogin());
				showSecretFilesPanel();
			}
		});
		
		JMenuItem sairItem = new JMenuItem("Sair do sistema");
		sairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5005, m_currentUser.getLogin());
				showExitPanel();
			}
		});
		
		//Apenas mostra esse item do menu para admins
		if(m_currentUser.getGroup().getName().equals(Group.ADMIN))
			mainMenu.add(cadastroItem);

		mainMenu.add(privadaItem);
		mainMenu.add(consultarItem);
		mainMenu.addSeparator();
		mainMenu.add(sairItem);
		menu.add(mainMenu);
		
		updateHeaderLabel(State.MAIN);
		frame.setJMenuBar(menu);
		frame.setVisible(true);
		
		m_manager.addRegistry(5001, m_currentUser.getLogin());
	}
	
	public void onPanelClose() {
		clearScreen();
	}
	
	public void onClose() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	private void clearScreen() {
		if(m_newUserPanel != null) {
			m_newUserPanel.setVisible(false);
			m_newUserPanel.removeAll();
			frame.remove(m_newUserPanel);
			m_newUserPanel = null;
		}
		
		if(m_uploadKeyPanel != null) {
			m_uploadKeyPanel.setVisible(false);
			m_uploadKeyPanel.removeAll();
			frame.remove(m_uploadKeyPanel);
			m_uploadKeyPanel = null;
		}
		
		if(m_secretFilesPanel != null) {
			m_secretFilesPanel.setVisible(false);
			m_secretFilesPanel.removeAll();
			frame.remove(m_secretFilesPanel);
			m_secretFilesPanel = null;
		}
		
		if(m_exitPanel != null) {
			m_exitPanel.setVisible(false);
			m_exitPanel.removeAll();
			frame.remove(m_exitPanel);
			m_exitPanel = null;
		}
		
		updateHeaderLabel(State.MAIN);
	}
	
	
	private void showNewUserPanel() {
		updateHeaderLabel(State.REGISTER);
		m_newUserPanel = new NewUserPanel(m_currentUser, this);
		frame.add(m_newUserPanel);
	}
	
	private void showUploadKeyPanel() {
		updateHeaderLabel(State.UPLOAD_KEY);
		m_uploadKeyPanel = new UploadPrivateKeyPanel(m_currentUser, this);
		frame.add(m_uploadKeyPanel);
	}
	
	private void showSecretFilesPanel() {
		updateHeaderLabel(State.SECRET_FILES);
		m_secretFilesPanel = new SecretFilesPanel(m_currentUser, this);
		frame.add(m_secretFilesPanel);
	}
	
	private void showExitPanel() {
		updateHeaderLabel(State.EXIT);
		m_exitPanel = new ExitPanel(m_currentUser, this);
		frame.add(m_exitPanel);
	}

	private void updateHeaderLabel(State state) {
		String header = "<html>";
		header += "Login: " + m_currentUser.getLogin() + "<br>";
		header += "Grupo: " + m_currentUser.getGroup().getName() + "<br>";
		header += "Nome: " + m_currentUser.getName() + "<br>";
		header += "<br>";
		if(state == State.MAIN || state == State.EXIT)
			header += "Total de acessos do usuario: " + m_currentUser.getNumAccesses();
		else if(state == State.REGISTER)
			header += "Total de usuarios do sistema: " + m_manager.getUserCount();
		else if(state == State.UPLOAD_KEY)
			header += "Total de listagem do usuario: " + m_currentUser.getNumQueries();
		else if(state == State.SECRET_FILES)
			header += "Total de consultas do usuario: " + m_currentUser.getNumQueries();
		header += "<br>";
		header += "</html>";
		m_headerLabel.setText(header);
	}

}
