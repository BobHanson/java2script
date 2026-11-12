package swingjs.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.JSComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

import javajs.api.js.HTML5Applet.JSFunction;
import swingjs.JSFrameViewer;
import swingjs.JSUtil;
import swingjs.api.js.DOMNode;
import swingjs.api.js.JSInterface;

public class Resizer {

	private final static String[] defaultResizerNodeStyles = new String[] {
//			"background-color", "red", 
			 "z-index", "1000002",
			 "cursor", "nwse-resize",
			   "width", "30px",
			   "height", "30px",
			   "margin-left", "-15px",
			   "margin-top", "-15px",					
			   "background-image", "linear-gradient(red, red)",
			   "background-repeat", "no-repeat",
			   "background-position", "center",
			   "background-size", "10px 10px"
			};
	
	private final static String[] defaultRubberBandStyles = new String[] {
			"border", "2px dashed #FF00FF", 
			"z-index", "1000003", 
			"position", "absolute",
			"left", "0px", 
			"top", "0px", 
			"display", "none", 
			"pointer-events", "none",
			"touch-action", "none",
			"user-select", "none",
			"webkit-user-select", "none",
			"cursor", "nwse-resize"			};

	
	
	private final static String[] defaultAltResizerNodeStyles = defaultResizerNodeStyles;
	
	private final static String[] defaultAltRubberBandStyles = defaultRubberBandStyles;
	
	public JRootPane rootPane;
	public DOMNode resizer, rootNode, rubberBand;
	private JFrame jframe;
	private int offsetx = -4, offsety = -4, minWidth = 10, minHeight = 10;
	private RootPaneContainer rpc;
	@SuppressWarnings("unused")
	private int titleHeight;
	private boolean enabled = true;
	private boolean allowResize = true;

	public Resizer() {
	}

	public Resizer set(JSFrameViewer viewer, RootPaneContainer top) {
		rpc = top;
		rootPane = rpc.getRootPane();
		titleHeight = viewer.getInsets().top; // 20px
		if (viewer.isApplet) {
			rootNode = viewer.getDiv("appletdiv");
		} else {
			jframe = (JFrame) rpc;
			rootNode = ((JSComponentUI) jframe.getUI()).getDOMNode();
		}
		return (rootNode == null ? null : this);
	}

	public void show() {
		if (!allowResize  || !enabled)
			return;
		if (resizer == null) 
			createAndShowResizer();
		else
			JSUtil.jQuery.$(resizer).show();
		setPosition(0, 0);
	}

	public void hide() {
		JSUtil.jQuery.$(resizer).hide();		
	}
	
	public void setMin(int min) {
		minWidth = minHeight = min;
	}
	
	@SuppressWarnings("unused")
	private void createAndShowResizer() {
		String id = rootPane.秘htmlName + "_resizer";
		resizer = DOMNode.createElement("div", id);
		DOMNode.setSize(resizer, 10, 10);
		DOMNode.setStyles(resizer, defaultResizerNodeStyles);
		JSUtil.jQuery.$(resizer).addClass("swingjs-resizer");
		rubberBand = DOMNode.createElement("div", id + "_rb");
		DOMNode.setStyles(rubberBand, defaultRubberBandStyles);
		rootNode.appendChild(resizer);
		rootNode.appendChild(rubberBand);
		JSFunction fHandleResizer = null;
		//, fHandleDOMResize = null;
		Object me = this;
		/**
		 * @j2sNative
		 * 
		 * 			fHandleResizer =
		 *            function(xyev,type){me.fHandleResizer$I$I$I( xyev.dx,
		 *            xyev.dy,type)};
		 * 
		 */
		{
		}
		// set to track size changes
		JSUtil.J2S.setDraggable(resizer, new JSFunction[] { fHandleResizer });
//		JSUtil.jQuery.$(rootNode).resize(fHandleDOMResize);
	}

	/**
	 * @j2sAlias
	 * 
	 * @param dw
	 * @param dh
	 */
	public void setPosition(int dw, int dh) {
		Rectangle r = getFrameRectangle(dw, dh);
		DOMNode.setTopLeftAbsolute(resizer, r.height + offsety, r.width + offsetx);
		DOMNode.setSize(rubberBand, r.width, r.height);
	}
	
	public DOMNode getDOMNode() {
		return resizer;
	}
	
	/**
	 * 
	 * @param xyev
	 * @param type
	 */
	protected void fHandleResizer(int dx, int dy, int type) {
		if (!enabled || !allowResize)
			return;
		switch (type) {
		case MouseEvent.MOUSE_MOVED:
			break; // TODO - change cursor? Entry/Exit?
		case MouseEvent.MOUSE_PRESSED:
			//DOMNode.setStyle(resizer, "background-color", "green");
			DOMNode.setVisible(rubberBand, true);
			JSInterface.setCursor("nwse-resize");
			// set cursor to dragging
			break;
		case MouseEvent.MOUSE_DRAGGED:
			setPosition(dx, dy);
			break;
		case MouseEvent.MOUSE_RELEASED:
			//DOMNode.setStyle(resizer, "background-color", "red");
			DOMNode.setVisible(rubberBand, false);
			JSInterface.setCursor("auto");
			fHandleDOMResize(null, dx, dy);
		}
	}

