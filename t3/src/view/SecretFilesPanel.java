package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import core.Manager;
import core.User;

public class SecretFilesPanel extends JPanel {

private static final long serialVersionUID = -7871019104393430384L;
	
	private User m_currentUser;
	private PanelCloseListener m_pcl;
	private Manager m_manager;

	public SecretFilesPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		m_manager = Manager.getInstance();
		preparePanel();
		
		m_manager.addRegistry(8001, m_currentUser.getId());
	}
	
	private void preparePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(new JLabel("Caminho da pasta:"));
		JTextField m_pathField = new JTextField("./Files");
		add(m_pathField);

		JButton listButton = new JButton("Listar");
		listButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(8003, m_currentUser.getId());
				if(decryptIndex(m_pathField.getText())) {
					
				}
				else {
					
				}
			}
		});
		
		add(listButton);
		
		Object [][] data = {};
		Object columnNames[] = { "Nome", "Assinatura Digital", "Envelope Digital" };
		JTable table = new JTable(data, columnNames);
		
		JScrollPane scrollPane = new JScrollPane(table);
	    add(scrollPane, BorderLayout.CENTER);
		
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(8002, m_currentUser.getId());
				m_pcl.onPanelClose();
			}
		});
		
		add(backButton);
		setVisible(true);
	}
	
	private boolean decryptIndex(String path) {
		String filename = path + "/index.enc";
		
		try {
			byte[] data = Files.readAllBytes(Paths.get(filename));
			System.out.println(User.byteToHex(data));
			
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(new String("teste123").getBytes());
			
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		    keyGen.init(56, random);
		    Key key = keyGen.generateKey();
			
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			byte[] newPlainText = cipher.doFinal(data);
			System.out.println( new String(newPlainText, "UTF8") );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
}
