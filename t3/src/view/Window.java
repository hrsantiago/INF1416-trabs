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

	private User currentUser;

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
					currentUser = manager.getUser(userId);

					if(currentUser.isBlocked()) {
						JOptionPane.showMessageDialog(null, "Usuario com acesso bloqueado temporariamente.");
						currentUser = null;
						return;
					}

					JOptionPane.showMessageDialog(null, "Login OK.");
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
		User.TanValue tanValue = currentUser.getTanValue();

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

		p.add(new JLabel("One time password #" + tanValue.index));

		JTextField passwordField = new JTextField();
		p.add(passwordField);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = passwordField.getText();
				if(password.equals(tanValue.password)) {
					JOptionPane.showMessageDialog(null, "Senha correta!");
					destroyTanListPanel();
					setVisible(false);
					
					RestrictedArea restrict = new RestrictedArea(currentUser, Window.this);
					restrict.show();
				} else {
					currentUser.addPasswordError();
					if(currentUser.isBlocked()) {
						currentUser = null;
						JOptionPane.showMessageDialog(null, "Usuario bloqueado!");
						destroyTanListPanel();
						createLoginPanel();
						setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, "Senha incorreta, tentativas sobrando: " + String.valueOf(User.MAX_ERRORS-currentUser.getPasswordError()));
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
			if(currentUser.isPasswordValid(s)) {
				passOk = true;
				break;
			}
		}
		destroyDigitalKeyboard();

		if(passOk) {
			createTanListPanel();
			setVisible(true);
			System.out.println("Senha correta!");
			JOptionPane.showMessageDialog(null, "Senha correta!");
		} else {
			currentUser.addPasswordError();
			if(currentUser.isBlocked()) {
				currentUser = null;
				JOptionPane.showMessageDialog(null, "User blocked!");
				createLoginPanel();
				setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "Senha incorreta, tentativas sobrando: " + String.valueOf(User.MAX_ERRORS-currentUser.getPasswordError()));
				createDigitalKeyboard();
			}
		}
	}
	
	public void onRestrictedAreaExit() {
		currentUser = null;
		createLoginPanel();
		setVisible(true);
	}
}