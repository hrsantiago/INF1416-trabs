package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import core.*;

public class RestrictedArea {

	private JFrame frame;
	private User m_currentUser;
	private Manager m_manager; //para fazer log
	private RestrictedAreaExitListener m_exitListener;
	
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
	}
	
	public void show() {
		JMenuBar menu = new JMenuBar();
		JMenu mainMenu = new JMenu("Menu Principal");
		
		JMenuItem cadastroItem = new JMenuItem("Cadastrar usuario");
		cadastroItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Tela de cadastrar usuario
			}
		});
		
		JMenuItem privadaItem = new JMenuItem("Carregar chave privada");
		privadaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Tela de carregar chave privada
			}
		});
		
		JMenuItem consultarItem = new JMenuItem("Consultar diretorio secreto");
		consultarItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Tela de consultar diretorio secreto
			}
		});
		
		JMenuItem sairItem = new JMenuItem("Sair do sistema");
		sairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
	}
	
	

}
