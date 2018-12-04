package swingjs.plaf;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.text.JTextComponent;

import swingjs.api.js.DOMNode;

public class JSEditorPaneUI  extends JSTextUI {

	@Override
  public DOMNode updateDOMNode() {
    if (domNode == null) {
      allowPaintedBackground = false;
      focusNode = enableNode = textNode = valueNode = domNode = 
          newDOMObject("div", id);
      DOMNode.setStyles(domNode, "resize", "none");
      setDataUI(domNode);
      if (((JTextComponent) c).isEditable())
        bindJSKeyEvents(domNode, true);
    }
    textListener.checkDocument();
    setCssFont(DOMNode.setAttr(domNode, "innerHTML", getComponentText()), c.getFont());
    DOMNode.setAttr(domNode, "contentEditable", editable ? "true" : "false");
	return updateDOMNodeCUI();
  }

  private Insets myInsets = new Insets(0, 0, 5, 5); 
  @Override
  public Insets getInsets() {
    return myInsets;
  }
  
  @Override
  protected Dimension getCSSAdjustment(boolean addingCSS) {
    return new Dimension(0, 0);
  }

  @Override
  protected String getPropertyPrefix() {
    return "EditablePane.";
  }
  
}
