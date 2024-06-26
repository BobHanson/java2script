/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.util.regex;

public final class Matcher /*implements MatchResult*/ {


    @SuppressWarnings("unused")
	private Pattern pat;

    CharSequence string;
    
    String strString = null;

  // bounds
  private int leftBound = -1;

  private int rightBound = -1;
	
	private String[] results;
	
    Matcher(Pattern pat, CharSequence cs) {
        this.pat = pat;
        this.string = cs;
        this.strString = cs.toString();
        this.leftBound = 0;
        this.rightBound = strString.length();
    }
    


	public boolean find() { 
		@SuppressWarnings("unused")
		String s = (this.rightBound == this.strString.length() ? this.strString
				: this.strString.substring(0, this.rightBound));
		@SuppressWarnings("unused")
		int lb = this.leftBound;

		/**
		 * @j2sNative
		 * 
		 * 			  this.pat.regexp.lastIndex = this.leftBound; 
		 * 			  this.results = this.pat.regexp.exec(s); 
		 * 			  this.leftBound = this.pat.regexp.lastIndex;
		 */
		{
		}
		return (this.results != null);
	}

    //@Override
	public int start() {
    	/** 
    	 * @js2Native
    	 * 
    	 * this.pat.regexp.lastIndex - this.results[0].length;
    	 * 
    	 */
    	{
    		 return 0;
    	}
    }

    //@Override
	public int start(int groupIndex) {
    	System.err.println("Matcher.start(groupIndex) not implemented");
    	return start();    
    }

    /**
     * @com.intel.drl.spec_ref
     */
    //@Override
	public int end(int groupIndex) {
    	System.err.println("Matcher.end(groupIndex) not implemented");
    	return end();
    }

    /**
     * @com.intel.drl.spec_ref
     */
    //@Override
	public int end() {
		/**
		 * @j2sNative
		 * return this.pat.regexp.lastIndex;
		 */ 
    	{
		    	return -1;
		}
    }

    //@Override
	public String group(int groupIndex) {
    	if (results == null || groupIndex < 0 || groupIndex > results.length) {
    		return null;
    	}
		return this.results[groupIndex];
    }

    //@Override
	public String group() {
        return group(0);
    }

