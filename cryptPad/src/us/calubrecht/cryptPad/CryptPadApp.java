package us.calubrecht.cryptPad;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.*;

public class CryptPadApp extends JFrame
{

  private static final long serialVersionUID = 1L;
  private static final String APP_TITLE = "CryptPad";
  
  public static String FILE_EXTENSION = "cpf";

  private FileIO fileIO_ = new FileIO();
  private JTextArea textArea_ = new JTextArea();
  File lastFileName_ = null;
  String lastPassword_ = null;

  public String getLastPassword()
  {
    return lastPassword_;
  }

  public void setLastPassword(String lastPassword)
  {
    lastPassword_ = lastPassword;
  }
  
  public String getPwdIfNeeded(File file)
  {
    if (FileIO.isEncryptedFile(file))
    {
      return PasswordDlg.getPassword(this, "Enter Password", "Enter a password (or leave blank for none)");
    }
    return "";
  }

  public CryptPadApp()
  {
    add(new JScrollPane(textArea_));
    setSize(300, 600);
    setLocation(300,100);
    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("locked.png")));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle(APP_TITLE);
    createMenu();
  }
  
  private void setFileName(File file)
  {
    lastFileName_ = file;
    setTitle(APP_TITLE + " - " + file.getName());
  }

  public void loadFile(File file, String pwd)
  {
    try
    {
      textArea_.setText(fileIO_.loadText(file, pwd));
      setFileName(file);
    }
    catch (Exception e)
    {
      String error = e.getMessage();
      if (error.equals("Tag mismatch!"))
      {
        error = "Password Incorrect";
      }
      JOptionPane.showMessageDialog(this, error, "Load File Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void saveFile(File file, String password)
  {
    try 
    {
      setFileName(fileIO_.saveText(file, textArea_.getText(), password));
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Save File Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public File getLastFileName()
  {
    return lastFileName_;
  }

  private JMenuItem createItem(String text, int mnemonicKey, int accelerator, ActionListener listener)
  {
    JMenuItem item = new JMenuItem(text);
    item.setMnemonic(mnemonicKey);
    if (accelerator >= 0)
    {
      KeyStroke ctrKey = KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx());

      item.setAccelerator(ctrKey);
    }
    item.addActionListener(listener);
    return item;
  }

  private void createMenu()
  {
    JMenuBar bar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.add(createItem("Load", KeyEvent.VK_L, KeyEvent.VK_L, new LoadFileAction(this)));
    fileMenu.add(createItem("Save", KeyEvent.VK_S, KeyEvent.VK_S, new SaveFileAction(this, false)));
    fileMenu.add(createItem("Save As", KeyEvent.VK_A, -1, new SaveFileAction(this, true)));
    fileMenu.add(createItem("Exit", KeyEvent.VK_X, KeyEvent.VK_Q, new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        exit();

      }
    }));
    bar.add(fileMenu);
    setJMenuBar(bar);
  }

  public void exit()
  {
    dispose();
  }

  public static void main(String[] args)
  {
    if ((args.length == 1) && args[0].equals("-generateKey"))
    {
      if (args[0].equals("-generateKey"))
      {
        System.out.println(FileIO.generateKey());
      }
 
      return;
    }
    if ((args.length >= 2) && args[0].equals("-out"))
    {
      try
      {
        String pwd = args.length > 2 ? args[2] : "";
        System.out.println(new FileIO().loadText(new File(args[1]), pwd));
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    }
    try
    {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    CryptPadApp app = new CryptPadApp();
    if (args.length == 1)
    {
      app.loadFile(new File(args[0]), app.getPwdIfNeeded(new File(args[0])));
    }
    app.setVisible(true);

  }

}
