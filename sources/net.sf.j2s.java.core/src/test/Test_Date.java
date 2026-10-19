package test;

import java.util.Date;
import java.util.Locale;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

strictfp class Test_Date extends Test_{
	
	public static void main(String[] args) {

//		1741542116297
//		03/9/25
//		03-09-2025 17:41:56
//		0309251741
//		
//		1741542116297
//		03/9/25
//		03-09-2025 12:41:56
//		0309251241
		long t = 1741542116297L;
		Date d = new Date("07/10/96 12:00 AM GMT");
		System.out.println(d);
		String s;

		s = new SimpleDateFormat("M/d/yy h:mm").format(d.getTime()).toString();
		System.out.println(s);

		System.out.println(d.getTime());

		s = new SimpleDateFormat("MM-dd-yy-hh-mm").format(d).toString();
		System.out.println(s);

		s = new SimpleDateFormat("MM-dd-yy-HH-mm").format(d).toString();
		System.out.println(s);

		s = new SimpleDateFormat("MM-dd-yy-kk-mm").format(d).toString();
		System.out.println(s);

		d = new Date("07/10/96 7:00 AM");
		System.out.println(d);

		System.out.println(d.getTime());

		s = new SimpleDateFormat("M-d-yyyy-hh-mm").format(d).toString();
		System.out.println(s);

		s = new SimpleDateFormat("M-d-yy-HH-mm").format(d).toString();
		System.out.println(s);

		s = new SimpleDateFormat("MM-dd-yy-kk-mm").format(d).toString();
		System.out.println(s);

		d = new Date("07/10/96 7:18 AM");
		System.out.println(d);

		s = new SimpleDateFormat("M/d/yy h:mm").format(d.getTime()).toString();
		System.out.println(s);
		s = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(d).toString();
		System.out.println(s);
		s = new SimpleDateFormat("MMddyyhhmm").format(d).toString();
		System.out.println(s);

		try {
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date timestamp = parser.parse("1970-01-01T01:00:00", new ParsePosition(0));
			System.out.println("Success." + timestamp);
			timestamp = parser.parse("TESTING 1970-01-01T01:00:00", new ParsePosition(8));
			System.out.println("Success." + timestamp);
			parser = new SimpleDateFormat("M/d/yyyy");
			timestamp = parser.parse("10/3/2026");
			System.out.println("Success." + timestamp);
			parser = new SimpleDateFormat("d/M/yyyy");
			timestamp = parser.parse("3/10/2026");
			System.out.println("Success." + timestamp);
			parser = new SimpleDateFormat("yyyy.MM.dd");
			timestamp = parser.parse("2026.10.3");
			System.out.println("Success." + timestamp);
			Date date = new Date(timestamp.toString());
			Locale.setDefault(Locale.CHINA);
			s = new SimpleDateFormat().format(date).toString();
			System.out.println(s);
			System.out.println(new SimpleDateFormat().parse(s));

		} catch (Throwable et) {
			System.err.print("Failed! ");
			et.printStackTrace();
		}

	}

	
}