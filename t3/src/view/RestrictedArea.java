package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import core.*;

public class RestrictedArea implements PanelCloseListener {

	private JFrame frame;
	private User m_currentUser;
	private Manager m_manager; //para fazer log
	private RestrictedAreaExitListener m_exitListener;
	
	private NewUserPanel m_newUserPanel;
	private JPanel m_uploadKeyPanel;
	private JPanel m_secretFilesPanel;
	
	public RestrictedArea(User user, RestrictedAreaExitListener exitListener){
		m_currentUser = user;
		m_manager = Manager.getInstance();
		m_exitListener = exitListener;
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
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	
	public void show() {
		JMenuBar menu = new JMenuBar();
		JMenu mainMenu = new JMenu("Menu Principal");
		
		JMenuItem cadastroItem = new JMenuItem("Cadastrar usuario");
		cadastroItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5002, m_currentUser.getId());
				showNewUserPanel();
			}
		});
		
		JMenuItem privadaItem = new JMenuItem("Carregar chave privada");
		privadaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5003, m_currentUser.getId());
				showUploadKeyPanel();
			}
		});
		
		JMenuItem consultarItem = new JMenuItem("Consultar diretorio secreto");
		consultarItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				m_manager.addRegistry(5004, m_currentUser.getId());
				showSecretFilesPanel();
			}
		});
		
		JMenuItem sairItem = new JMenuItem("Sair do sistema");
		sairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(5005, m_currentUser.getId());
				frame.setVisible(false);
				frame.dispose();
				m_exitListener.onRestrictedAreaExit();
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
		
		frame.setJMenuBar(menu);
		frame.setVisible(true);
		
		m_manager.addRegistry(5001, m_currentUser.getId());
	}
	
	public void onPanelClose() {
		clearScreen();
	}
	
	private void clearScreen() {
		if (m_newUserPanel != null) {
			m_newUserPanel.setVisible(false);
			m_newUserPanel.removeAll();
			frame.remove(m_newUserPanel);
			m_newUserPanel = null;
		}
		
		if (m_uploadKeyPanel != null) {
			m_uploadKeyPanel.setVisible(false);
			m_uploadKeyPanel.removeAll();
			frame.remove(m_uploadKeyPanel);
			m_uploadKeyPanel = null;
		}
		
		if (m_secretFilesPanel != null) {
			m_secretFilesPanel.setVisible(false);
			m_secretFilesPanel.removeAll();
			frame.remove(m_secretFilesPanel);
			m_secretFilesPanel = null;
		}
	}
	
	
	private void showNewUserPanel() {
		m_newUserPanel = new NewUserPanel(m_currentUser, this);
		frame.add(m_newUserPanel);
	}
	
	private void showUploadKeyPanel() {
		//TODO: implementar panel de upload de chave privada
	}
	
	private void showSecretFilesPanel() {
		//TODO: implementar panel de arquivos secretos
	}

}