	/**
	 * @j2alias
	 * 
	 * @param event
	 * @param dw
	 * @param dh
	 */
	protected void fHandleDOMResize(Object event, int dw, int dh) {
		Rectangle r;
		if (!enabled)
			return;
		if (event == null) {
			// from above
			r = getFrameRectangle(dw, dh);
		} else {
			// from some DOM event
			DOMNode.getCSSRectangle(rootNode, r = new Rectangle());
		}
		setFrameDimensions(r);
		// Toolkit.getEventQueue().postEvent(new ComponentEvent(f,
		// ComponentEvent.COMPONENT_RESIZED));
	}

	/**
	 * 
	 * @param r
	 */
  void setFrameDimensions(Rectangle r) {
		if (jframe == null) {
			rootPane.getGraphics().setColor(Color.WHITE);
			rootPane.getGraphics().fillRect(0, 0, r.width, r.height);
			((JApplet) rootPane.getParent()).resizeHTML(r.width, r.height);
		} else {
			jframe.setPreferredSize(new Dimension(r.width, r.height));
			jframe.invalidateTree();
			jframe.repackContainer();
			if (jframe instanceof JInternalFrame) {
				try {
					((JInternalFrame) jframe).setSelected(true);
				} catch (PropertyVetoException e) {
				}
			} else {
				jframe.toFront();
			}
		}
		setPosition(0, 0);
	}

  	private Rectangle getFrameRectangle(int dw, int dh) {
  		Rectangle r = ((JSComponent) rpc).getBounds();			
		// from mouse release
		if (r.width + dw > minWidth)
			r.width += dw;
		if (r.height + dh > minHeight)
			r.height += dh;
		return r;
	}

    /**
     * Embedded frames can do this
     * 
     * @param b
     */
    public void setAllowResize(boolean b) {
    	allowResize = b;
    }
    
	public void setEnabled(boolean b) {
		enabled = b;
		if (b)
			show();
		else
			hide();
	}

	private boolean isSet;

	/**
	 * This method propertly assigns touch events and allows for CSS definitions of the resizer square and rubberband.
	 * 
	 * @param minWidth
	 * @param minHeight
	 * @param resizerNodeStyles
	 * @param rubberBandStyles
	 * @param useAI
	 * @author ChatGPT+, modified by Bob Hanson
	 */
	public void setFrameResizer(int minWidth, int minHeight, String[] resizerNodeStyles, String[] rubberBandStyles, boolean useAI) {
		// only once
		if (minWidth > 0)
			this.minWidth = minWidth;
		if (minHeight > 0)
			this.minHeight = minHeight;
		try {
			show();
			if (useAI) {
				DOMNode.setStyles(resizer, defaultAltResizerNodeStyles);
				DOMNode.setStyles(rubberBand, defaultAltRubberBandStyles);
			} else {
				DOMNode.setStyles(resizer, defaultResizerNodeStyles);
				DOMNode.setStyles(rubberBand, defaultRubberBandStyles);
			}
			if (resizerNodeStyles != null)
				DOMNode.setStyles(resizer, resizerNodeStyles);				
			if (rubberBandStyles != null)
				DOMNode.setStyles(rubberBand, rubberBandStyles);				
			// Attach dedicated touch listeners with passive: false 
			// to prevent iOS Safari scrolling	
			if(useAI && !isSet)
				setAltTouchFunctions(resizer, rubberBand);	
			isSet = true;
		} catch (Throwable t) {
			System.err.println("Resizer failed " + t);
		}
	}

	@SuppressWarnings("unused")
	private void setAltTouchFunctions(DOMNode resizerNode, DOMNode rubberBandNode) {
		Resizer myResizer = this;
		DOMNode node = rootNode;
		int mouseDRAGGED = MouseEvent.MOUSE_DRAGGED;
		int mouseUP = MouseEvent.MOUSE_RELEASED;
		Object capture = /** @j2sNative { passive: false, capture: true } || */null;
		JSFunction touchStart = null;
		Point pt0 = new Point();

		/** @j2sNative				
	    var handle = function(ev, touch, eventType) {
	       ev.preventDefault();
	       ev.stopPropagation();
	       var dx = touch.pageX - pt0.x;
	       var dy = touch.pageY - pt0.y;
	       myResizer.fHandleResizer(null, dx, dy, eventType);	       
	    };
	       
	    var touchStart = function(e) {
	       if (!e.touches || e.touches.length === 0) return;
	       var touch = e.touches[0];
	       pt0.x = touch.pageX;
	       pt0.y = touch.pageY; 
	       handle(e, touch, mouseDRAGGED);       
	       node.addEventListener("touchmove", onTouchMove, capture);
	       node.addEventListener("touchend", onTouchEnd, capture);
	       node.addEventListener("touchcancel", onTouchEnd, capture);
	    };

        var onTouchMove = function(me) {
          if (!me.touches || me.touches.length === 0) return;
          handle(me, me.touches[0], mouseDRAGGED);
        };
       
        var onTouchEnd = function(ue) {
          var touch = (ue.changedTouches && ue.changedTouches.length > 0 ? ue.changedTouches[0] : touch0);
          handle(ue, touch, mouseUP);
          node.removeEventListener("touchmove", onTouchMove, capture);
          node.removeEventListener("touchend", onTouchEnd, capture);
          node.removeEventListener("touchcancel", onTouchEnd, capture);	         
       };       
       */
		resizer.addEventListener("touchStart", touchStart, capture);
	}

}
