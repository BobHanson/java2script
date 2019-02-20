package swingjs.a2s;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends JTextField {

	public void isAWT() {}
	
	public TextField() {
		this("", 0);
	}

	public TextField(String text) {
        this(text, (text != null) ? text.length() : 0);
	}

	public TextField(int width) {
		this("", width);
	}

    public TextField(String text, int width) {
		super(text, width);
	}

	public void addTextListener(final TextListener textListener) {
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				textListener.textValueChanged(new TextEvent(this, 0));
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textListener.textValueChanged(new TextEvent(this, 0));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// not what you think. -- only when the style changes
				textListener.textValueChanged(new TextEvent(this, 0));
			}
		});
	}
	
    @Override
	public Dimension getPreferredSize() {
        return preferredSize(columns);
    }


    @Override
	@Deprecated
    public Dimension preferredSize() {
    	return getPreferredSize(columns);
    }

    
    public Dimension getPreferredSize(int columns) {
        return preferredSize(columns);
    }

    @Deprecated
    public Dimension preferredSize(int columns) {
    	return getPrefSizeJTF(columns);
    }

    /**
     * Gets the minimum dimensions for a text field with
     * the specified number of columns.
     * @param    columns   the number of columns in
     *                          this text field.
     * @since    JDK1.1
     */
    public Dimension getMinimumSize(int columns) {
        return minimumSize(columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int)</code>.
     */
    @Deprecated
    public Dimension minimumSize(int columns) {
    	return getMinimumSizeJTF(columns);
    }


	/**
     * Gets the minimum dimensions for this text field.
     * @return     the minimum dimensions for
     *                  displaying this text field.
     * @since      JDK1.1
     */
    @Override
	public Dimension getMinimumSize() {
        return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    @Override
	@Deprecated
    public Dimension minimumSize() {
        synchronized (getTreeLock()) {
            return (columns > 0) ?
                       minimumSize(columns) :
                       super.minimumSize();
        }
    }

    @Override
	protected int getColumnWidth() {
    	// _ not m here (empirical testing -- this is from the peer, which is native OS code)
        if (columnWidth == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            columnWidth = metrics.charWidth('_');
        }
        return columnWidth;
    }
    
	@Override
    protected int getJ2SWidth(int columns) {
		return  columns * getColumnWidth() + 24;
	}


//	A2SListener listener = null;
//
//	@Override
//	public A2SListener getA2SListener() {
//		if (listener == null)
//			listener = new A2SListener();
//		return listener;
//	}
//
    @Override
	protected void fireActionPerformed() {
    	A2SEvent.addListener(this);
    	super.fireActionPerformed();
    }
    
    



}
