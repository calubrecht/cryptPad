package us.calubrecht.cryptPad;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LoadFileAction  extends CryptPadApp.MenuAction
{

  JFileChooser fc = new JFileChooser();
  CryptPadApp frame_;
  CryptPadDoc doc_;

  public LoadFileAction(CryptPadApp frame, String name, int mnemonic, int accelerator)
  {
    frame.super(name, mnemonic, accelerator);
    frame_ = frame;
    doc_ = frame.getDocument();
    fc.setFileFilter(new FileNameExtensionFilter("CryptPad Files", "cpf"));
    fc.addChoosableFileFilter(new FileNameExtensionFilter("Text Files", "txt", "ini", "conf", "xml"));
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    File fileName = doc_.getLastFileName();
    if (fileName != null)
    {
      fc.setCurrentDirectory(fileName);
    }
    int returnVal = fc.showOpenDialog(frame_);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      frame_.loadFile(file);
    }
    else
    {
    }

  }

}
