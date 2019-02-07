package us.calubrecht.cryptPad;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class AboutDlg extends JDialog
{

  public AboutDlg(JFrame parent)
  {
    super(parent, "About");
    JTextPane text = new JTextPane();
    text.setContentType("text/html");

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(text, BorderLayout.CENTER);
    text.setEditable(false);

    text.addHyperlinkListener(new HyperlinkListener()
    {

      @Override
      public void hyperlinkUpdate(HyperlinkEvent e)
      {
        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
        {
          return;
        }
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
        {
          try
          {
            Desktop.getDesktop().browse(e.getURL().toURI());
          }
          catch (Exception e1)
          {
            System.out.println("Failed to launch browser.");
          }
        }
      }
    });
    text.setText(getContent());
    
    JButton button = new JButton("Close"); 
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
        
      }});
    JPanel paddingPane = new JPanel();
    paddingPane.setLayout(new BoxLayout(paddingPane, BoxLayout.Y_AXIS));
    paddingPane.add(button);
    paddingPane.setBackground(Color.WHITE);
    int size = 20;
    paddingPane.setBorder( new EmptyBorder(size, size, size, size));
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
    pane.add(Box.createHorizontalGlue());
    pane.add(paddingPane);
    pane.add(Box.createHorizontalGlue());
    pane.setBackground(Color.WHITE);

    getContentPane().add(pane, BorderLayout.SOUTH);
    setSize(500,300);

    setModal(true);
  }
  
  private String getContent()
  {
    InputStream is = getClass().getResourceAsStream("about.html");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    StringBuilder builder = new StringBuilder();
    String line = null;
    try
    {
      while ((line = reader.readLine()) != null)
      {
        builder.append(line).append('\n');
      }
    }
    catch (IOException e)
    {
      return e.toString();
    }
    return builder.toString(); 
  }
}
