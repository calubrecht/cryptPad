package us.calubrecht.cryptPad;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LoadFileAction implements ActionListener
{

  JFileChooser fc = new JFileChooser();
  CryptPadApp frame_;
  CryptPadDoc doc_;

  public LoadFileAction(CryptPadApp frame)
  {
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
      String pwd = frame_.getPwdIfNeeded(file);
      frame_.loadFile(file, pwd);
    }
    else
    {
    }

  }

}
