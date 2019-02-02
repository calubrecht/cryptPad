package us.calubrecht.cryptPad;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveFileAction implements ActionListener
{

  JFileChooser fc = new JFileChooser();
  CryptPadApp frame_;
  boolean saveAs_;

  public SaveFileAction(CryptPadApp frame, boolean saveAs)
  {
    frame_ = frame;
    fc.setFileFilter(new FileNameExtensionFilter("CryptPad Files", "cpf"));
    saveAs_ = saveAs;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    File fileName = frame_.getLastFileName();
    if (fileName != null)
    {
      if (saveAs_)
      {
        fc.setCurrentDirectory(fileName);
      }
      else
      {
        frame_.saveFile(fileName, frame_.getLastPassword());
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
        if (JOptionPane.showConfirmDialog(frame_, fileName.getPath() + " already exists. Do you want to overwrite?",
            UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        {
          return;
        }
      }
      String pwd = frame_.getPwdIfNeeded(file);
      frame_.setLastPassword(pwd);
      frame_.saveFile(file, pwd);
    }
    else
    {
    }

  }

}
