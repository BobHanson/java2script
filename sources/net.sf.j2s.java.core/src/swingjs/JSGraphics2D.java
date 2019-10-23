package swingjs;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import swingjs.api.js.DOMNode;
import swingjs.api.js.HTML5Canvas;
import swingjs.api.js.HTML5CanvasContext2D;
import swingjs.api.js.HTML5CanvasContext2D.ImageData;

// BH 9/18/2018 fill3DRect fix
// BH 6/2018 adds g.copyArea(x,y,width,height,dx,dy)

/**
 * generic 2D drawing methods -- JavaScript version
 * 
 * @author Bob Hanson hansonr@stolaf.edu
 */

@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class JSGraphics2D implements
		// Graphics2D,
		Cloneable {

	private static final int DRAW_NOCLOSE = 0;
	private static final int DRAW_CLOSE = 1;
	private static final int FILL = 2;

//	private boolean backgroundPainted;

	public int constrainX;
	public int constrainY;

	private int width;
	private int height;
	private HTML5Canvas canvas;

	private HTML5CanvasContext2D ctx;
	private GraphicsConfiguration gc;

	private BasicStroke currentStroke;
	private Shape currentClip;

	private AlphaComposite alphaComposite;
	private int initialState;

	// public int strokeState;
	// public int transformState;
	// public int clipState;

	// public Color foregroundColor;
	boolean isShifted;// private, but only JavaScript
	private Font font;

	private RenderingHints hints;

	private AffineTransform transform;

	private Color backgroundColor;

	// private Color currentColor;

	private static float pixelRatio = /**
										 * @j2sNative (function () { var ctx =
										 *            document.createElement("canvas").getContext("2d"), dpr =
										 *            window.devicePixelRatio || 1, bsr =
										 *            ctx.webkitBackingStorePixelRatio || ctx.mozBackingStorePixelRatio
										 *            || ctx.msBackingStorePixelRatio || ctx.oBackingStorePixelRatio ||
										 *            ctx.backingStorePixelRatio || 1; return dpr / bsr; })() ||
										 */
			1;
	static {
		System.out.println("JSGraphics2D pixelRatio is " + pixelRatio);
	}
// this is 1.5 for Windows
// nice, but now how would be do raw pixel setting, say, from images?
//    can.width = w * pixelRatio;
//    can.height = h * pixelRatio;
//    can.getContext("2d").setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);

	public JSGraphics2D(Object canvas) { // this must be Object, because we are
											// passing an actual HTML5 canvas
		hints = new RenderingHints(new Hashtable());
		this.canvas = (HTML5Canvas) canvas;
		ctx = this.canvas.getContext("2d");
		transform = new AffineTransform();
		setStroke(new BasicStroke());
		/**
		 * @j2sNative
		 * 
		 * 			this.gc = SwingJS; this.width = canvas.width; this.height =
		 *            canvas.height;
		 * 
		 */
		{
		}
//		// reduce antialiasing, thank you,
//		// http://www.rgraph.net/docs/howto-get-crisp-lines-with-no- antialias.html
		setAntialias(true);
		setClip(0, 0, width, height);
	}

	public void setAntialias(boolean tf) {
//		if (tf) {
//			if (!isShifted)
//				ctx.translate(0.5, 0.5);
//		} else {
//			if (isShifted)
//				ctx.translate(-0.5, -0.5);
//		}
		// this is important if images are being drawn - test/TAPP6
		// see also
		// http://vaughnroyko.com/state-of-nearest-neighbor-interpolation-in-canvas/
		/**
		 * @j2sNative
		 * 
		 * 			this.ctx.mozImageSmoothingEnabled = false;
		 *            this.ctx.webkitImageSmoothingEnabled = false;
		 *            this.ctx.msImageSmoothingEnabled = false;
		 *            this.ctx.imageSmoothingEnabled = false;
		 * 
		 */
		isShifted = tf;
	}

	/**
	 * the SwingJS object
	 */
	public GraphicsConfiguration getDeviceConfiguration() {
		return gc;
	}

	public void drawLine(int x0, int y0, int x1, int y1) {
		boolean inPath = this.inPath;
		if (!inPath) {
			if (x0 == x1 && y0 == y1) {
				// meaning is to draw a point
				ctx.fillRect(x0, y0, 1, 1);
				return;
			}
			doStroke(true);
		}
		ctx.moveTo(x0, y0);
		ctx.lineTo(x1, y1);
		if (!inPath)
			doStroke(false);
	}

	public void drawOval(int left, int top, int width, int height) {
		doStroke(true);
		if (width == height)
			doCirc(left, top, width);
		else
			doArc(left, top, width, height, 0, 360, DRAW_NOCLOSE);
		doStroke(false);
	}

	public void fillOval(int left, int top, int width, int height) {
		ctx.beginPath();
		if (width == height)
			doCirc(left, top, width);
		else
			doArc(left, top, width, height, 0, 360, DRAW_CLOSE);
		ctx.fill();
	}

	private void doCirc(int left, int top, int diameter) {
		if (diameter <= 0)
			return;
		double r = diameter / 2f;
		ctx.arc(left + r, top + r, r, 0, 2 * Math.PI, false);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		doStroke(true);
		doArc(x, y, width, height, startAngle, arcAngle, DRAW_NOCLOSE);
		doStroke(false);
	}

	public void fillArc(int centerX, int centerY, int width, int height, int startAngle, int arcAngle) {
		ctx.beginPath();
		doArc(centerX, centerY, width, height, startAngle, arcAngle, DRAW_CLOSE);
		ctx.fill();
	}

	private void doArc(double x, double y, double width, double height, double startAngle, double arcAngle, int mode) {
		if (width <= 0 || height <= 0)
			return;
		// boolean doClose = (arcAngle - startAngle == 360);
		ctx.save();
		{
			if (arcAngle < 0) {
				startAngle += arcAngle;
				arcAngle = -arcAngle;
			}
			ctx.translate(x, y);
			ctx.scale(width / 2, height / 2);
			ctx.beginPath();
			ctx.arc(1, 1, 1, toRad(360 - startAngle), toRad(360 - arcAngle - startAngle), true);
			if (mode == DRAW_CLOSE)
				ctx.lineTo(1, 1);
		}
		ctx.restore();
	}

	private double toRad(double a) {
		return a * (Math.PI / 180);
	}

	public void clearRect(int x, int y, int width, int height) {
//		backgroundPainted = true;
		clearRectPriv(x, y, width, height);
	}

	private void clearRectPriv(int x, int y, int w, int h) {
		ctx.clearRect(x, y, w, h);
		if (!clearing)
			setGraphicsColor(backgroundColor == null ? Color.WHITE : backgroundColor);
		fillRect(x, y, w, h);
		if (!clearing)
			setGraphicsColor(foregroundColor);
	}

	public void drawPolygon(Polygon p) {
		// from Graphics
		doPoly(p.xpoints, p.ypoints, p.npoints, DRAW_CLOSE);
	}

	public void drawPolygon(int[] axPoints, int[] ayPoints, int nPoints) {
		doPoly(axPoints, ayPoints, nPoints, DRAW_CLOSE);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		doPoly(xPoints, yPoints, nPoints, DRAW_NOCLOSE);
	}

	public void fillPolygon(Polygon p) {
		// from Graphics
		doPoly(p.xpoints, p.ypoints, p.npoints, FILL);
	}

	public void fillPolygon(int[] axPoints, int[] ayPoints, int nPoints) {
		doPoly(axPoints, ayPoints, nPoints, FILL);
	}

	/**
	 * @param axPoints
	 * @param ayPoints
	 * @param nPoints
	 * @param doFill
	 */
	private void doPoly(int[] axPoints, int[] ayPoints, int nPoints, int mode) {
		ctx.beginPath();
		if (mode != FILL)
			ctx.translate(0.5, 0.5);
		ctx.moveTo(axPoints[0], ayPoints[0]);
		for (int i = 1; i < nPoints; i++) {
			ctx.lineTo(axPoints[i], ayPoints[i]);
		}
		if (mode != DRAW_NOCLOSE)
			ctx.lineTo(axPoints[0], ayPoints[0]);
		if (mode != FILL) {
			ctx.stroke();
			ctx.translate(-0.5, -0.5);
		} else {
			ctx.fill();
		}
	}

	public void drawRect(int x, int y, int width, int height) {
		// from Graphics
		if (width <= 0 || height <= 0)
			return;
		ctx.translate(0.5, 0.5);
		ctx.beginPath();
		ctx.rect(x, y, width, height);
		ctx.stroke();
		ctx.translate(-0.5, -0.5);
	}

	public void fillRect(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0)
			return;
//		backgroundPainted = true;
		ctx.fillRect(x, y, width, height);
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		do3DRect(x, y, width, height, raised, DRAW_CLOSE);
	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		do3DRect(x, y, width, height, raised, FILL);
	}

	private void do3DRect(int x, int y, int width, int height, boolean raised, int mode) {
		if (width <= 0 || height <= 0)
			return;
		Paint p = getPaint();
		Color c = getColor();
		Color brighter = c.brighter();
		Color darker = c.darker();

		if (!raised) {
			setColor(darker);
		} else if (p != c) {
			setColor(c);
		}
		if (mode == FILL)
			fillRect(x + 1, y + 1, width - 2, height - 2);
		setColor(raised ? brighter : darker);
		// drawLine(x, y, x, y + height - 1);
		fillRect(x, y, 1, height);
		// drawLine(x + 1, y, x + width - 2, y);
		fillRect(x + 1, y, width - 2, 1);
		setColor(raised ? darker : brighter);
		// drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		fillRect(x + 1, y + height - 1, width - 1, 1);
		// drawLine(x + width - 1, y, x + width - 1, y + height - 2);
		fillRect(x + width - 1, y, 1, height - 1);
		setPaint(p);
	}

	public void setFont(Font font) {
		// this equality check speeds mark/reset significantly
		if (font == this.font)
			return;
		this.font = font;
		if (font != null)
			ctx.font = JSToolkit.getCanvasFont(font);
	}

	public void setStrokeBold(boolean tf) {
		setLineWidth(tf ? 2. : 1.);
	}

	private void setLineWidth(double d) {
		ctx.lineWidth = d;
	}

	public boolean canDoLineTo() {
		return true;
	}

	boolean inPath;

	private Color foregroundColor;

	public void doStroke(boolean isBegin) {
		inPath = isBegin;
		if (isBegin) {
			ctx.translate(0.5, 0.5);
			ctx.beginPath();
		} else {
			ctx.stroke();
			ctx.translate(-0.5, -0.5);
		}
	}

	public void lineTo(int x2, int y2) {
		if (inPath) {
			ctx.lineTo(x2, y2);
		} else {
			ctx.translate(0.5, 0.5);
			ctx.lineTo(x2, y2);
			ctx.translate(-0.5, -0.5);
		}
	}

	public void clip(Shape s) {
		ctx.beginPath();
		doShape(s);
		currentClip = s;
		ctx.clip();
	}

	public void draw(Shape s) {
		doStroke(true);
		doShape(s);
		doStroke(false);
	}

	private int doShape(Shape s) {
		double[] pts = new double[6];
		PathIterator pi = s.getPathIterator(null);
		while (!pi.isDone()) {
			switch (pi.currentSegment(pts)) {
			case PathIterator.SEG_MOVETO:
				ctx.moveTo(pts[0], pts[1]);
				break;
			case PathIterator.SEG_LINETO:
				ctx.lineTo(pts[0], pts[1]);
				break;
			case PathIterator.SEG_QUADTO:
				ctx.quadraticCurveTo(pts[0], pts[1], pts[2], pts[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
				break;
			case PathIterator.SEG_CLOSE:
				ctx.closePath();
				break;
			}
			pi.next();
		}
		return pi.getWindingRule();
		// then fill or stroke or clip
	}

	public void fill(Shape s) {
		ctx.beginPath();
		if (doShape(s) == Path2D.WIND_EVEN_ODD)
		/**
		 * @j2sNative
		 * 
		 * 			this.ctx.fill("evenodd");
		 */
		{
		} else
			ctx.fill();
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		doRoundRect(x, y, width, height, arcWidth, arcHeight, DRAW_CLOSE);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		doRoundRect(x, y, width, height, arcWidth, arcHeight, FILL);
	}

	RoundRectangle2D.Double rrect;

	private void doRoundRect(int x, int y, int w, int h, int aw, int ah, int mode) {
		if (rrect == null || rrect.getX() != x || rrect.getY() != y || rrect.getWidth() != w || rrect.getHeight() != h
				|| rrect.getArcWidth() != aw || rrect.getArcHeight() != ah) {
			rrect = new RoundRectangle2D.Double(x, y, w, h, aw, ah);
		}
		if (mode == FILL)
			fill(rrect);
		else
			draw(rrect);
	}

	/**
	 * Allows for direct image transfer by buffer
	 * @param img
	 * @param x
	 * @param y
	 * @param observer
	 * @return
	 */
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return drawImagePriv(img, x, y, observer);
	}

	public boolean drawImage(Image img, int x, int y, Color background, ImageObserver observer) {
		JSUtil.notImplemented("transparent image pixel background fill is not supported in SwingJS");
		return drawImagePriv(img, x, y, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (width <= 0 || height <= 0)
			return true;
//		backgroundPainted = true;
		if (img != null) {
			DOMNode imgNode = getImageNode(img);
			if (imgNode != null)
				ctx.drawImage(imgNode, x, y, width, height);
			if (observer != null)
				observe(img, observer, imgNode != null);
		}
		return true;
	}

	private DOMNode getImageNode(Image img) {
//		backgroundPainted = true;
		DOMNode imgNode = DOMNode.getImageNode(img);
		return (imgNode == null ? JSGraphicsCompositor.createImageNode(img) : imgNode);
	}

	private void observe(Image img, ImageObserver observer, boolean isOK) {
		observer.imageUpdate(img, (isOK ? 0 : ImageObserver.ABORT | ImageObserver.ERROR), -1, -1, -1, -1);
	}

//	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
////		backgroundPainted = true;
//		return drawImage(img, x, y, observer);
//	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		if (width <= 0 || height <= 0)
			return false;
//		backgroundPainted = true;
		return drawImage(img, x, y, width, height, observer);
	}

	@SuppressWarnings("unused")

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
//		backgroundPainted = true;
		if (img != null) {
			byte[] bytes = null;
			DOMNode imgNode = getImageNode(img);
			if (imgNode != null)
				ctx.drawImage(imgNode, sx1, sy1, sx2 - sx1, sy2 - sy1, dx1, dy1, dx2 - dx1, dy2 - dy1);
			if (observer != null)
				observe(img, observer, imgNode != null);
		}
		return true;
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		JSUtil.notImplemented(null);
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return drawImageXT(img, xform, obs);
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		drawImageXT((Image) img, xform, null);
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		drawImageXT((Image) img, xform, null);
	}

	private boolean drawImageXT(Image img, AffineTransform xform, ImageObserver obs) {
		ctx.save();
		transformCTX(xform);
		boolean ret = drawImagePriv(img, 0, 0, obs);
		ctx.restore();
		return ret;
	}

	private ImageData imageData;
	private int[] buf8;
	private int lastx, lasty, nx, ny;

	@SuppressWarnings("unused")
	public boolean drawImagePriv(Image img, int x, int y, ImageObserver observer) {
//		backgroundPainted = true;
		if (img != null) {
			int[] pixels = null;
			boolean isRGB = false; // usually RGBA
			/**
			 * @j2sNative
			 * 
			 * 			pixels = img.raster.秘pix ||img.秘pix; isRGB = (img.imageType == 1);
			 *
			 */
			DOMNode imgNode = null;
			int width = ((BufferedImage) img).getRaster().getWidth();
			int height = ((BufferedImage) img).getRaster().getHeight();

			if (pixels == null) {
				if ((imgNode = getImageNode(img)) != null)
					ctx.drawImage(imgNode, x, y, width, height);
			} else {
				drawDirect(pixels, x, y, width, height, isRGB);
			}
			if (observer != null)
				observe(img, observer, imgNode != null);
		}
		return true;
	}

	public void drawDirect(int[] pixels, int x, int y, int width, int height, boolean isRGB) {
		if (buf8 == null || x != lastx || y != lasty || nx != width || ny != height) {
			imageData = ctx.getImageData(x, y, width, height);
			buf8 = imageData.data;
			lastx = x;
			lasty = y;
			nx = width;
			ny = height;
		}
		double[] m = new double[6];
		transform.getMatrix(m);
		if (m[0] != 1 || m[1] != 0 || m[2] != 0 || m[3] != 1)
			System.err.println("Unsupported transform");
		x += m[4];
		y += m[5];
		for (int pt = 0, i = 0, n = Math.min(buf8.length / 4, pixels.length); i < n; i++) {
			int argb = pixels[i];
			buf8[pt++] = (argb >> 16) & 0xFF;
			buf8[pt++] = (argb >> 8) & 0xFF;
			buf8[pt++] = argb & 0xFF;
			buf8[pt++] = (isRGB ? 0xFF : (argb >> 24) & 0xFF);
		}
		double[] m2 = HTML5CanvasContext2D.getMatrix(ctx, transform);
		if (m2[0] != 1 || m2[1] != 0 || m2[2] != 0 || m2[3] != 1)
			System.err.println("Unsupported transform");
		x += m2[4];
		y += m2[5];
		ctx.putImageData(imageData, x, y);
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		JSUtil.notImplemented(null);
		return false;
	}

	public Stroke getStroke() {
		return currentStroke;
	}

	@SuppressWarnings("unused")

	public void setStroke(Stroke s) {
		if (!(s instanceof BasicStroke))
			return;
		BasicStroke b = currentStroke = (BasicStroke) s;
		float[] dash = b.getDashArray();
		int[] idash = new int[dash == null ? 0 : dash.length];
		for (int i = idash.length; --i >= 0;)
			idash[i] = (int) dash[i];
		ctx.setLineDash(idash);
		setLineWidth(b.getLineWidth());
		String lineCap, lineJoin;
		float miterLimit = -1;
		switch (b.getEndCap()) {
		case BasicStroke.CAP_BUTT:
			lineCap = "butt";
			break;
		case BasicStroke.CAP_SQUARE:
			lineCap = "square";
			break;
		case BasicStroke.CAP_ROUND:
		default:
			lineCap = "round";
		}
		switch (b.getLineJoin()) {
		case BasicStroke.JOIN_BEVEL:
			lineJoin = "bevel";
			break;
		case BasicStroke.JOIN_MITER:
			lineJoin = "miter";
			miterLimit = b.getMiterLimit();
			break;
		case BasicStroke.JOIN_ROUND:
			lineJoin = "round";
		}
		/**
		 * @j2sNative
		 * 
		 * 			this.ctx.lineCap = lineCap; this.ctx.lineJoin = lineJoin; if
		 *            (miterLimit >= 0) this.ctx.miterLimit = miterLimit;
		 */
		{
		}
		// SwingJS TODO more here
	}

	public void setRenderingHint(Key hintKey, Object hintValue) {
		hints.put(hintKey, hintValue);
	}

	public Object getRenderingHint(Key hintKey) {
		return hints.get(hintKey);
	}

	public void setRenderingHints(Map<?, ?> hints) {
		this.hints = new RenderingHints((Map<Key, ?>) hints);
	}

	public void addRenderingHints(Map<?, ?> hints) {
		for (Entry<?, ?> e : hints.entrySet())
			this.hints.put(e.getKey(), e.getValue());
	}

	public RenderingHints getRenderingHints() {
		return hints;
	}

	public void setBackground(Color c) {
		// for clearRect only
			backgroundColor = c;
	}

	public Color getBackground() {
		return backgroundColor;
	}

	public Color getColor() {
		return foregroundColor;
	}

	public void setColor(Color c) {
		if (clearing) {
			clearColorSaved = c;
		} else {
			foregroundColor = c;
			setGraphicsColor(c);
		}
	}

	public void setPaint(Paint paint) {
		setColor((Color) paint);
	}

	public Font getFont() {
		if (font == null)
			font = new Font("Arial", Font.PLAIN, 12);
		return font;
	}

	public FontMetrics getFontMetrics() {
		// from Graphics
		return getFontMetrics(getFont());
	}

	public FontMetrics getFontMetrics(Font f) {
		return Toolkit.getDefaultToolkit().getFontMetrics(f);
	}

	public void clipRect(int x, int y, int width, int height) {
		// SwingJS -- this is not quite right. Should ADD this to the clipping
		// region
		ctx.beginPath();
		ctx.rect(x, y, width, height);
		setCurrentClip(x, y, width, height);
		ctx.clip();
	}

	public void setClip(int x, int y, int width, int height) {
		// clipping is disabled because for general component painting
		// because it consumes so much processing.
		// it is presumed that the user will clip when desired
		setCurrentClip(x, y, width, height);
		ctx.beginPath();
		ctx.rect(x, y, width, height);
		ctx.clip();
	}

	private void setCurrentClip(int x, int y, int width, int height) {
		Rectangle r = (currentClip instanceof Rectangle ? (Rectangle) currentClip : null);
		if (r == null || r.x != x || r.y != y || r.width != width || r.height != height)
			currentClip = new Rectangle(x, y, width, height);
	}

	public void setClip(Shape clip) {
		if (clip == null) {
			setClip(0, 0, width, height);
			return;
		}
		currentClip = clip;
		ctx.beginPath();
		doShape(clip);
		ctx.clip();
	}

	public boolean hitClip(int x, int y, int width, int height) {
		// from Graphics

		// Note, this implementation is not very efficient.
		// Subclasses should override this method and calculate
		// the results more directly.
		Rectangle clipRect = getClipBounds();
		if (clipRect == null) {
			return true;
		}
		return clipRect.intersects(x, y, width, height);
	}

	private int alpha;
	private void setGraphicsColor(Color c) {
		if (c == null)
			return; // this was the case with a JRootPanel graphic call
		int a = c.getAlpha();
		// set alpha only if it is new and if this color has an alpha not 0xFF
		if (a != alpha && a != 255)
			ctx.globalAlpha = (alpha = a) / 256F;
		ctx.fillStyle = ctx.strokeStyle = JSToolkit.getCSSColor(c);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		ctx.putImageData(ctx.getImageData(x, y, width, height), x + dx, y + dy);
	}

	public Shape getClip() {
		return currentClip == null ? getClipBoundsImpl() : currentClip;
	}

	public void drawString(String s, int x, int y) {
		fillText(s, x, y);
	}

	public void drawChars(char[] chars, int x, int y) {
		fillText(String.valueOf(chars), x, y);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		// from Graphics
		drawString(new String(data, offset, length), x, y);
	}

	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		// from Graphics
		drawString(new String(data, offset, length), x, y);
	}

	public void drawString(String str, float x, float y) {
		fillText(str, x, y);
	}

	private void fillText(String str, float x, float y) {
		ctx.fillText(str, x, y);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		JSUtil.notImplemented(null);
	}

	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		JSUtil.notImplemented(null);
	}

	public void translate(double tx, double ty) {
		JSUtil.notImplemented(null);
	}

	public void shear(double shx, double shy) {
		JSUtil.notImplemented(null);
	}

	public void translate(int x, int y) {
		ctx.translate(x, y);
		transform.translate(x, y);
	}

	public void rotate(double radians) {
		ctx.rotate(radians);
		transform.rotate(radians);
	}

	public void rotate(double theta, double x, double y) {
		ctx.translate(x, y);
		ctx.rotate(theta);
		ctx.translate(-x, -y);
		transform.rotate(theta, x, y);
	}

	public void scale(double sx, double sy) {
		ctx.scale(sx, sy);
		transform.scale(sx, sy);
	}

	/**
	 * concatenates the given transform matrix to the current transform
	 * 
	 */

	public void transform(AffineTransform t) {
		transformCTX(t);
		transform.concatenate(t);
	}

	private void transformCTX(AffineTransform t) {
		/**
		 * @j2sNative
		 * 
		 * 			this.ctx.transform (t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
		 */
		{
		}
	}

	/**
	 * sets the transform matrix to the given one
	 */

	public void setTransform(AffineTransform t) {
		/**
		 * @j2sNative
		 * 
		 * 			this.ctx.setTransform (t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
		 * 
		 * 
		 */
		{
		}
		transform.setTransform(t);
	}

	/**
	 * Returns a copy of the current transform
	 */

	public AffineTransform getTransform() {
		return (AffineTransform) transform.clone();
	}

	// /**
	// * Returns the current Transform ignoring the "constrain"
	// * rectangle.
	// */
	// public AffineTransform cloneTransform() {
	// return (AffineTransform) transform.clone();
	// }
	//

	public Paint getPaint() {
		return getColor();
	}

	public FontRenderContext getFontRenderContext() {
		return getFontMetrics(getFont()).getFontRenderContext();
	}

	public void setPaintMode() {
		setComposite(AlphaComposite.SrcOver);
	}

	public void setXORMode(Color c) {
		if (c == null)
			throw new IllegalArgumentException("null XORColor");
		setComposite(AlphaComposite.Xor);
	}

	public Rectangle getClipRect() {
		// from Graphics
		return getClipBounds();
	}

	public Rectangle getClipBounds() {
		return getClipBounds(null);
	}

	public Rectangle getClipBounds(Rectangle r) {
		Rectangle clipRect = getClipBoundsImpl();
		if (r == null) {
			r = clipRect;
		} else {
			r.x = clipRect.x;
			r.y = clipRect.y;
			r.width = clipRect.width;
			r.height = clipRect.height;
		}
		return r;
	}

	private Rectangle getClipBoundsImpl() {
		if (currentClip == null) {
			currentClip = new Rectangle(0, 0, width, height);
		}
		return currentClip.getBounds();
	}

	private Color clearColorSaved;
	private boolean clearing;

	/**
	 * 
	 * @param comp AlphaComposite or XORComposite
	 */
	public void setComposite(Composite comp) {
		// TODO -- XORComposite
		// this equality check speeds mark/reset significantly
		if (comp == this.alphaComposite)
			return;
		// alpha composite only here
		boolean isAlpha = comp instanceof AlphaComposite;
		int newRule = (!isAlpha ? 0 : ((AlphaComposite) comp).getRule());
		boolean isValid = (isAlpha && alphaComposite == null || newRule != alphaComposite.getRule());
		if (isValid && JSGraphicsCompositor.setGraphicsCompositeAlpha(this, newRule)) {
			alphaComposite = (AlphaComposite) comp;
		}
		if (newRule == AlphaComposite.CLEAR) {
			clearColorSaved = foregroundColor;
			setColor(Color.black);
			clearing = true;
		} else if (clearing) {
			clearing = false;
			setColor(clearColorSaved);
			clearColorSaved = null;
		}

		setAlpha(comp == null ? 1 : ((AlphaComposite) comp).getAlpha());
	}

	public Composite getComposite() {
		return alphaComposite;
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		JSGraphicsCompositor.drawImageOp(this, img, op, x, y);
	}

	public void setAlpha(float f) {
		ctx.globalAlpha = f;
		alpha = (int) Math.floor(f * 256 + 0.0039);
	}

	public HTML5Canvas getCanvas() {
		return canvas;
	}

