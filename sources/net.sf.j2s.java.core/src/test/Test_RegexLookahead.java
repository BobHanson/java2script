package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test_RegexLookahead extends Test_ {

	public static void main(String[] args) {
		assert Pattern.compile(".*='[^']*(?!')").matcher("a='hello").matches();

		check("a(?=b)", "xab", new int[][] { { 1, 2 } });
		check("(?=a(b))(ab)", "xab", new int[][] { { 1, 3 }, { 2, 3 }, { 1, 3 } });
		check("(a)?b", "b", new int[][] { { 0, 1 }, { -1, -1 } });
		check("x(a)\\1", "xaa", new int[][] { { 0, 3 }, { 1, 2 } });
		Matcher named = check("(?=(?<ahead>ab))(?<after>a)", "xab",
				new int[][] { { 1, 2 }, { 1, 3 }, { 1, 2 } });
		assert named.group("ahead").equals("ab");
		assert named.end("after") == 2;
		named = check("(?<=x)(?<after>a)", "xa", new int[][] { { 1, 2 }, { 1, 2 } });
		assert named.start("after") == 1;

		Matcher advancing = check("(a)", "aa", new int[][] { { 0, 1 }, { 0, 1 } });
		java.util.regex.MatchResult snapshot = advancing.toMatchResult();
		assert advancing.find();
		assert snapshot.start(1) == 0;
		System.out.println("Test_RegexLookahead OK");
	}

	private static Matcher check(String regex, String input, int[][] bounds) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		assert matcher.find() : regex;
		assert matcher.groupCount() == bounds.length - 1 : regex;
		for (int i = 0; i < bounds.length; i++) {
			assert matcher.start(i) == bounds[i][0] : regex + " start " + i;
			assert matcher.end(i) == bounds[i][1] : regex + " end " + i;
			assert bounds[i][0] < 0 ? matcher.group(i) == null
					: matcher.group(i).equals(input.substring(bounds[i][0], bounds[i][1])) : regex + " group " + i;
		}
		return matcher;
	}
}
