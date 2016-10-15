package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import core.*;

public class NewUserPanel extends JPanel {

	private static final long serialVersionUID = -7871019104393440384L;

	private User m_currentUser;
	private String m_keyPath;
	
	private JTextField m_nameField;
	private JTextField m_loginField;
	private JComboBox<String> m_groupCombo;
	private JPasswordField m_passwordField;
	private JPasswordField m_confirmPassField;
	private JButton fileChooseButton;
	
	private PanelCloseListener m_pcl;
	
	private Manager m_manager;
	
	public NewUserPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		
		m_manager = Manager.getInstance();
		
		preparePanel();
		
		m_manager.addRegistry(6001, m_currentUser.getId());
	}
	
	private void preparePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(new JLabel("Nome do usuario:"));
		m_nameField = new JTextField();
		add(m_nameField);
		
		add(new JLabel("Nome de login:"));
		m_loginField = new JTextField();
		add(m_loginField);
		
		add(new JLabel("Grupo:"));
		String[] groups = {Group.USER, Group.ADMIN};
		m_groupCombo = new JComboBox<String>(groups);
		add(m_groupCombo);
		
		add(new JLabel("Senha (BA BE BO CA CE CO DA DE DO FA FE FO GA GE GO):"));
		m_passwordField = new JPasswordField();
		add(m_passwordField);
		
		add(new JLabel("Confirmacao da senha:"));
		m_confirmPassField = new JPasswordField();
		add(m_confirmPassField);
		
		add(new JLabel("Chave privada:"));
		fileChooseButton = new JButton("Selecionar arquivo");
		fileChooseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println(selectedFile.getPath());
					if(!selectedFile.getName().endsWith(".key")){
						m_manager.addRegistry(6003, m_currentUser.getId());
						JOptionPane.showMessageDialog(null, "Arquivo invalido, deve ser uma chave privada .key");
					} else {
						JOptionPane.showMessageDialog(null, "Arquivo OK");
						m_keyPath = selectedFile.getPath();
					}
		        }
			}
		});
		add(fileChooseButton);
		
		JButton addUserButton = new JButton("Cadastrar");
		addUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(6002, m_currentUser.getId());

				if(validateFormNewUser()){
					User newUser = new User(
						m_nameField.getText(), 
						m_loginField.getText(), 
						(String) m_groupCombo.getSelectedItem(), 
						new String(m_passwordField.getPassword()),
						m_keyPath
					);
					
					if(m_manager.createNewUser(newUser)){
						//TODO: create tanList.txt
						
						JOptionPane.showMessageDialog(null, "Usuario cadastrado");
					} else {
						JOptionPane.showMessageDialog(null, "Falha ao cadastrar usuario");
					}
				}
			}
		});
		
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(6006, m_currentUser.getId());
				m_pcl.onPanelClose();
			}
		});
		
		add(addUserButton);
		add(backButton);
		setVisible(true);
	}
	
	private boolean validateFormNewUser(){
		String pass = new String(m_passwordField.getPassword());
		String conf = new String(m_confirmPassField.getPassword());
		
		if(m_nameField.getText().length() < 6){
			showErrorMessage("Nome de usuario muito curto.");
			return false;
		} else if (m_nameField.getText().length() > 50){
			showErrorMessage("Nome de usuario muito longo.");
			return false;
		} else if (m_loginField.getText().length() < 6){
			showErrorMessage("Login muito curto.");
			return false;
		} else if (m_loginField.getText().length() > 50){
			showErrorMessage("Login muito longo.");
			return false;
		} else if (!pass.equals(conf)){
			showErrorMessage("Senhas nao batem.");
			return false;
		} else if (m_keyPath == null){
			showErrorMessage("Chave privada nao encontrada.");
			return false;
		} 
		
		m_manager.addRegistry(6004, m_currentUser.getId());
		return true;
	}
	
	private void showErrorMessage(String msg) {
		m_manager.addRegistry(6005, m_currentUser.getId());
		JOptionPane.showMessageDialog(null, msg);
	}
}
