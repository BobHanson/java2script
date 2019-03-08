package swingjs.plaf;

import java.awt.Component;
import java.awt.JSComponent;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.UIManager;

import swingjs.api.js.DOMNode;

public class JSMenuUI extends JSMenuItemUI {

	private JMenu jm;

	public JSMenuUI() {
		isMenu = true;
		setDoc();
	}

	@Override
	public DOMNode updateDOMNode() {
		boolean isMenuBarMenu = jm.isTopLevelMenu();
		if (domNode != null && isMenuItem == isMenuBarMenu) {
			// we have changed the type of menu, from popup to menubar or something like
			// that.
			// the outerNode for a menu item is its domNode, the <li> tag. But a MenuBar
			// needs
			// the outerNode. The domNode should be fine. The big change is the
			// containerNode
			outerNode = null;
			if (isMenuBarMenu)
				DOMNode.detachAll(((JSComponentUI) jc.getParent().ui).domNode);
		}
		if (domNode == null) {
			domNode = createItem(isMenuBarMenu ? "_bar" : "_elem", null);
			bindJQueryEvents(domNode, "mouseenter mouseleave", SOME_MOUSE_EVENT);
		}
		allowTextAlignment = isMenuItem = !isMenuBarMenu;
		containerNode = (isMenuBarMenu ? null : domNode);
		addClass(domNode, isMenuBarMenu ? "j2s-menuBar-menu" : "j2s-popup-menu");
		removeClass(domNode, !isMenuBarMenu ? "j2s-menuBar-menu" : "j2s-popup-menu");
		if (isMenuBarMenu)
			removeClass(menuAnchorNode, "a");
		else
			addClass(menuAnchorNode, "a");
		setCssFont(domNode, c.getFont());
		DOMNode.setVisible(domNode, jc.isVisible());
		setIconAndText("menu", currentIcon, currentGap, currentText);
		return domNode;
	}

//	@Override
//	public boolean handleJSEvent(Object target, int eventType, Object jQueryEvent) {
//		System.out.println("JSMenuUI this is not working, right? ");// RIGHT
//		// we use == here because this will be JavaScript
//		if (target == domNode && eventType == -1) {
//			String type = (/** @j2sNative jQueryEvent.type || */ "");
//			if (type.equals("mouseenter")) {
//				if (!jm.getParent().getUIClassID().equals("MenuBarUI"))
//					stopPopupMenuTimer();
//				jm.setSelected(true);
//				return HANDLED;
//			}
//			if (type.equals("mouseleave")) {
//				jm.setSelected(false);
//				if (jm.getParent().getUIClassID().equals("MenuBarUI"))
//					startPopupMenuTimer();
//				return HANDLED;
//			}
//		}
//		return super.handleJSEvent(target, eventType, jQueryEvent);
//	} 

	@Override
	public void propertyChangedFromListener(PropertyChangeEvent e, String prop) {
		// System.out.println("JSMenuUI prop = " + prop + " " + jm.getText());
		super.propertyChangedFromListener(e, prop);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		// System.out.println("JSMenuUI prop " + prop);
		if (jc.isVisible()) {
			if (prop == "ancestor") {
				rebuild();
			}
		}
		super.propertyChange(e);
	}

	private void rebuild() {
		if (jc.getParent() != null) {
			if (domNode != null && isMenuItem == jm.isTopLevelMenu()) {
				reInit();
				outerNode = null;
				updateDOMNode();
				return;
			}
		}
	}

	@Override
	public void installUI(JComponent jc) {
		jm = (JMenu) jc;
		super.installUI(jc);
		((JMenu) menuItem).setDelay(200);
	}

	@Override
	public void uninstallUI(JComponent jc) {
		super.uninstallUI(jc);
	}

	/**
	 * note that the last element is null if array is oversize
	 * 
	 */
	@Override
	protected Component[] getChildren() {
		return (isMenuItem ? new Component[] { jm.getPopupMenu() } : JSComponent.getChildArray(jm));
	}

	@Override
	protected int getChildCount() {
		return (isMenuItem ? 1 : jc.getComponentCount());
	}

//	@Override
//	public Dimension getMaximumSize(JComponent jc) {
//		return super.getPreferredSize(jc);
//	}
//
//	@Override
//	public Dimension getPreferredSize(JComponent jc) {
//		return super.getPreferredSize(jc);
//	}

	@Override
	protected String getPropertyPrefix() {
		return "Menu";
	}

}
