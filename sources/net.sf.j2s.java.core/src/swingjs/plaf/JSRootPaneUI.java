package swingjs.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import swingjs.api.js.DOMNode;

public class JSRootPaneUI extends JSLightweightUI {

	Resizer resizer;
	private boolean isApplet;
	
	void setResizer(Resizer resizer) {
		this.resizer = resizer;
	}

	public JSRootPaneUI() {
		isRootPane = isContainer = true;
		setDoc();
	}

	@Override
	public DOMNode updateDOMNode() {
		if (domNode == null) {
			domNode = newDOMObject("div", id);
			addFocusHandler();
			//allowPaintedBackground = !jc.isOpaque();
			super.setBackgroundDOM(domNode, jc);
			isApplet = ((JRootPane) jc).getFrameViewer().isApplet;
		}
		checkAllowDivOverflow();
		return domNode;
	}
	
	@Override
	public void installUI(JComponent jc) {
	}

	@Override
	public boolean isConnected() {
		return isApplet || DOMNode.isConnected(domNode);
	}

	@Override
	public void uninstallUI(JComponent jc) {
	}

	@Override
	public Dimension getPreferredSize(JComponent jc) {
  	return null;
  }

	@SuppressWarnings("unused")
	@Override
	protected void setInnerComponentBounds(int width, int height) {
		boolean isResizable = true;
		try{
			isResizable = ((JFrame)jc.getParent()).isResizable();
			if (isResizable) {
				Resizer resizer = jc.getFrameViewer().getResizer();
				if (resizer != null)
					resizer.setPosition(0, 0);		
			}
		} catch (Throwable t) {
			
		}
	}
	
}