    /// END OF STORY FOR Jmol ///

//  static int MODE_FIND = 1 << 0;
//
//  static int MODE_MATCH = 1 << 1;
//	/**
//	 * Return a reference to the pattern used by this Matcher.
//	 * 
//	 * @return A reference to the pattern used by this Matcher.
//	 */
//    public Pattern pattern() {
//        return pat;
//    }
//
//
//  // replacements
//  private int appendPos = 0;
//
//  private String replacement = null;
//
//  private String processedRepl = null;
//
////  private ArrayList replacementParts = null;
//  private Object[] replacementParts = null;
//
//	private int lastAppendIndex;
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher appendReplacement(StringBuffer sb, String replacement) {
//        processedRepl = processReplacement(replacement);
//        sb.append(string.subSequence(appendPos, start()));
//        sb.append(processedRepl);
//        appendPos = end();
////		int beginningIndex = 0;
////		/**
////		 * @j2sNative
////		beginningIndex = this.pat.regexp.lastIndex;
////		 */ {}
////		 beginningIndex -= this.results[0].length();
////		sb.append(this.string.subSequence(lastAppendIndex, beginningIndex));
////		/**
////		 * @j2sNative
////		this.lastAppendIndex = this.pat.regexp.lastIndex;
////		 */ {}
////		int idx = replacement.indexOf('$');
////		if (idx == -1) {
////			sb.append(replacement);
////		} else /** @j2sNative
////		var re = new RegExp (this.pat.regexp.source, "");
////		sb.append (this.results[0].replace (re, s));  
////		*/ {
////		}
//        return this;
//    }
//
//    /**
//     * Parses replacement string and creates pattern
//     */
//    private String processReplacement(String replacement) {
//        if (this.replacement != null 
//                && this.replacement.equals(replacement)) {
//            if (replacementParts == null) {
//                return processedRepl;
//            } else {
//                StringBuffer sb = new StringBuffer();
////                for (int i = 0; i < replacementParts.size(); i++) {
////                    sb.append(replacementParts.get(i));
////                }
//                for (int i = 0; i < replacementParts.length; i++) {
//                	sb.append(replacementParts[i]);
//                }
//
//                return sb.toString();
//            }
//        } else {
//            this.replacement = replacement;
//            char[] repl = replacement.toCharArray();
//            StringBuffer res = new StringBuffer();
//            replacementParts = null;
//
//            int index = 0;
//            int replacementPos = 0;
//            boolean nextBackSlashed = false;
//
//            while (index < repl.length) {
//                
//                if (repl[index] == '\\' && !nextBackSlashed) {
//                    nextBackSlashed = true;
//                    index++;
//                } 
//               
//                if (nextBackSlashed) {
//                    res.append(repl[index]);
//                    nextBackSlashed = false;
//                } else {
//                    if (repl[index] == '$') {
//                        if (replacementParts == null) {
////                            replacementParts = new ArrayList();
//                            replacementParts = new Object[0];
//                        }
//                        try {
//                            final int gr = Integer.parseInt(new String(
//                                    repl, ++index, 1));
//
//                            if (replacementPos != res.length()) {
////                                replacementParts.add(res.subSequence(
////                                        replacementPos, res.length()));
//                            	replacementParts[replacementParts.length] = res.subSequence(
//                                		replacementPos, res.length());
//                                replacementPos = res.length();
//                            }
//
//                            /*
//                            replacementParts.add(new Object() {  //$NON-LOCK-1$
//                                private final int grN = gr;
//
//                                public String toString() {
//                                    return group(grN);
//                                }
//                            });
//                            */
//                            replacementParts[replacementParts.length] = new Object() {  //$NON-LOCK-1$
//                                private final int grN = gr;
//
//                                //@Override
//								public String toString() {
//                                    return group(grN);
//                                }
//                            };
//                            String group = group(gr);
//                            replacementPos += group.length();
//                            res.append(group);
//
//                        } catch (IndexOutOfBoundsException iob) {
//                            throw iob;
//                        } catch (Exception e) {
//                            throw new IllegalArgumentException(
////                                    Messages.getString("regex.00")); //$NON-NLS-1$
//                            		"Illegal regular expression format");
//                        }
//                    } else {
//                        res.append(repl[index]);
//                    }
//                }
//
//                index++;
//            }
//
//            if (replacementParts != null && replacementPos != res.length()) {
////                replacementParts.add(res.subSequence(replacementPos, res
////                        .length()));
//            	replacementParts[replacementParts.length] = res.subSequence(replacementPos, res
//                		.length());
//            }
//            return res.toString();
//        }
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher reset(CharSequence newSequence) {
//        if (newSequence == null) {
////            throw new NullPointerException(Messages.getString("regex.01")); //$NON-NLS-1$
//            throw new NullPointerException("Empty new sequence!");
//        }
//        this.string = newSequence;
//        return reset();
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher reset() {
//        this.leftBound = 0;
//        this.rightBound = string.length();
////        matchResult.reset(string, leftBound, rightBound);
//        appendPos = 0;
//        replacement = null;
////        matchResult.previousMatch = -1;
//		/**
//		 * @j2sNative
//		var flags = "" + (this.pat.regexp.ignoreCase ? "i" : "")
//				+ (this.pat.regexp.global ? "g" : "")
//				+ (this.pat.regexp.multiline ? "m" : "");
//		this.pat.regexp = new RegExp (this.pat.regexp.source, flags);
//		 */ {}
//        return this;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher region(int leftBound, int rightBound) {
//
//        if (leftBound > rightBound || leftBound < 0 || rightBound < 0
//                || leftBound > string.length() || rightBound > string.length()) {
////            throw new IndexOutOfBoundsException( Messages.getString("regex.02", //$NON-NLS-1$
////                            Integer.toString(leftBound), Integer.toString(rightBound)));
//            throw new IndexOutOfBoundsException( leftBound + " is out of bound of " + rightBound );
//        }
//
//        this.leftBound = leftBound;
//        this.rightBound = rightBound;
//        
//        this.results = null;
//        
////        matchResult.reset(null, leftBound, rightBound);
//        appendPos = 0;
//        replacement = null;
//
//        return this;
//    }
//
//	/**
//	 * TODO: appendTail(StringBuffer) javadoc
//	 * 
//	 * @param sb
//	 * @return
//	 */
//    public StringBuffer appendTail(StringBuffer sb) {
//        return sb.append(string.subSequence(appendPos, string.length()));
//    }
//
//	/**
//	 * This is very similar to replaceAll except only the first occurrence of a
//	 * sequence matching the pattern is replaced.
//	 * 
//	 * @param replacement
//	 *            A string to replace occurrences of character sequences
//	 *            matching the pattern.
//	 * @return A new string with replacements inserted
//	 */
//    public String replaceFirst(String replacement) {
//        reset();
//        if (find()) {
//            StringBuffer sb = new StringBuffer();
//            appendReplacement(sb, replacement);
//            return appendTail(sb).toString();
//        }
//
//        return string.toString();
//
//    }
//
//	/**
//	 * Replace all occurrences of character sequences which match the pattern
//	 * with the given replacement string. The replacement string may refer to
//	 * capturing groups using the syntax "$<group number>".
//	 * 
//	 * @param replacement
//	 *            A string to replace occurrences of character sequences
//	 *            matching the pattern.
//	 * @return A new string with replacements inserted
//	 */
//    public String replaceAll(String replacement) {
//        StringBuffer sb = new StringBuffer();
//        reset();
//        while (find()) {
//            appendReplacement(sb, replacement);
//        }
//
//        return appendTail(sb).toString();
//    }
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public boolean find(int startIndex) {
//        int stringLength = string.length();
//        if (startIndex < 0 || startIndex > stringLength)
////            throw new IndexOutOfBoundsException(Messages.getString("regex.03", //$NON-NLS-1$ 
////                    new Integer(startIndex)));
//        	throw new IndexOutOfBoundsException("Out of bound " + startIndex); 
//
//        startIndex = findAt(startIndex);
////        if (startIndex >= 0 && matchResult.isValid()) {
////            matchResult.finalizeMatch();
////            return true;
////        }
////        matchResult.startIndex = -1;
//        return false;
//    }
//
//    private int findAt(int startIndex) {
////        matchResult.reset();
////        matchResult.setMode(Matcher.MODE_FIND);
////        matchResult.setStartIndex(startIndex);
////        int foundIndex = start.find(startIndex, string, matchResult);
////        if (foundIndex == -1) {
////            matchResult.hitEnd = true;
////        }
////        return foundIndex;
//    	return -1;
//    }
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public static String quoteReplacement(String string) {
//        // first check whether we have smth to quote
//        if (string.indexOf('\\') < 0 && string.indexOf('$') < 0)
//            return string;
//        StringBuffer res = new StringBuffer(string.length() * 2);
//        char ch;
//        int len = string.length();
//
//        for (int i = 0; i < len; i++) {
//
//            switch (ch = string.charAt(i)) {
//            case '$':
//                res.append('\\');
//                res.append('$');
//                break;
//            case '\\':
//                res.append('\\');
//                res.append('\\');
//                break;
//            default:
//                res.append(ch);
//            }
//        }
//
//        return res.toString();
//    }
//
//    /**
//     * Runs match starting from <code>set</code> specified against input
//     * sequence starting at <code>index</code> specified; Result of the match
//     * will be stored into matchResult instance;
//     */
//    private boolean runMatch(AbstractSet set, int index,
//            MatchResultImpl matchResult) {
//
//        if (set.matches(index, string, matchResult) >= 0) {
//            matchResult.finalizeMatch();
//            return true;
//        }
//
//        return false;
//    }
//
//	/**
//	 * This method attempts to match the pattern against the character sequence
//	 * starting at the beginning. If the pattern matches even a prefix of the
//	 * input character sequence, lookingAt() will return true. Otherwise it will
//	 * return false.
//	 * 
//	 * @return A boolean indicating if the pattern matches a prefix of the input
//	 *         character sequence.
//	 */
//    public boolean lookingAt() {
////        return lookingAt(leftBound, Matcher.MODE_FIND);
//    	return false;
//    }
//
//    private boolean lookingAt(int startIndex, int mode) {
//        matchResult.reset();
//        matchResult.setMode(mode);
//        matchResult.setStartIndex(startIndex);
//        return runMatch(start, startIndex, matchResult);
//    }
//
//	/**
//	 * Return the number of capturing groups in the pattern.
//	 * 
//	 * @return The number of capturing groups in the pattern.
//	 */
//    //@Override
//	public int groupCount() {
//        return matchResult.groupCount();
//    	return this.results == null ? 0 : this.results.length;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public MatchResult toMatchResult() {
////        return this.matchResult.cloneImpl();
//    	return this;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher useAnchoringBounds(boolean value) {
////        matchResult.useAnchoringBounds(value);
//        return this;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public boolean hasAnchoringBounds() {
////        return matchResult.hasAnchoringBounds();
//    	return false;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher useTransparentBounds(boolean value) {
////        matchResult.useTransparentBounds(value);
//        return this;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public boolean hasTransparentBounds() {
////        return matchResult.hasTransparentBounds();
//    	return false;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public int regionStart() {
////        return matchResult.getLeftBound();
//    	return this.leftBound;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public int regionEnd() {
////        return matchResult.getRightBound();
//    	return this.rightBound;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public boolean requireEnd() {
////        return matchResult.requireEnd;
//    	return false;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public boolean hitEnd() {
////        return matchResult.hitEnd;
//    	return false;
//    }
//
//    /**
//     * @com.intel.drl.spec_ref
//     */
//    public Matcher usePattern(Pattern pat) {
//    	if (pat == null) {
//    		throw new IllegalArgumentException("Empty pattern!");
//    	}
//        this.pat = pat;
//        this.results = null;
//        return this;
//    }

}
