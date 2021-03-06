package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.swing.*;

import core.Manager;
import core.User;

/**
 * UploadPrivateKeyPanel class
 * Classe auditavel - Logs 70XX
 */
public class UploadPrivateKeyPanel extends JPanel {

	private static final long serialVersionUID = -4239328003905582749L;
	
	private JTextField m_passphraseField;
	private User m_currentUser;
	private String m_keyPath;
	
	private Manager m_manager;
	
	private PanelCloseListener m_pcl;
	
	public UploadPrivateKeyPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		
		m_manager = Manager.getInstance();
		
		preparePanel();
		
		m_manager.addRegistry(7001, m_currentUser.getLogin());
	}
	
	private void preparePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(new JLabel("Chave privada:"));
		JButton addFileButton = new JButton("Selecionar arquivo");
		addFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println(selectedFile.getPath());
					if(!selectedFile.getName().endsWith(".key")){
						m_manager.addRegistry(7002, m_currentUser.getLogin());
						JOptionPane.showMessageDialog(null, "Arquivo invalido, deve ser um certificado digital .key");
					} else {
						JOptionPane.showMessageDialog(null, "Arquivo OK");
						m_keyPath = selectedFile.getPath();
					}
		        }
			}
		});
		add(addFileButton);
		
		add(new JLabel("Frase secreta:"));
		m_passphraseField = new JTextField();
		add(m_passphraseField);
		
		JButton addKeyButton = new JButton("Adicionar chave privada");
		addKeyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(m_keyPath == null) {
					JOptionPane.showMessageDialog(null, "Arquivo chave privada não selecionado");
					return;
				}
				if(m_passphraseField.getText().length() == 0){
					JOptionPane.showMessageDialog(null, "Insira a frase secreta");
					return;
				}
				if(testKey()){
					m_currentUser.setPrivateKey(m_keyPath);
					m_manager.updateUserPrivateKey(m_currentUser);
				}
			}
		});
		add(addKeyButton);
		
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(7006, m_currentUser.getLogin());
				m_pcl.onPanelClose();
			}
		});
		
		add(backButton);
		setVisible(true);
	}
	
	private boolean testKey() {
		try {
			PrivateKey privkey = User.getPrivateKeyObject(m_keyPath, m_passphraseField.getText());
			if(privkey == null) {
				m_manager.addRegistry(7003, m_currentUser.getLogin());
				JOptionPane.showMessageDialog(null, "Frase secreta invalida");
				return false;
			}
			
			byte[] testArray = generateTestArray(512);
	
			Signature sig = Signature.getInstance("MD5withRSA");
			sig.initSign(privkey);
			sig.update(testArray);
			byte[] ds = sig.sign();
			
			byte[] pubbytes = m_currentUser.getCertificate().getBytes("UTF-8");
			InputStream inStream = new ByteArrayInputStream(pubbytes); 
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate signercert = (X509Certificate)cf.generateCertificate(inStream);
			
			sig.initVerify(signercert.getPublicKey());
			sig.update(testArray);
			
			if(sig.verify(ds)){
				m_manager.addRegistry(7005, m_currentUser.getLogin());
				JOptionPane.showMessageDialog(null, "Chave validada!");
				return true;
			} else {
				m_manager.addRegistry(7004, m_currentUser.getLogin());
				JOptionPane.showMessageDialog(null, "Chave rejeitada!");
				return false;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erro na verificação da chave privada");
			return false;
		}
	}
	
	public byte[] generateTestArray(int length) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer();
		
		SecureRandom rnd = new SecureRandom();
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for(int i = 0; i < length; i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		
		return sb.toString().getBytes("UTF-8");
	}
}
