package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Manager;
import core.User;

/**
 *  ExitPanel class
 *  Classe auditavel - Logs 900X
 */

public class ExitPanel extends JPanel {

	private static final long serialVersionUID = -7871019104393430384L;
	
	private User m_currentUser;
	private PanelCloseListener m_pcl;
	private Manager m_manager;
	
	public ExitPanel(User user, PanelCloseListener pcl) {
		m_currentUser = user;
		m_pcl = pcl;
		m_manager = Manager.getInstance();
		preparePanel();
		
		m_manager.addRegistry(9001, m_currentUser.getId());
	}
	
	private void preparePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(new JLabel("Saida do sistema:"));
		add(new JLabel("Pressione o botão Sair para confirmar."));

		JButton exitButton = new JButton("Sair");
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(9002, m_currentUser.getId());
				m_pcl.onClose();
			}
		});
		
		add(exitButton);
		
		JButton backButton = new JButton("Voltar");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_manager.addRegistry(9003, m_currentUser.getId());
				m_pcl.onPanelClose();
			}
		});
		
		add(backButton);
		setVisible(true);
	}
}
