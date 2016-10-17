package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;

import core.Manager;
import core.User;

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
		
		m_manager.addRegistry(6001, m_currentUser.getId());
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
				m_pcl.onPanelClose();
			}
		});
		
		add(backButton);
		setVisible(true);
	}
	
	private boolean testKey() {
		try {
			Path pathPrivateKey = Paths.get(m_keyPath);
			byte[] keybytes = Files.readAllBytes(pathPrivateKey);
			
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            random.setSeed(m_passphraseField.getText().getBytes());
           
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56, random);
            Key key = keyGen.generateKey();
           
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
           
            byte[] privKeyBytesBase64 = cipher.doFinal(keybytes);
            String keyString = new String(privKeyBytesBase64, "UTF8");
            keyString = keyString.replace("-----BEGIN PRIVATE KEY-----", "");
            keyString = keyString.replace("-----END PRIVATE KEY-----", "");
            System.out.println(keyString.trim());
           
            byte[] privKeyB = DatatypeConverter.parseBase64Binary(keyString.trim());
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privkey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privKeyB));
            
			byte[] testArray = generateTestArray(512);
	
			Signature sig = Signature.getInstance("MD5withRSA");
			sig.initSign(privkey);
			sig.update(testArray);
			byte[] ds = sig.sign();
			
			Path pathCertificate = Paths.get(m_currentUser.getCertificate());
			byte[] pubbytes = Files.readAllBytes(pathCertificate);
			InputStream inStream = new ByteArrayInputStream(pubbytes); 
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate signercert = (X509Certificate)cf.generateCertificate(inStream);
			
			sig.initVerify(signercert.getPublicKey());
			sig.update(testArray);
			
			if(sig.verify(ds)){
				JOptionPane.showMessageDialog(null, "Chave validada!");
				return true;
			} else {
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
