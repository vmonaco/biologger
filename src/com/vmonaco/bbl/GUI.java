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
//        mLogger.setLevel(Level.FINE);
        TextAreaHandler th = new TextAreaHandler(20, 60, 20);
        mLogger.addHandler(th);
//        JScrollPane logScrollPane = new JScrollPane(th.getTextArea());
        
        final JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                app.stopLogging();
                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
                    @Override
                    protected String doInBackground() throws Exception {
                    	exit.setText("Uploading...");
                    	exit.setEnabled(false);
                    	mLogger.info("Closing session, this may take several minutes. Please wait.");
                        app.flush();
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                    	app.close();
                    }
                };
                
                worker.execute();
            }
        });
        
//        final JButton flush = new JButton("Send Data");
//        flush.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
//                    @Override
//                    protected String doInBackground() throws Exception {
//                    	flush.setText("Uploading...");
//                    	flush.setEnabled(false);
//                        app.flush();
//                        return null;
//                    }
//                    
//                    @Override
//                    protected void done() {
//                    	flush.setText("Send Data");
//                    	flush.setEnabled(true);
//                    }
//                };
//                
//                worker.execute();
//            }
//        });
        
//        final JButton keymap = new JButton("Print Keymap");
//        keymap.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
//                    @Override
//                    protected String doInBackground() throws Exception {
//                        app.printKeyMap();
//                        return null;
//                    }
//                };
//                
//                worker.execute();
//            }
//        });
        
//        final JButton verbose = new JButton("More Verbose");
//        verbose.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
//                    @Override
//                    protected String doInBackground() throws Exception {
//                    	if (Level.FINE == mLogger.getLevel()) {
//                    		mLogger.setLevel(Level.FINER);
//                    		verbose.setText("Less Verbose");
//                    	} else {
//                    		mLogger.setLevel(Level.FINE);
//                    		verbose.setText("More Verbose");
//                    	}
//                        return null;
//                    }
//                };
//                
//                worker.execute();
//            }
//        });
        
        JPanel buttons = new JPanel();
        buttons.add(exit);
//        buttons.add(flush);
//        buttons.add(keymap);
//        buttons.add(verbose);
        
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.add(userDataPanel, BorderLayout.NORTH);
        container.add(th.getTextArea(), BorderLayout.CENTER);
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
