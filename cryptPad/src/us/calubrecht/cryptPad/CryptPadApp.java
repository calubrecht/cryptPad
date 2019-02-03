package us.calubrecht.cryptPad;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class CryptPadApp extends JFrame implements DocChangeListener
{

  private static final long serialVersionUID = 1L;
  private static final String APP_TITLE = "CryptPad";
  
  public static String FILE_EXTENSION = "cpf";

  private JTextArea textArea_ = new JTextArea();
  private CryptPadDoc doc_ = new CryptPadDoc();
  
  @SuppressWarnings("serial")
  private UndoManager undoManager_ = new UndoManager() 
      {
        @Override
        public void undoableEditHappened(UndoableEditEvent e)
        {
          super.undoableEditHappened(e);
          undoManagerChanged();
        }
      };
  
  JMenuItem undoItem_;
  JMenuItem redoItem_;

  
  public CryptPadDoc getDocument()
  {
    return doc_;
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
    doc_.addDocChangeListener(this);
    textArea_.setDocument(doc_);
    setSize(300, 600);
    setLocation(300,100);
    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("locked.png")));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle(APP_TITLE);
    createMenu();
    doc_.addUndoableEditListener(undoManager_);
  }
  
  private void computeTitle()
  {
    String title = APP_TITLE;
    File fileName = doc_.getLastFileName();
    if (fileName != null)
    {
      title += " - " + fileName.getName();
      if (doc_.isDirty())
      {
        title += " *";
      }
    }
    setTitle(title);
  }

  public void loadFile(File file, String pwd)
  {
    try
    {
      doc_.loadFile(file, pwd);
      computeTitle();
    }
    catch (Exception e)
    {
      String error = e.getMessage();
      if (error == null)
      {
        error = "Unknown Error";
      }
      if (error.equals("Tag mismatch!"))
      {
        error = "Password Incorrect";
      }
      JOptionPane.showMessageDialog(this, error, "Load File Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  public void saveFile(File file, String password)
  {
    try 
    {
      doc_.saveFile(file, password);
    }
    catch (Exception e)
    {
      String error = e.getMessage();
      if (error == null)
      {
        error = "Unknown Error";
      }
      JOptionPane.showMessageDialog(this, error, "Save File Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
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
    fileMenu.add(createItem("New", KeyEvent.VK_N, KeyEvent.VK_N, new ActionListener() {public void actionPerformed(ActionEvent e) {clearDoc();}}));
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

    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_E);
    undoItem_= createItem("Undo", KeyEvent.VK_U, KeyEvent.VK_Z, new ActionListener() {public void actionPerformed(ActionEvent e) {undo();}});
    redoItem_= createItem("Redo", KeyEvent.VK_R, KeyEvent.VK_Y, new ActionListener() {public void actionPerformed(ActionEvent e) {redo();}});
    undoItem_.setEnabled(false);
    redoItem_.setEnabled(false);
    editMenu.add(undoItem_);
    editMenu.add(redoItem_);
    bar.add(editMenu);
    setJMenuBar(bar);
  }
  
  public void clearDoc()
  {
    doc_.clear();
  }
  
  public void undo()
  {
    if (undoManager_.canUndo())
    {
      undoManager_.undo();
      undoManagerChanged();
    }
  }
  
  public void redo()
  {
    if (undoManager_.canRedo())
    {
      undoManager_.redo();
      undoManagerChanged();
    }
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
  
  public void undoManagerChanged()
  {
    undoItem_.setEnabled(undoManager_.canUndo());
    redoItem_.setEnabled(undoManager_.canRedo());
  }

  @Override
  public void docChanged(DocChangeEvent event)
  {
    if (event.getEvent().equals("load") || event.getEvent().equals("save"))
    {
      undoManager_.die();
      undoManagerChanged();
    }
    computeTitle();
  }

}
