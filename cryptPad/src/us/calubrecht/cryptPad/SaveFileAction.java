package us.calubrecht.cryptPad;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveFileAction extends CryptPadApp.MenuAction
{

  JFileChooser fc = new JFileChooser();
  CryptPadApp frame_;
  CryptPadDoc doc_;
  boolean saveAs_;

  public SaveFileAction(CryptPadApp frame, String name, int mnemonic, int accelerator, boolean saveAs)
  {
    frame.super(name, mnemonic, accelerator);
    frame_ = frame;
    doc_ = frame_.getDocument();
    fc.setFileFilter(new FileNameExtensionFilter("CryptPad Files", "cpf"));
    saveAs_ = saveAs;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    File fileName = doc_.getLastFileName();
    if (fileName != null)
    {
      if (saveAs_)
      {
        fc.setCurrentDirectory(fileName);
      }
      else
      {
        frame_.saveFile(fileName, doc_.getLastPassword());
        return;
      }
    }

    int returnVal = fc.showSaveDialog(frame_);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      file = FileIO.correctFileName(file);

      if (file.exists())
      {
        if (JOptionPane.showConfirmDialog(frame_, file.getPath() + " already exists. Do you want to overwrite?",
            UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        {
          return;
        }
      }
      String pwd = frame_.getPwdIfNeeded(file);
      doc_.setLastPassword(pwd);
      frame_.saveFile(file, pwd);
    }
    else
    {
    }

  }

}
