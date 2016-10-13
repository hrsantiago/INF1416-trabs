package view;

import core.Manager;

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
import java.sql.*;

import java.util.*;
import javax.swing.*;

import java.security.*;
import javax.crypto.*;

import core.*;

public class Window extends JFrame implements DigitalKeyboardListener {

  private static final long serialVersionUID = -3739008754324139578L;

  private int passwordErrors;
  private DigitalKeyboard dk;
  private User currentUser;

  private JPanel m_loginPanel;

  public Window() {
    createLoginPanel();
    setupWindow();

    passwordErrors = 0;
    m_loginPanel.setVisible(true);
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
  }

  private void createLoginPanel() {
    JPanel p = new JPanel();
    p.setVisible(false);
    p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

    p.add(new JLabel("Login:"));

    JTextField loginField = new JTextField();
    p.add(loginField);

    JButton loginButton = new JButton("Login");
    loginButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          String login = loginField.getText();

          Manager manager = Manager.getInstance();
          int userId = manager.getUserId(login);
          if(userId != -1) {
            currentUser = manager.getUser(userId);

            JOptionPane.showMessageDialog(null, "Login OK.");
            m_loginPanel.setVisible(false);
            
            dk = new DigitalKeyboard(Window.this);
            dk.show();
            //setVisible(false);
          }
          else
            JOptionPane.showMessageDialog(null, "Login or password invalid.");
        }
    });
    p.add(loginButton);

    add(p);
    m_loginPanel = p;
  }

  public void onCombinationsPrepared(List<String> combinations) {
    boolean passOk = false;
    
    try{
      MessageDigest md = MessageDigest.getInstance("MD5");

      for (String s : combinations){
        String combsalt = s + currentUser.getSalt();
        // DEBUG
        //System.out.println(combsalt);

        byte[] digest = md.digest(combsalt.getBytes());
        // converte o digist para hexadecimal
        String digestHex = User.byteToHex(digest);
        if(currentUser.getPassword().equals(digestHex)){
          passOk = true;
          break;
        }
      }
      if(passOk){
        dk.dismiss();
        System.out.println("Senha correta!");
      } else {
        dk.dismiss();
        passwordErrors++;
        if(passwordErrors >= 3){
          //TODO: adicionar usuario como locked;
          currentUser = null;
          m_loginPanel.setVisible(true);
        } else {
          dk = new DigitalKeyboard(Window.this);
          dk.show();
        }
      }
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
      dk.dismiss();
      System.out.println("ERROR! NoSuchAlgorithmException MD5");
    }
    
  }
}