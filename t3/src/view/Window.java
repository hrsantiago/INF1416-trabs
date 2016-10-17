package view;

import core.Manager;
import view.DigitalKeyboard;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import javax.swing.*;


import core.*;

/**
 * Window class
 * Classe auditavel - Logs 10XX, 20XX, 30XX e 40XX
 */
public class Window extends JFrame implements DigitalKeyboardListener, RestrictedAreaExitListener {

	private static final long serialVersionUID = -3739008754324139578L;

	private User m_currentUser;

	private JPanel m_loginPanel;
	private DigitalKeyboard m_digitalKeyboard;
	private JPanel m_tanListPanel;
	
	private Manager m_manager;

	public Window() {
		m_manager = Manager.getInstance();
		setupWindow();
	}

	private void setupWindow() {
		setSize(500, 540);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new FlowLayout());

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);

		createLoginPanel();
	}

	private void createLoginPanel() {
		m_manager.addRegistry(2001);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

		p.add(new JLabel("Login:"));

		JTextField loginField = new JTextField("fulano"); // TODO remove this
		p.add(loginField);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String login = loginField.getText();

				Manager manager = Manager.getInstance();
				int userId = manager.getUserId(login);
				if(userId != -1) {
					m_currentUser = manager.getUser(userId);

					if(m_currentUser.isBlocked()) {
						m_manager.addRegistry(2004, m_currentUser.getId());
						JOptionPane.showMessageDialog(null, "Usuario com acesso bloqueado temporariamente.");
						m_currentUser = null;
						return;
					}
					destroyLoginPanel();
					m_manager.addRegistry(2003, m_currentUser.getId());
					createDigitalKeyboard();
					setVisible(false);
				} else {
					m_manager.addRegistry(2005);
					JOptionPane.showMessageDialog(null, "Login invalido.");
				}
			}
		});
		p.add(loginButton);
		getRootPane().setDefaultButton(loginButton);

		p.setVisible(true);
		add(p);
		m_loginPanel = p;
	}

	private void destroyLoginPanel() {
		m_manager.addRegistry(2002);
		m_loginPanel.setVisible(false);
		remove(m_loginPanel);
		m_loginPanel.removeAll();
		m_loginPanel = null;
	}

	private void createDigitalKeyboard() {
		m_manager.addRegistry(3001, m_currentUser.getId());
		m_digitalKeyboard = new DigitalKeyboard(Window.this);
		m_digitalKeyboard.show();
	}

	private void destroyDigitalKeyboard() {
		m_manager.addRegistry(3002, m_currentUser.getId());
		m_digitalKeyboard.dismiss();
		m_digitalKeyboard = null;
	}

	private void createTanListPanel() {
		m_manager.addRegistry(4001, m_currentUser.getId());
		User.TanValue tanValue = m_currentUser.getTanValue();

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

		p.add(new JLabel("One time password #" + tanValue.index));

		JTextField passwordField = new JTextField(tanValue.password); // TODO: remove this
		p.add(passwordField);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = passwordField.getText();
				if(password.equals(tanValue.password)) {
					m_manager.addRegistry(4003, m_currentUser.getId());
					destroyTanListPanel();
					setVisible(false);

					m_currentUser.useTanValue(tanValue);
					m_currentUser.resetPasswordErrors();
					m_currentUser.setNumAccesses(m_currentUser.getNumAccesses() + 1);
					RestrictedArea restrict = new RestrictedArea(m_currentUser, Window.this);
					restrict.show();
				} else {
					m_currentUser.addPasswordError();
					
					if(m_currentUser.getPasswordErrors() == 1) 
						m_manager.addRegistry(4004, m_currentUser.getId());
					else if (m_currentUser.getPasswordErrors() == 2)
						m_manager.addRegistry(4005, m_currentUser.getId());
					else if (m_currentUser.getPasswordErrors() == 3)
						m_manager.addRegistry(4006, m_currentUser.getId());
					
					if(m_currentUser.isBlocked()) {
						m_manager.addRegistry(4009, m_currentUser.getId());
						m_currentUser = null;
						JOptionPane.showMessageDialog(null, "Usuario bloqueado!");
						destroyTanListPanel();
						createLoginPanel();
						setVisible(true);
					} else {
						int remainingTries = User.MAX_ERRORS - m_currentUser.getPasswordErrors();
						JOptionPane.showMessageDialog(null, "Senha incorreta, tentativas sobrando: " + remainingTries);
						passwordField.setText("");
					}
				}
			}
		});
		p.add(loginButton);
		getRootPane().setDefaultButton(loginButton);
		p.setVisible(true);
		
		add(p);
		m_tanListPanel = p;
	}

	private void destroyTanListPanel() {
		m_manager.addRegistry(4002, m_currentUser.getId());
		m_tanListPanel.setVisible(false);
		remove(m_tanListPanel);
		m_tanListPanel.removeAll();
		m_tanListPanel = null;
	}

	public void onCombinationsPrepared(List<String> combinations) {
		boolean passOk = false;
		for (String s : combinations) {
			if(m_currentUser.isPasswordValid(s)) {
				passOk = true;
				break;
			}
		}
		destroyDigitalKeyboard();

		passOk = true; // TODO: remove this

		if(passOk) {
			m_manager.addRegistry(3003, m_currentUser.getId());
			createTanListPanel();
			setVisible(true);
			System.out.println("Senha correta!");
			m_currentUser.resetPasswordErrors();
		} else {
			m_manager.addRegistry(3004, m_currentUser.getId());
			m_currentUser.addPasswordError();
			
			if(m_currentUser.getPasswordErrors() == 1) 
				m_manager.addRegistry(3005, m_currentUser.getId());
			else if (m_currentUser.getPasswordErrors() == 2)
				m_manager.addRegistry(3006, m_currentUser.getId());
			else if (m_currentUser.getPasswordErrors() == 3)
				m_manager.addRegistry(3007, m_currentUser.getId());

			if(m_currentUser.isBlocked()) {
				m_manager.addRegistry(3008, m_currentUser.getId());
				m_currentUser = null;
				JOptionPane.showMessageDialog(null, "User blocked!");
				createLoginPanel();
				setVisible(true);
			} else {
				int remainingTries = User.MAX_ERRORS - m_currentUser.getPasswordErrors();
				JOptionPane.showMessageDialog(null, "Senha incorreta, tentativas sobrando: " + remainingTries);
				createDigitalKeyboard();
			}
		}
	}
	
	public void onRestrictedAreaExit() {
		m_currentUser = null;
		createLoginPanel();
		setVisible(true);
	}
}