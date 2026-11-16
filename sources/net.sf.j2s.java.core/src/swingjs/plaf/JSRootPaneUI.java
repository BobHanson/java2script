package swingjs.plaf;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import swingjs.api.js.DOMNode;

public class JSRootPaneUI extends JSLightweightUI {

	Resizer resizer;
	
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
			DOMNode.setStyle(domNode,  "background", (jc.isOpaque() ? toCSSString(jc.getBackground()) : "transparent"));				
		}
		checkAllowDivOverflow();
		return domNode;
	}
	
	@Override
	public void installUI(JComponent jc) {
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
