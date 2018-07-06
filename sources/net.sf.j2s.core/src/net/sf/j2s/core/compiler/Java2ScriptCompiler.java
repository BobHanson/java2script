package net.sf.j2s.core.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import net.sf.j2s.core.astvisitors.Java2ScriptVisitor;
import net.sf.j2s.core.builder.SourceFile;
import net.sf.j2s.core.builder.SourceFileProxy;
import net.sf.j2s.core.hotspot.InnerHotspotServer;

/**
 * The main (and currently only operational) Java2Script compiler. 
 * 
 * @author Bob Hanson
 *
 */
@SuppressWarnings("restriction")
public class Java2ScriptCompiler implements IExtendedCompiler {

	// BH: added "true".equals(getProperty(props, "j2s.compiler.allow.compression")) to ensure compression only occurs when desired
    static final int JSL_LEVEL = AST.JLS8; // BH can we go to JSL 8? 

    private static boolean showJ2SSettings = true;
    
    // We copy all non .java files from any directory from which we loaded a java file into the site directory
	private static HashSet<String> copyResources = new HashSet<String>();
	private static Map<String,String>htMethodsCalled;
	private static List<String>lstMethodsDeclared;
	
	private static Properties props;
	private static String htmlTemplate = null;

	public void process(ICompilationUnit sourceUnit, IContainer binaryFolder) {
		final IProject project = binaryFolder.getProject();
		/*
		 * 
		 * synchronized (project) { if
		 * (Java2ScriptProjectNature.hasJavaBuilder(project)) { if
		 * (Java2ScriptProjectNature.removeJavaBuilder(project)) { new
		 * Thread(new Runnable() { public void run() { try { Thread.sleep(50); }
		 * catch (InterruptedException e1) { e1.printStackTrace(); } try {
		 * project.build(IncrementalProjectBuilder.CLEAN_BUILD, null); } catch
		 * (CoreException e) { e.printStackTrace(); } } }).start(); return; } }
		 * } //
		 */
		String projectFolder = project.getLocation().toOSString();
		File file = new File(projectFolder, ".j2s"); //$NON-NLS-1$
		if (!file.exists()) {
			/*
			 * w The file .j2s is a marker for Java2Script to compile JavaScript
			 */
			return;
		}
		props = new Properties();
		try {
			props.load(new FileInputStream(file));
			String status = getProperty("j2s.compiler.status");
			if (!"enable".equals(status)) {
				/*
				 * Not enabled!
				 */
				return;
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String siteFolder = getProperty("j2s.site.directory");
		if (siteFolder == null)
			siteFolder = "site";
		siteFolder = projectFolder + "/" + siteFolder;
		String j2sPath = siteFolder + "/swingjs/j2s";

		// method declarations and invocations are only logged
		// when the designated files are deleted prior to building

		String logDeclared = getProperty("j2s.log.methods.declared");
		if (logDeclared != null) {
			if (!(file = new File(projectFolder, logDeclared)).exists()) {
				lstMethodsDeclared = new ArrayList<String>();
				System.err.println("logging methods declared to " + file);
			}
			logDeclared = projectFolder + "/" + logDeclared;
		}
		boolean logAllCalls = false;

		String logCalled = getProperty("j2s.log.methods.called");
		if (logCalled != null) {
			if (!(file = new File(projectFolder, logCalled)).exists()) {
				htMethodsCalled = new Hashtable<String, String>();
				System.err.println("logging methods called to " + file);
			}
			logCalled = projectFolder + "/" + logCalled;
			logAllCalls = "true".equals(getProperty("j2s.log.all.calls"));
		}

		String excludedPaths = getProperty("j2s.excluded.paths");

		List<String> lstExcludedPaths = null;

		if (excludedPaths != null) {
			lstExcludedPaths = new ArrayList<String>();
			String[] paths = excludedPaths.split(";");
			for (int i = 0; i < paths.length; i++)
				if (paths[i].trim().length() > 0)
					lstExcludedPaths.add(paths[i].trim() + "/");
			if (lstExcludedPaths.size() == 0)
				lstExcludedPaths = null;
		}

		String nonqualifiedClasses = getProperty("j2s.compiler.nonqualified.classes");

		// includes @j2sDebug blocks
		boolean isDebugging = "debug".equals(getProperty("j2s.compiler.mode"));

		String classReplacements = getProperty("j2s.class.replacements");

		String htmlTemplateFile = getProperty("j2s.template.html");
		if (htmlTemplateFile == null)
			htmlTemplateFile = "template.html";

		if (htmlTemplate == null) {
			file = new File(projectFolder, htmlTemplateFile);
			if (!file.exists()) {
				String html = getDefaultHTMLTemplate();
				System.err.println("creating new htmltemplate\n" + html);
				writeToFile(file, html);
			}
			htmlTemplate = getFileContents(file);
			if (showJ2SSettings)
				System.err.println("using HTML template " + file);
		}

		Java2ScriptVisitor.setNoQualifiedNamePackages(nonqualifiedClasses);
		Java2ScriptVisitor.setDebugging(isDebugging);
		Java2ScriptVisitor.setClassReplacements(classReplacements);
		Java2ScriptVisitor.setLogging(lstMethodsDeclared, htMethodsCalled, logAllCalls);

		CompilationUnit root;
		ASTParser astParser = ASTParser.newParser(JSL_LEVEL);
		if (!(sourceUnit instanceof SourceFile))
			return;
		SourceFile unitSource = (SourceFile) sourceUnit;
		
		String fileName = new String(sourceUnit.getFileName());
		if (lstExcludedPaths != null) {
			for (int i = lstExcludedPaths.size(); --i >= 0;)
				if (fileName.startsWith(lstExcludedPaths.get(i)))
					return;
		}
		
		org.eclipse.jdt.core.ICompilationUnit createdUnit = JavaCore
				.createCompilationUnitFrom(new SourceFileProxy(unitSource).getResource());
		astParser.setResolveBindings(true);
		astParser.setSource(createdUnit);
		root = (CompilationUnit) astParser.createAST(null);
		Java2ScriptVisitor visitor = new Java2ScriptVisitor();

		try {

			// transpile the code

			root.accept(visitor);

			// generate the .js file(s) in the site directory
			
			outputJavaScript(visitor, j2sPath);
			
			logMethods(logCalled, logDeclared, logAllCalls);
			
			// add the HTML files in the site directory

			addHTML(visitor.getAppList(true), siteFolder, htmlTemplate, true);
			addHTML(visitor.getAppList(false), siteFolder, htmlTemplate, false);
		} catch (Throwable e) {
			e.printStackTrace();
			// find the file and delete it.
			String filePath = j2sPath;
			String rootName = root.getJavaElement().getElementName();
			rootName = rootName.substring(0, rootName.lastIndexOf('.'));
			String packageName = visitor.getPackageName();
			if (packageName != null) {
				File folder = new File(filePath, packageName.replace('.', File.separatorChar));
				filePath = folder.getAbsolutePath();
				File jsFile = new File(filePath, rootName + ".js"); //$NON-NLS-1$
				if (jsFile.exists()) {
					jsFile.delete();
				}
			}
		}
		String packageName = visitor.getPackageName();
		if (packageName != null) {
			int pt = packageName.indexOf(".");
			if (pt >= 0)
				packageName = packageName.substring(0, pt);
			if (!copyResources.contains(packageName)) {
				copyResources.add(packageName);
				File src = new File(projectFolder + "/src", packageName);
				File dest = new File(j2sPath, packageName);
				copySiteResources(src, dest);
			}
		}
	}

	private static void logMethods(String logCalled, String logDeclared, boolean doAppend) {
		if (htMethodsCalled != null)
			try {
				File file = new File(logCalled);
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file, doAppend);
				for (String key : htMethodsCalled.keySet()) {
					String val = htMethodsCalled.get(key);
					fos.write(key.getBytes());
					if (!val.equals("-")) {
						fos.write(',');
						fos.write(val.getBytes());
					}
					fos.write('\n');
				}
				fos.close();
			} catch (Exception e) {
				System.err.println("Cannot log to " + logCalled);
			}
		if (lstMethodsDeclared != null)
			try {
				File file = new File(logDeclared);
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file, true);
				for (int i = 0, n = lstMethodsDeclared.size(); i < n; i++) {
					fos.write(lstMethodsDeclared.get(i).getBytes());
					fos.write('\n');
				}
				fos.close();
			} catch (Exception e) {
				System.err.println("Cannot log to " + logDeclared);
			}
	}

//	/**
//	 *  untested
//	 *   
//	 * @param sourceUnit
//	 * @param binaryFolder
//	 */
//	@Deprecated
//	private void saveResourceLists(ICompilationUnit sourceUnit, IContainer binaryFolder) {
//	
//		String resPaths = getProperty("j2s.resources.list");
//		String abandonedPaths = getProperty("j2s.abandoned.resources.list");
//	
//		List<String> abandonedList = new ArrayList<String>();
//		List<String> list = new ArrayList<String>();
//		if (resPaths != null && resPaths.trim().length() > 0) {
//			String[] splits = resPaths.split(",");
//			for (int i = 0; i < splits.length; i++) {
//				list.add(splits[i]);
//			}
//		}
//		if (abandonedPaths != null && abandonedPaths.trim().length() > 0) {
//			String[] splits = abandonedPaths.split(",");
//			// list = Arrays.asList(splits);
//			for (int i = 0; i < splits.length; i++) {
//				abandonedList.add(splits[i]);
//			}
//		}
//		if (sourceUnit instanceof SourceFile) {
//			SourceFile unitSource = (SourceFile) sourceUnit;
//			String fileName = new String(unitSource.getFileName());
//			int idx = fileName.lastIndexOf('/');
//			String className = fileName.substring(idx + 1, fileName.lastIndexOf('.'));
//			StringBuffer path = new StringBuffer();
//			char[][] pkgs = unitSource.getPackageName();
//			for (int j = 0; j < pkgs.length; j++) {
//				path.append(new String(pkgs[j]));
//				path.append("/");
//			}
//			path.append(className);
//			path.append(".js");
//			String jsPath = binaryFolder.getProjectRelativePath().toPortableString() + "/" + path.toString();
//			if (!list.contains(jsPath) && !abandonedList.contains(jsPath)) {
//				list.add(jsPath);
//			}
//		}
//		StringBuffer buf = new StringBuffer();
//		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
//			String path = iter.next();
//			buf.append(path);
//			if (iter.hasNext()) {
//				buf.append(",");
//			}
//		}
//		//	try {
//		//		props.store(new FileOutputStream(file), "Java2Script Configuration");
//		//	} catch (IOException e) {
//		//		e.printStackTrace();
//		//	}
//		//
//	}

