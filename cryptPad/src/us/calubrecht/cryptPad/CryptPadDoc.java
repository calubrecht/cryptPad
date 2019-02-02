package us.calubrecht.cryptPad;

import java.util.*;

import javax.swing.event.*;
import javax.swing.text.*;

public class CryptPadDoc extends PlainDocument implements DocumentListener
{
  private static final long serialVersionUID = 1L;
  List<DocChangeListener> listeners_ = new ArrayList<DocChangeListener>();
  boolean isDirty_ = false;
  
  public CryptPadDoc()
  {
    addDocumentListener(this);
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
    markDirty();
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
