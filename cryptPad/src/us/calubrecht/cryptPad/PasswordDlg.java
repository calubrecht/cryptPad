package us.calubrecht.cryptPad;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class PasswordDlg extends JDialog
{
  static final long serialVersionUID = 1L;
  
  JPasswordField passwd_ = new JPasswordField(25);
  
  public PasswordDlg(Frame parent, String title, String text)
  {
    super(parent, title, true);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    JLabel label = new JLabel(text);
    
    contentPane.add(Box.createVerticalGlue());
    contentPane.add(centerComponent(label));
    contentPane.add(Box.createVerticalGlue());
    passwd_.setMaximumSize( passwd_.getPreferredSize() );
    contentPane.add(centerComponent(passwd_));
    contentPane.add(Box.createVerticalGlue());
    JButton button = new JButton("OK");
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
        
      }});
    contentPane.add(centerComponent(button));
    contentPane.add(Box.createVerticalGlue());
    getRootPane().setDefaultButton(button);
    setResizable(false);
    setSize(350, 120);
    setLocationRelativeTo(parent);
    
  }
  
  JPanel centerComponent(JComponent comp)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(Box.createHorizontalGlue());
    panel.add(comp);
    panel.add(Box.createHorizontalGlue());
    return panel;
  }
  
  public String getPasswd()
  {
    return new String(passwd_.getPassword());
  }

  public static String getPassword(Frame parent, String title, String text)
  {
    PasswordDlg dlg = new PasswordDlg(parent, title, text);
    dlg.setVisible(true);
    return dlg.getPasswd();
  }
}
