package swingjs.a2s;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBoxMenuItem;

import swingjs.plaf.JSCheckBoxUI;

public class CheckboxMenuItem extends JCheckBoxMenuItem {

	public void isAWT() {}
	
	public CheckboxMenuItem(String string) {
		super(string);
	}

	public CheckboxMenuItem() {
	}

	public CheckboxMenuItem(String string, boolean b) {
		super(string, b);
	}

	@Override
	public boolean getState() {
		return isSelected();
	}
	
	
	@Override
	public void setState(boolean b) {
		if (((DefaultButtonModel) model).setStateNoFire(b))
			((JSCheckBoxUI) (Object) getUI()).updateDOMNode();
	}   
	

}
