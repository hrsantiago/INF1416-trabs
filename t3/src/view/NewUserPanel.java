package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.*;

import core.*;

/**
 *  NewUserPanel class
 *  Classe auditavel - Logs 600X
 */
public class NewUserPanel extends JPanel {

	private static final long serialVersionUID = -7871019104393440384L;

	private User m_currentUser;
	private String m_certificatePath;
	
	private JTextField m_nameField;
	private JTextField m_loginField;
	private JComboBox<Group> m_groupCombo;
	private JPasswordField m_passwordField;
	private JPasswordField m_confirmPassField;
	
	private PanelCloseListener m_pcl;
	
	private Manager m_manager;
	
	private X509Certificate signercert;
	
	public NewUserPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		
		m_manager = Manager.getInstance();
		
		preparePanel();
		
		m_manager.addRegistry(6001, m_currentUser.getLogin());
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
		m_groupCombo = new JComboBox<Group>();
		
		Map<Integer,Group> groups = m_manager.getGroups();
		for(Map.Entry<Integer, Group> entry : groups.entrySet()) {
		    Group value = entry.getValue();
		    m_groupCombo.addItem(value);
		}
		add(m_groupCombo);
		
		add(new JLabel("Senha (BA BE BO CA CE CO DA DE DO FA FE FO GA GE GO):"));
		m_passwordField = new JPasswordField();
		add(m_passwordField);
		
		add(new JLabel("Confirmacao da senha:"));
		m_confirmPassField = new JPasswordField();
		add(m_confirmPassField);
		
		add(new JLabel("Certificado digital:"));
		JButton fileChooseButton = new JButton("Selecionar arquivo");
		fileChooseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println(selectedFile.getPath());
					if(!selectedFile.getName().endsWith(".crt")){
						m_manager.addRegistry(6003, m_currentUser.getLogin());
						JOptionPane.showMessageDialog(null, "Arquivo invalido, deve ser um certificado digital .crt");
					} else {
						JOptionPane.showMessageDialog(null, "Arquivo OK");
						m_certificatePath = selectedFile.getPath();
					}
		        }
			}
		});
		add(fileChooseButton);
		
		JButton addUserButton = new JButton("Cadastrar");
		addUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(6002, m_currentUser.getLogin());

				byte[] data;
				try {
					Path path = Paths.get(m_certificatePath);
					data = Files.readAllBytes(path);
					InputStream inStream = new ByteArrayInputStream(data); 
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					signercert = (X509Certificate)cf.generateCertificate(inStream);
				    inStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
					showErrorMessage("Certificado digital inválido");
					return;
				}
				
				if(validateFormNewUser()) {
					try{
						User newUser = m_manager.createNewUser(
							m_nameField.getText(), 
							m_loginField.getText(), 
							(Group)m_groupCombo.getSelectedItem(), 
							new String(m_passwordField.getPassword()),
							new String(data, "UTF-8")
						);
						
						if(newUser != null) {
							try {
								newUser.createTanList();
								newUser.saveTanList("./");
								
								JOptionPane.showMessageDialog(null, newUser.getTanListText());
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							
							JOptionPane.showMessageDialog(null, "Usuario cadastrado");
						} else {
							JOptionPane.showMessageDialog(null, "Falha ao cadastrar usuario");
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Falha ao cadastrar usuario");
						ex.printStackTrace();
					}
				}
			}
		});
		
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(6006, m_currentUser.getLogin());
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
		} else if (!User.checkPasswordIntegrity(pass)) {
			showErrorMessage("Senha invalida, use somente os fonemas aceitos e sem repeti-los.");
			return false;
		} else if (!pass.equals(conf)){
			showErrorMessage("Senhas nao batem.");
			return false;
		} else if (m_certificatePath == null){
			showErrorMessage("Certificado nao encontrado.");
			return false;
		} 
		
		String msg = "Nome do usuario: " + m_nameField.getText() + "\n"
				+ "Login do usuario: " + m_loginField.getText() + "\n"
				+ "Grupo do usuario: " + m_groupCombo.getSelectedItem() + "\n"
				+ "Certificado: \n"
				+ "Versão: " + String.valueOf(signercert.getVersion()) + "\n"
				+ "Serie: " + String.valueOf(signercert.getSerialNumber()) + "\n"
				+ "Validade: " + signercert.getNotAfter().toString() + "\n"
				+ "Tipo assinatura: " + signercert.getSigAlgName() + "\n"
				+ "Emissor: " + signercert.getIssuerX500Principal().getName() + "\n"
				+ "Sujeito: " + signercert.getSubjectX500Principal().getName() + "\n";
		
		if(JOptionPane.showConfirmDialog(null, msg, "Confirmação de dados", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
			m_manager.addRegistry(6004, m_currentUser.getLogin());
			return true;
		} else {
			m_manager.addRegistry(6005, m_currentUser.getLogin());
			return false;
		}
	}
	
	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
