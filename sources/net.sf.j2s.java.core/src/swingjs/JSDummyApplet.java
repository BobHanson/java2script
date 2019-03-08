package swingjs;

import javax.swing.JApplet;

/**
 * JSApplet -- a dummy applet for running a main method in a general class 
 * @author Bob Hanson
 * 
 */
public class JSDummyApplet extends JApplet {

	public Class<?> runMain(String className, String[] args) {
		Class<?> theClass = null;
		try {
			theClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Running main but cannot find class " + className);
			JSUtil.alert("Cannot find class " + className + " for running main(args)");
			e.printStackTrace();
			return null;
		}
		System.out.println("Running main(args) in class " + className);
	  /**
	   * @j2sNative
	   * 
	   * setTimeout(function(){theClass.$clazz$.main$SA.call(null, args || []);},1);
	   * 
	   */
		{
		}
		return theClass;			
	}
}