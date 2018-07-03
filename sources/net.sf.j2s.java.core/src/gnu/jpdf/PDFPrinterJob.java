package gnu.jpdf;

import java.awt.HeadlessException;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.RequestingUserName;
import javax.swing.JFileChooser;

/**
 * <p>
 * This class extends awt's PrinterJob, to provide a simple method of writing
 * PDF documents.
 * </p>
 * 
 * <p>
 * You can use this with any code that uses Java's printing mechanism. It does
 * include a few extra methods to provide access to some of PDF's features like
 * annotations, or outlines.
 * </p>
 * 
 * @author Gilbert DeLeeuw, gil1@users.sourceforge.net
 */
public class PDFPrinterJob extends PrinterJob {

	/**
	 * The file chooser
	 */
	private static JFileChooser fileChooser;

	/**
	 * Printing options;
	 */
	private PrintRequestAttributeSet attributes;

	/**
	 * PDF document properties
	 */
	private PDFInfo info;

	/**
	 * A pageable, or null
	 */
	private Pageable pageable = null;

	/**
	 * Page format.
	 */
	private PageFormat pageFormat;

	/**
	 * The Printable object to print.
	 */
	private Printable printable;

	/**
	 * The actual print job.
	 */
	private PDFJob printJob;

	/**
	 * Initializes a new instance of <code>PDFPrinterJob</code>.
	 */
	public PDFPrinterJob() {
		attributes = new HashPrintRequestAttributeSet();
		info = new PDFInfo();
		//pageFormat = new PageFormat(); // default page format.
		setJobName("Java Printing");
	}

	@Override
	public void cancel() {
		// Cancel is not an option
	}

	@Override
	public PageFormat defaultPage(PageFormat page) {
		return validatePage(page);
	}

	@Override
	public int getCopies() {
		return ((IntegerSyntax) attributes.get(Copies.class)).getValue();
	}

	@Override
	public String getJobName() {
		return ((TextSyntax) attributes.get(JobName.class)).getValue();
	}

	public static PrinterJob getPrinterJob() {
		return new PDFPrinterJob();
	}

	@Override
	public String getUserName() {
		return ((TextSyntax) attributes.get(RequestingUserName.class)).getValue();
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public PageFormat pageDialog(PageFormat page) throws HeadlessException {
		// No page dialog is supported.
		return (PageFormat) page.clone();
	}

	/**
	 * Prints a set of pages.
	 * 
	 * @param pathname
	 *            the full path for the output PDF file.
	 * @exception PrinterException
	 *                an error in the print system caused the job to be aborted.
	 * @see Book
	 * @see Pageable
	 * @see Printable
	 */
	public void print(String pathname) throws PrinterException {
		int pageCount;
		File file = null;
		FileOutputStream fileOutputStream = null;

		try {
			file = new File(pathname);
			fileOutputStream = new FileOutputStream(file);
		} catch (Exception e) {
			System.err.println("Error!! - Invalid output file path: " + pathname);
		}

		System.out.println("GNU JPDF creating " + file);

		PDFGraphics pdfGraphics = null;
		printJob = new PDFJob(fileOutputStream);

		if (info != null) {
			printJob.getPDFDocument().setPDFInfo(info);
		}

		// BH 2018 allows for unknown number of pages and a return of NO_SUCH_PAGE from the 
		// project to stop sending pages. 
		pageCount = (pageable == null ? Pageable.UNKNOWN_NUMBER_OF_PAGES : pageable.getNumberOfPages());

		for (int pageIndex = 0; pageCount < 0 || pageIndex < pageCount; pageIndex++) {
			if (pageable != null)
				pageFormat = pageable.getPageFormat(pageIndex);
			if (pageFormat == null)
				pageFormat = defaultPage();
			pdfGraphics = (PDFGraphics) printJob.getGraphics(pageFormat);
			if (pageable != null)
				printable = pageable.getPrintable(pageIndex);
			if (printable.print(pdfGraphics, pageFormat, pageIndex) == Printable.NO_SUCH_PAGE)
			   pageCount = 0;
			pdfGraphics.dispose();
		}
		printJob.end();

		System.out.println("GNU JPDF created: " + file);

	}

	@Override
	public void print() throws PrinterException {
		File file;
		File path;
		String jobName = getJobName();

		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}

		// Make sure job name is not blank
		if (jobName.equals("")) {
			jobName = "Java Printing";
		}
		// Eliminate invalid characters from job name.
		jobName = jobName.replaceAll("\\\\", "-");
		jobName = jobName.replaceAll("/", "-");

		path = fileChooser.getCurrentDirectory();
		file = new File(path, jobName + ".pdf");

		// Dialog to get file name...
		fileChooser.setSelectedFile(file);

		// Print
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			String s = file.getAbsolutePath();
			if (!s.endsWith(".pdf")) // needed for JavaScript
				s += ".pdf";
			print(s);
		}
	}	

	@Override
	public boolean printDialog() throws HeadlessException {
		return true;
	}

	/**
	 * Sets the author for this document.
	 * 
	 * @param author
	 *          the author's name.
	 */
	public void setAuthor(String author) {
		info.setAuthor(author);
	}

	@Override
	public void setCopies(int copies) {
		// Will be ignored, but add attribute anyway
		attributes.add(new Copies(copies));
	}

	/**
	 * Sets the creator for this document.
	 * 
	 * @param creator
	 *          the application name.
	 */
	public void setCreator(String creator) {
		info.setCreator(creator);
	}

	@Override
	public void setJobName(String jobName) {
		attributes.add(new JobName(jobName, Locale.getDefault()));

		if (info.getTitle() == null) {
			info.setTitle(jobName);
		}
	}

	@Override
	public void setPageable(Pageable document) throws NullPointerException {
		if (document == null) {
			throw new NullPointerException("Pageable cannot be null.");
		}
		this.pageable = document;
	}

	@Override
	public void setPrintable(Printable painter) {
		this.printable = painter;
	}

	@Override
	public void setPrintable(Printable painter, PageFormat format) {
		this.printable = painter;
		this.pageFormat = format;
	}

	/**
	 * Sets the title for this document.
	 * 
	 * @param title
	 *          the document title.
	 */
	public void setTitle(String title) {
		info.setTitle(title);
	}

	@Override
	public PageFormat validatePage(PageFormat page) {
		return (PageFormat) page.clone();
	}

}
