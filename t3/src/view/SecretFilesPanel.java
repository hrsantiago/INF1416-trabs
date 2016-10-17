package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import core.Manager;
import core.User;

public class SecretFilesPanel extends JPanel {

private static final long serialVersionUID = -7871019104393430384L;

	private JTable m_table;
	private DefaultTableModel m_tableModel;

	private User m_currentUser;
	private PanelCloseListener m_pcl;
	private Manager m_manager;
	
	private List<Object[]> dataList;

	public SecretFilesPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		m_manager = Manager.getInstance();
		preparePanel();
		
		m_manager.addRegistry(8001, m_currentUser.getLogin());
	}
	
	private void preparePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(new JLabel("Frase secreta:"));
		JTextField passphraseField = new JTextField("teste123");
		add(passphraseField);
		
		add(new JLabel("Caminho da pasta:"));
		JTextField m_pathField = new JTextField("./Files");
		add(m_pathField);

		JButton listButton = new JButton("Listar");
		listButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(8003, m_currentUser.getLogin());
				byte[] index = decryptFile(m_pathField.getText(), "index", passphraseField.getText()); 
				if(index != null) {
					m_manager.addRegistry(8007, m_currentUser.getLogin());
					try {
						prepareTable(new String(index, "UTF8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				} else {
					m_manager.addRegistry(8006, m_currentUser.getLogin());
				}
			}
		});
		
		add(listButton);
		
		Object columnNames[] = { "Nome", "Assinatura Digital", "Envelope Digital" };
		m_tableModel = new DefaultTableModel(columnNames, 0);
		m_table = new JTable(m_tableModel);
		
		JScrollPane scrollPane = new JScrollPane(m_table);
	    add(scrollPane, BorderLayout.CENTER);
		
	    JButton decryptFileButton = new JButton("Abrir arquivo");
	    decryptFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = m_table.getSelectedRow();
				System.out.println("Linha selecionada: "+ String.valueOf(row));
				if (row != -1) {
					Object[] obj = dataList.get(row);
					byte[] data = decryptFile(m_pathField.getText(), (String)obj[0], passphraseField.getText());

					try {
						FileOutputStream out = new FileOutputStream(m_pathField.getText() + "/" + (String)obj[1]);
						out.write(data);
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
	    add(decryptFileButton);
	    
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(8002, m_currentUser.getLogin());
				m_pcl.onPanelClose();
			}
		});
		
		add(backButton);
		setVisible(true);
	}
	
	private void prepareTable(String index) {
		dataList = new ArrayList<Object[]>();
		String[] rows = index.split("\n");
		for(String row : rows) {
			String[] data = row.split(" ");
			if(data.length > 2){
				Object[] dataRow = data;
				m_tableModel.addRow(new Object[] {data[1], data[0] + ".env", data[0] + ".asd"});
				dataList.add(dataRow);
			}
		}
	}
	
	private byte[] decryptFile(String path, String name, String passphrase) {
		
		try {
			m_manager.addRegistry(8008, m_currentUser.getLogin(), name);
			
			byte[] envData = Files.readAllBytes(Paths.get(path + "/" + name + ".env"));
			PrivateKey privkey = User.getPrivateKeyObject(m_currentUser.getPrivateKey(), passphrase);
			
			Cipher cipherEnv = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipherEnv.init(Cipher.DECRYPT_MODE, privkey);
			byte[] symmetricKey = cipherEnv.doFinal(envData);
			
			byte[] indexData = Files.readAllBytes(Paths.get(path + "/" + name + ".enc"));
			
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(symmetricKey);
			
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		    keyGen.init(56, random);
		    Key key = keyGen.generateKey();
			
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			byte[] plainIndexData = cipher.doFinal(indexData);
			
			m_manager.addRegistry(8009, m_currentUser.getLogin(), name);

			Path pathCertificate = Paths.get(m_currentUser.getCertificate());
			byte[] pubbytes = Files.readAllBytes(pathCertificate);
			InputStream inStream = new ByteArrayInputStream(pubbytes); 
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate signercert = (X509Certificate)cf.generateCertificate(inStream);
			
			Signature sig = Signature.getInstance("MD5withRSA");
			sig.initVerify(signercert.getPublicKey());
			sig.update(plainIndexData);
			
			byte[] sigData = Files.readAllBytes(Paths.get(path + "/" + name + ".asd"));
			if(!sig.verify(sigData)) {
				JOptionPane.showMessageDialog(null, name + " pode ter sido adulterada!");
				m_manager.addRegistry(8012, m_currentUser.getLogin(), name);
			}
			else {
				m_manager.addRegistry(8010, m_currentUser.getLogin(), name);
			}
			return plainIndexData;
			
		} catch (NoSuchPaddingException e) {
			m_manager.addRegistry(8011, m_currentUser.getLogin(), name);
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			m_manager.addRegistry(8011, m_currentUser.getLogin(), name);
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			m_manager.addRegistry(8011, m_currentUser.getLogin(), name);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			m_manager.addRegistry(8011, m_currentUser.getLogin(), name);
			e.printStackTrace();
		} 
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
}
