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

public class Window extends JFrame implements DigitalKeyboardListener, RestrictedAreaExitListener {

	private static final long serialVersionUID = -3739008754324139578L;

	private User m_currentUser;

	private JPanel m_loginPanel;
	private DigitalKeyboard m_digitalKeyboard;
	private JPanel m_tanListPanel;

	public Window() {
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
						JOptionPane.showMessageDialog(null, "Usuario com acesso bloqueado temporariamente.");
						m_currentUser = null;
						return;
					}
					destroyLoginPanel();
					
					createDigitalKeyboard();
					setVisible(false);
				}
				else
					JOptionPane.showMessageDialog(null, "Login invalido.");
			}
		});
		p.add(loginButton);
		getRootPane().setDefaultButton(loginButton);

		p.setVisible(true);
		add(p);
		m_loginPanel = p;
	}

	private void destroyLoginPanel() {
		m_loginPanel.setVisible(false);
		remove(m_loginPanel);
		m_loginPanel.removeAll();
		m_loginPanel = null;
	}

	private void createDigitalKeyboard() {
		m_digitalKeyboard = new DigitalKeyboard(Window.this);
		m_digitalKeyboard.show();
	}

	private void destroyDigitalKeyboard() {
		m_digitalKeyboard.dismiss();
		m_digitalKeyboard = null;
	}

	private void createTanListPanel() {
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
					destroyTanListPanel();
					setVisible(false);

					m_currentUser.useTanValue(tanValue);
					m_currentUser.resetPasswordErrors();
					RestrictedArea restrict = new RestrictedArea(m_currentUser, Window.this);
					restrict.show();
				} else {
					m_currentUser.addPasswordError();
					if(m_currentUser.isBlocked()) {
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
			createTanListPanel();
			setVisible(true);
			System.out.println("Senha correta!");
			m_currentUser.resetPasswordErrors();
		} else {
			m_currentUser.addPasswordError();
			if(m_currentUser.isBlocked()) {
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