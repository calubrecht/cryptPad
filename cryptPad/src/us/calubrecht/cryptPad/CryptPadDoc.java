package us.calubrecht.cryptPad;

import java.io.*;
import java.security.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.text.*;

public class CryptPadDoc extends PlainDocument implements DocumentListener
{
  private static final long serialVersionUID = 1L;
  List<DocChangeListener> listeners_ = new ArrayList<DocChangeListener>();
  boolean isDirty_ = false;
  File lastFileName_ = null;
  String lastPassword_ = null;
  private FileIO fileIO_ = new FileIO();

  public void loadFile(File file, String pwd) throws GeneralSecurityException, IOException
  {
    setText(fileIO_.loadText(file, pwd), false);
    lastFileName_ = file;
    lastPassword_ = pwd;
    docChanged("load");
  }
  
  public void saveFile(File file, String password) throws IOException, GeneralSecurityException
  {
    File fileName = fileIO_.saveText(file, getText(), password);
    markClean();
    lastFileName_ = fileName;
    docChanged("save");
  }

  public File getLastFileName()
  {
    return lastFileName_;
  }

  public void setLastFileName(File lastFileName)
  {
    this.lastFileName_ = lastFileName;
  }

  public String getLastPassword()
  {
    return lastPassword_;
  }

  public void setLastPassword(String lastPassword)
  {
    lastPassword_ = lastPassword;
  }

  public CryptPadDoc()
  {
    addDocumentListener(this);
  }

  public void clear()
  {
    lastFileName_ = null;
    lastPassword_ = null;
    setText("", false);
  }

  public void setText(String text, boolean isDirty)
  {
    try
    {
      remove(0, getLength());
      insertString(0, text, null);
      isDirty_ = isDirty;
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String getText()
  {
    try
    {
      return getText(0, getLength());
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

  public void markDirty()
  {
    isDirty_ = true;
  }

  public void markClean()
  {
    isDirty_ = false;
  }

  public boolean isDirty()
  {
    return isDirty_;
  }

  public void addDocChangeListener(DocChangeListener listener)
  {
    listeners_.add(listener);
  }

  private void docChanged(String event)
  {
    if (!(event.equals("load") || event.equals("save")))
    {
      markDirty();
    }
    for (DocChangeListener listener : listeners_)
    {
      listener.docChanged(new DocChangeEvent(event));
    }
  }

  @Override
  public void insertUpdate(DocumentEvent e)
  {
    docChanged("insert");
  }

  @Override
  public void removeUpdate(DocumentEvent e)
  {
    docChanged("remove");
  }

  @Override
  public void changedUpdate(DocumentEvent e)
  {
    docChanged("change");
  }

}
