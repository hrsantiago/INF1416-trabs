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
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
					String indexString = "XXXXYYYYZZZZ Teste.doc teste01 gteste\nXXXXYYYYZZZZ Teste.doc teste01 gteste";
					prepareTable(indexString);
				} else {
					//TODO: mensagem de erro no decryptIndex
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
					//TODO: resolver o resto com arquivo que foi selecionado
				}
			}
		});
	    add(decryptFileButton);
	    
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