	private static String getProperty(String key) {
		String val = props.getProperty(key);
		if (showJ2SSettings)
			System.err.println(key + " = " + val);
		return val;
	}

	public static void outputJavaScript(Java2ScriptVisitor visitor, 
			//DependencyASTVisitor dvisitor, CompilationUnit fRoot,
			String j2sPath) {

		// fragments[0] is package]
		List<String> elements = visitor.getElementList();//dvisitor.getDependencyScript(visitor.getBuffer());

		// BH all compression is deprecated --- use Google Closure Compiler
				
		String packageName = visitor.getPackageName();
		for (int i = 0; i < elements.size();) {
			String elementName = elements.get(i++);
			String element = elements.get(i++);
			createJSFile(j2sPath, packageName, elementName, element);			
		}
		showJ2SSettings = false; // just once per compilation run
	}

	private static void createJSFile(String j2sPath, String packageName, String elementName, String js) {
		if (packageName != null) {
			File folder = new File(j2sPath, packageName.replace('.', File.separatorChar));
			j2sPath = folder.getAbsolutePath();
			if (!folder.exists() || !folder.isDirectory()) {
				if (!folder.mkdirs()) {
					throw new RuntimeException("Failed to create folder " + j2sPath); //$NON-NLS-1$
				}
			}
			InnerHotspotServer.addCompiledItem(packageName + "." + elementName);
		} else {
			InnerHotspotServer.addCompiledItem(elementName);
		}
		writeToFile(new File(j2sPath, elementName + ".js"), js);
	}

