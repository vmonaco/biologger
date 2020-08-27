package com.vmonaco.bio;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;

public class GUI {

	private JFrame frame;
	private JLabel mStatusLabel;
	private Logger mLogger;

	private class TextAreaHandler extends java.util.logging.Handler {

		private JTextArea mTextArea;
		private int mMaxLines;

		public TextAreaHandler(int rows, int columns, int maxLines) {
			mMaxLines = maxLines;
			mTextArea = new JTextArea(rows, columns);
			mTextArea.setEditable(false);
			mTextArea.setHighlighter(null);
			mTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));

			DefaultCaret caret = (DefaultCaret) mTextArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		}

		@Override
		public void publish(final LogRecord record) {

			SwingWorker<String, String> worker = new SwingWorker<String, String>() {
				@Override
				protected String doInBackground() throws Exception {
				 	while (mTextArea.getLineCount() > mMaxLines) {
						// remove the first line
						Element root = mTextArea.getDocument().getDefaultRootElement();
						Element first = root.getElement(0);
						try {
							mTextArea.getDocument().remove(first.getStartOffset(), first.getEndOffset());
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
					mTextArea.append(record.getMessage() + "\n");
					mTextArea.setCaretPosition(mTextArea.getDocument().getLength());
					return null;
				}
			};

			worker.execute();
		}

		public JTextArea getTextArea() {
			return this.mTextArea;
		}

		@Override
		public void close() throws SecurityException {
			// Empty
		}

		@Override
		public void flush() {
			// Empty
		}

	}

	public GUI(final BioLogger app, String outDir) {

		JPanel userDataPanel = new JPanel();
		mStatusLabel = new JLabel("Starting up...");
		userDataPanel.add(mStatusLabel);

		mLogger = Logger.getLogger("com.vmonaco.bio");
		TextAreaHandler th = new TextAreaHandler(40, 80, 40);
		mLogger.addHandler(th);

		final JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// To give a chance to process the event that invoked this button
				try {
		    	Thread.sleep(50);
				} catch(InterruptedException ex) {
		    	Thread.currentThread().interrupt();
				}
				System.exit(0);
			}
		});

		JPanel buttons = new JPanel();
		buttons.add(exit);

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

		mStatusLabel.setText("Saving files to: " + outDir);
	}

	public void alert(String text) {
		JOptionPane.showMessageDialog(frame, text);
	}
}
