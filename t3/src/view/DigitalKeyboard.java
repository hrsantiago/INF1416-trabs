package view;

import javax.swing.*;
import java.util.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;

public class DigitalKeyboard {

	private JFrame frame;
	private ArrayList<DigitalKey> teclas;
	private List<String> fonemas;
	private int keysPressed;
	private List<String> opt1;
	private List<String> opt2;
	private List<String> opt3;

	private DigitalKeyboardListener dkl;

	public DigitalKeyboard (DigitalKeyboardListener dkl){
		//Inicializa e embaralha a lista de fonemas
		fonemas = Arrays.asList(
			"BA", "BE", "BO",
			"CA", "CE", "CO",
			"DA", "DE", "DO",
			"FA", "FE", "FO",
			"GA", "GE", "GO"
		);

		this.dkl = dkl;
	}

	public void show() {
		Collections.shuffle(fonemas, new Random(System.nanoTime()));
	
		teclas = new ArrayList<DigitalKey>();
		keysPressed = 0;

		frame = new JFrame("Digital Keyboard");
		frame.setLayout(new FlowLayout());

		drawKeys();
		
		frame.setResizable(false);
		frame.setSize(300, 150);
		frame.setVisible(true);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void dismiss() {
		frame.setVisible(false);
		frame.dispose();
	}

	private void drawKeys() {
		for (int i = 0; i < fonemas.size(); i += 3){
			DigitalKey tecla = new DigitalKey(fonemas.get(i), fonemas.get(i+1), fonemas.get(i+2));
			tecla.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { 
					if(keysPressed == 0)
						opt1 = new ArrayList<String>(tecla.getFonemas());
					else if (keysPressed == 1) 
						opt2 = new ArrayList<String>(tecla.getFonemas());
					else 
						opt3 = new ArrayList<String>(tecla.getFonemas());

					keysPressed++;

					if(keysPressed == 3) 
						dkl.onCombinationsPrepared(generateCombinations());
				} 
			});
			frame.add(tecla);
		}
	}

	private List<String> generateCombinations() {
		ArrayList<String> combs = new ArrayList<String>();

		for (String f1 : opt1) {
			for (String f2 : opt2) {
				for (String f3 : opt3) {
					combs.add(f1+f2+f3);
				}
			}
		}

		return combs;
	}
	
	public class DigitalKey extends JButton {
		private List<String> fonemas;

		public DigitalKey(String f1, String f2, String f3){
			super(f1+"/"+f2+"/"+f3);

			fonemas = Arrays.asList(f1, f2, f3);
		}

		public List<String> getFonemas() { return fonemas; }
	}

}