	private String getFileContents(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			FileInputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			return sb.toString();
		} catch (@SuppressWarnings("unused") IOException e) {
			//
		}
		return null;
	}

	private static void writeToFile(File file, String data) {
		if (data == null)
			return;
		try {
			FileOutputStream os = new FileOutputStream(file);
			os.write(data.getBytes("UTF-8"));			
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The default template file. The user can specify another in the .j2s file using template.html=.....
	 * 
	 * @return default template with _NAME_, _CODE_, and _MAIN_ to fill in.
	 */
	private String getDefaultHTMLTemplate() {
		String ret = "<!DOCTYPE html>\n<html>\n"
		+"<head>\n"
		+"<title>SwingJS test _NAME_</title>"
		+"<meta charset=\"utf-8\" />\n"
		+"<script src=\"swingjs/swingjs2.js\"></script>\n"
		+"<script>\n"
		+"if (!self.SwingJS)alert('swingjs2.js was not found. It needs to be in swingjs folder in the same directory as ' + document.location.href)\n"
		+"Info = {\n"
		+"  code: _CODE_,\n"
		+"  main: _MAIN_,\n"
		+"	width: 850,\n"
		+"	height: 550,\n"
		+"  readyFunction: null,\n"
		+"	serverURL: 'https://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php',\n"
		+"	j2sPath: 'swingjs/j2s',\n"
		+"	console:'sysoutdiv',\n"
		+"	allowjavascript: true\n"
		+"}\n"
		+"</script>\n</head>\n<body>\n<script>\n"
		+"SwingJS.getApplet('testApplet', Info)\n"
		+"getClassList = function(){J2S._saveFile('_j2sclasslist.txt', Clazz.ClassFilesLoaded.sort().join('\\n'))}\n"
		+"</script>\n"
		+"<div style=\"position:absolute;left:900px;top:30px;width:600px;height:300px;\">\n"
		+"<div id=\"sysoutdiv\" style=\"border:1px solid green;width:100%;height:95%;overflow:auto\"></div>\n"
		+"This is System.out. <a href=\"javascript:testApplet._clearConsole()\">clear it</a> <br>Add ?j2snocore to URL to see full class list; ?j2sdebug to use uncompressed j2s/core files <br><a href=\"javascript:getClassList()\">get _j2sClassList.txt</a>\n"
		+"</div>\n"
		+"</body>\n"
		+"</html>\n";
		return ret;
	}
	
	/**
	 * Create a test HTML file for the applet or application. It will go into <project>/site. 
	 * 
	 * @param appList
	 * @param siteFolder
	 * @param template
	 * @param isApplet
	 */
	private static void addHTML(ArrayList<String> appList, String siteFolder, String template, boolean isApplet) {
		if (appList == null || template == null)
			return;
		for (int i = appList.size(); --i >= 0;) {
			String cl = appList.get(i);
			String _NAME_ = cl.substring(cl.lastIndexOf(".") + 1);
			String fname = cl.replaceAll("\\.", "_") + (isApplet ? "_applet" : "") +".html";
			cl = "\"" + cl + "\"";
			String _MAIN_ = (isApplet ? "null" : cl);
			String _CODE_ = (isApplet ? cl : "null");
			template = template.replace("_NAME_", _NAME_).replace("_CODE_", _CODE_).replace("_MAIN_", _MAIN_);
			System.err.println("Java2Script creating " + siteFolder + "/" + fname);
			writeToFile(new File(siteFolder, fname), template);
		}
	}

	private static FileFilter filter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			//System.out.println("accept " + pathname + " " +pathname.isDirectory()); 
			return pathname.isDirectory() || !pathname.getName().endsWith(".java");
		}
		
	};
	
	private static void copySiteResources(File from, File dest) {
		copyNonclassFiles(from, dest);
	}

	private static void copyNonclassFiles(File dir, File target) {
		if (dir.equals(target))
			return;
		File[] files = dir.listFiles(filter);
		System.err.println("copy nonclassFiles " + dir + " to " + target + " [" + (files != null ? files.length : "") + "]");
		File f = null;
		if (files != null)
			try {
				if (!target.exists())
					Files.createDirectories(target.toPath());
				for (int i = 0; i < files.length; i++) {
					f = files[i];
					if (f == null) {
						//
					} else if (f.isDirectory()) {
						copyNonclassFiles(f, new File(target, f.getName()));
					} else {
							Files.copy(f.toPath(), new File(target, f.getName()).toPath(),
									StandardCopyOption.REPLACE_EXISTING);
							System.  err.println("copied " + f + " to " + target);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("error copying " + f + " to " + target);
				e1.printStackTrace();
			}
	}


}