//	/**
//	 * used to determine if it is likely that the background was painted
//	 * 
//	 * @return background taint count
//	 */
//	public boolean isBackgroundPainted() {
//		return backgroundPainted;
//	}

	/////////////// saving of the state ////////////////

	private final static int SAVE_ALPHA = 0;
	private final static int SAVE_COMPOSITE = 1;
	private final static int SAVE_STROKE = 2;
	private final static int SAVE_TRANSFORM = 3;
	private final static int SAVE_FONT = 4;
	private final static int SAVE_CLIP = 5;
	private final static int SAVE_BACKGROUND_PAINTED = 6;
	private final static int SAVE_MAX = 7;

	/**
	 * creates a save object to extend the capabilities of context2d.save() that
	 * brings that into line with Java's graphics2d .create()
	 * 
	 * in development -- we need to identify all differences
	 * 
	 * 
	 * @return the length of the ctx._aSaved array after the push
	 */
	public int mark() {
		// note: This method is referred to in JComponent.java j2snative block as mark$
		ctx.save();
		Object[] map = new Object[SAVE_MAX];
		map[SAVE_ALPHA] = Float.valueOf(ctx.globalAlpha);
		map[SAVE_COMPOSITE] = alphaComposite;
		map[SAVE_STROKE] = currentStroke;
		map[SAVE_TRANSFORM] = transform;
		map[SAVE_FONT] = font;
		map[SAVE_CLIP] = currentClip;
//		map[SAVE_BACKGROUND_PAINTED] = (backgroundPainted ? Boolean.TRUE : Boolean.FALSE);
//		backgroundPainted = false;
		return HTML5CanvasContext2D.push(ctx, map);
	}

	/**
	 * try to equate g.dispose() with ctx.restore().
	 * 
	 * @param n0
	 */
	public void reset(int n0) {
		if (n0 < 1)
			n0 = 1;
		while (HTML5CanvasContext2D.getSavedLevel(ctx) >= n0) {
			Object[] map = HTML5CanvasContext2D.pop(ctx);
			setComposite((Composite) map[SAVE_COMPOSITE]);
			Float alpha = (Float) map[SAVE_ALPHA];
			if (alpha != null) {
				setAlpha(alpha.floatValue());
			}
			setStroke((Stroke) map[SAVE_STROKE]);
			setTransform((AffineTransform) map[SAVE_TRANSFORM]);
			setFont((Font) map[SAVE_FONT]);
			currentClip = (Shape) map[SAVE_CLIP];
//			backgroundPainted = ((Boolean) map[SAVE_BACKGROUND_PAINTED]).booleanValue();
			ctx.restore();
		}
	}

	public Graphics create(int x, int y, int width, int height) {
		// from Graphics
		// cell renderer pane and JComponent
		Graphics g = create();
		if (g == null)
			return null;
		g.translate(x, y);
		g.clipRect(0, 0, width, height);
		return g;
	}

	public Graphics create() {
		return (Graphics) clone();
	}

	@Override
	public Object clone() {
		int n = mark();
		JSGraphics2D g = this;
		/**
		 * avoid super call to Object.clone();
		 * 
		 * @j2sNative
		 * 
		 * 			g = Clazz.clone(this);
		 * 
		 */
		{
		}
		g.transform = new AffineTransform(transform);
		if (hints != null) {
			g.hints = (RenderingHints) hints.clone();
		}
		/*
		 * FontInfos are re-used, so must be cloned too, if they are valid, and be
		 * nulled out if invalid. The implied trade-off is that there is more to be
		 * gained from re-using these objects than is lost by having to clone them when
		 * the SG2D is cloned.
		 */
		// if (this.fontInfo != null) {
		// if (this.validFontInfo) {
		// g.fontInfo = (FontInfo)this.fontInfo.clone();
		// } else {
		// g.fontInfo = null;
		// }
		// }
		// if (this.glyphVectorFontInfo != null) {
		// g.glyphVectorFontInfo =
		// (FontInfo)this.glyphVectorFontInfo.clone();
		// g.glyphVectorFRC = this.glyphVectorFRC;
		// }
		// g.invalidatePipe();
		g.setStroke((BasicStroke) currentStroke.clone());
		g.initialState = n;
		return g;
	}

	public void dispose() {
		reset(initialState);
	}

}
