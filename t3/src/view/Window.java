package view;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;
import javax.swing.*;

public class Window extends JFrame implements DigitalKeyboardListener {

  private static final long serialVersionUID = -3739008754324139578L;

  private int passwordErrors;
  private DigitalKeyboard dk;

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

    JButton btnShowKeyboard = new JButton("Abrir teclado virtual");
    btnShowKeyboard.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { 
          dk = new DigitalKeyboard(Window.this);
          dk.show();
          setVisible(false);
        }
    });

    add(btnShowKeyboard);
  }

  public void onCombinationsPrepared(List<String> combinations) {
    setVisible(true);

    // DEBUG
    for (String s : combinations){
      System.out.println(s);
    }

    dk.dismiss();
  }
}