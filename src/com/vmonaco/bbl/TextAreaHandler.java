package com.vmonaco.bbl;

import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;

public class TextAreaHandler extends java.util.logging.Handler {

	private JTextArea mTextArea;
	private int mMaxLines;

	public TextAreaHandler(int rows, int columns, int maxLines) {
		mMaxLines = maxLines;
		mTextArea = new JTextArea(rows, columns);
		mTextArea.setEditable(false);
		mTextArea.setHighlighter(null);
		
		DefaultCaret caret = (DefaultCaret) mTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	@Override
	public void publish(final LogRecord record) {

		SwingWorker<String, String> worker = new SwingWorker<String, String>() {
			@Override
			protected String doInBackground() throws Exception {
				if (mTextArea.getLineCount() > mMaxLines) {
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

		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// if (mTextArea.getLineCount() > mMaxLines) {
		// // remove the first line
		// Element root = mTextArea.getDocument()
		// .getDefaultRootElement();
		// Element first = root.getElement(0);
		// try {
		// mTextArea.getDocument().remove(first.getStartOffset(),
		// first.getEndOffset());
		// } catch (BadLocationException e) {
		// CustomExceptionHandler.submitCrashReport(e);
		// }
		// }
		// mTextArea.append(record.getMessage() + "\n");
		// mTextArea.setCaretPosition( mTextArea.getDocument().getLength());
		// }
		// });
	}

	public JTextArea getTextArea() {
		return this.mTextArea;
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
	}

}
