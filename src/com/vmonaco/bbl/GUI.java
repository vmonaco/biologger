package com.vmonaco.bbl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

public class GUI {
   
    private JFrame frame;
    private JLabel mStatusLabel;
    private Logger mLogger;
    
    public GUI(final BioLogger app) {
        
        JPanel userDataPanel = new JPanel();
        mStatusLabel = new JLabel("Starting up...");
        userDataPanel.add(mStatusLabel);
        
        mLogger = Logger.getLogger("com.vmonaco.bbl");
        mLogger.setLevel(Level.INFO);
        TextAreaHandler th = new TextAreaHandler(20, 60, 100);
        mLogger.addHandler(th);
        JScrollPane logScrollPane = new JScrollPane(th.getTextArea());
        
        final JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        app.close();
                        return null;
                    }
                };
                
                worker.execute();
                exit.setText("Uploading...");
                exit.setEnabled(false);
                mLogger.info("Closing session, this may take several minutes. Please wait.");
            }
        });
        
        JPanel buttons = new JPanel();
        buttons.add(exit);
        
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.add(userDataPanel, BorderLayout.NORTH);
        container.add(logScrollPane, BorderLayout.CENTER);
        container.add(buttons, BorderLayout.SOUTH);
        
        Border border = BorderFactory.createEmptyBorder(10, 5, 10, 5);
        container.setBorder(border);
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // center the window
        frame.setTitle("Behavioral Biometrics Logger");
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
    }

    public void setStatus(String status) {
        mStatusLabel.setText(status);
    }
    
    public void alert(String text) {
        
        JOptionPane.showMessageDialog(frame, text); 
    }
}
