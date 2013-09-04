package com.vmonaco.bbl;

import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 * Logging handler which fits in a Swing GUI and updates the caret position
 * 
 * @author vinnie
 *
 */
public class TextAreaHandler extends java.util.logging.Handler {

    private JTextArea mTextArea;
    private int mMaxLines;

    public TextAreaHandler(int rows, int columns, int maxLines) {
        mMaxLines = maxLines;
        mTextArea = new JTextArea(rows, columns);
        mTextArea.setEditable(false);
        
        // this doesn't always work for some reason, manually set below
//        DefaultCaret caret = (DefaultCaret) mTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    @Override
    public void publish(final LogRecord record) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (mTextArea.getLineCount() > mMaxLines) {
                    // remove the first line
                    Element root = mTextArea.getDocument()
                            .getDefaultRootElement();
                    Element first = root.getElement(0);
                    try {
                        mTextArea.getDocument().remove(first.getStartOffset(),
                                first.getEndOffset());
                    } catch (BadLocationException e) {
                        CustomExceptionHandler.submitCrashReport(e);
                    }
                }
                mTextArea.append(record.getMessage() + "\n");
                mTextArea.setCaretPosition( mTextArea.getDocument().getLength());
            }
        });
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
