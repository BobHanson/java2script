/*
 * Some portions of this file have been modified by Robert Hanson hansonr.at.stolaf.edu 2012-2017
 * for use in SwingJS via transpilation into JavaScript using Java2Script.
 *
 * Copyright (c) 1996, 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

import java.io.Serializable;

import java.text.ParseException;


/**
 * <code>Format</code> is an abstract base class for formatting locale-sensitive
 * information such as dates, messages, and numbers.
 *
 * <p>
 * <code>Format</code> defines the programming interface for formatting
 * locale-sensitive objects into <code>String</code>s (the
 * <code>format</code> method) and for parsing <code>String</code>s back
 * into objects (the <code>parseObject</code> method).
 *
 * <p>
 * Generally, a format's <code>parseObject</code> method must be able to parse
 * any string formatted by its <code>format</code> method. However, there may
 * be exceptional cases where this is not possible. For example, a
 * <code>format</code> method might create two adjacent integer numbers with
 * no separator in between, and in this case the <code>parseObject</code> could
 * not tell which digits belong to which number.
 *
 * <h4>Subclassing</h4>
 *
 * <p>
 * The Java Platform provides three specialized subclasses of <code>Format</code>--
 * <code>DateFormat</code>, <code>MessageFormat</code>, and
 * <code>NumberFormat</code>--for formatting dates, messages, and numbers,
 * respectively.
 * <p>
 * Concrete subclasses must implement three methods:
 * <ol>
 * <li> <code>format(Object obj, StringBuffer toAppendTo, FieldPosition pos)</code>
 * <li> <code>formatToCharacterIterator(Object obj)</code>
 * <li> <code>parseObject(String source, ParsePosition pos)</code>
 * </ol>
 * These general methods allow polymorphic parsing and formatting of objects
 * and are used, for example, by <code>MessageFormat</code>.
 * Subclasses often also provide additional <code>format</code> methods for
 * specific input types as well as <code>parse</code> methods for specific
 * result types. Any <code>parse</code> method that does not take a
 * <code>ParsePosition</code> argument should throw <code>ParseException</code>
 * when no text in the required format is at the beginning of the input text.
 *
 * <p>
 * Most subclasses will also implement the following factory methods:
 * <ol>
 * <li>
 * <code>getInstance</code> for getting a useful format object appropriate
 * for the current locale
 * <li>
 * <code>getInstance(Locale)</code> for getting a useful format
 * object appropriate for the specified locale
 * </ol>
 * In addition, some subclasses may also implement other
 * <code>getXxxxInstance</code> methods for more specialized control. For
 * example, the <code>NumberFormat</code> class provides
 * <code>getPercentInstance</code> and <code>getCurrencyInstance</code>
 * methods for getting specialized number formatters.
 *
 * <p>
 * Subclasses of <code>Format</code> that allow programmers to create objects
 * for locales (with <code>getInstance(Locale)</code> for example)
 * must also implement the following class method:
 * <blockquote>
 * <pre>
 * public static Locale[] getAvailableLocales()
 * </pre>
 * </blockquote>
 *
 * <p>
 * And finally subclasses may define a set of constants to identify the various
 * fields in the formatted output. These constants are used to create a FieldPosition
 * object which identifies what information is contained in the field and its
 * position in the formatted result. These constants should be named
 * <code><em>item</em>_FIELD</code> where <code><em>item</em></code> identifies
 * the field. For examples of these constants, see <code>ERA_FIELD</code> and its
 * friends in {@link DateFormat}.
 *
 * <h4><a name="synchronization">Synchronization</a></h4>
 *
 * <p>
 * Formats are generally not synchronized.
 * It is recommended to create separate format instances for each thread.
 * If multiple threads access a format concurrently, it must be synchronized
 * externally.
 *
 * @see          java.text.ParsePosition
 * @see          java.text.FieldPosition
 * @see          java.text.NumberFormat
 * @see          java.text.DateFormat
 * @see          java.text.MessageFormat
 * @author       Mark Davis
 */
public abstract class Format implements Serializable, Cloneable {

    private static final long serialVersionUID = -299282585814624189L;
	protected boolean isSimpleMMDDYY;


    

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected Format() {
    }

    /**
     * Formats an object to produce a string. This is equivalent to
     * <blockquote>
     * {@link #format(Object, StringBuffer, FieldPosition) format}<code>(obj,
     *         new StringBuffer(), new FieldPosition(0)).toString();</code>
     * </blockquote>
     *
     * @param obj    The object to format
     * @return       Formatted string.
     * @exception IllegalArgumentException if the Format cannot format the given
     *            object
     */
    public /* swingjs final*/ String format (Object obj) {
        return format(obj, new StringBuffer(), (isSimpleMMDDYY ? null : new FieldPosition(0))).toString();
    }

    /**
     * Formats an object and appends the resulting text to a given string
     * buffer.
     * If the <code>pos</code> argument identifies a field used by the format,
     * then its indices are set to the beginning and end of the first such
     * field encountered.
     *
     * @param obj    The object to format
     * @param toAppendTo    where the text is to be appended
     * @param pos    A <code>FieldPosition</code> identifying a field
     *               in the formatted text
     * @return       the string buffer passed in as <code>toAppendTo</code>,
     *               with formatted text appended
     * @exception NullPointerException if <code>toAppendTo</code> or
     *            <code>pos</code> is null
     * @exception IllegalArgumentException if the Format cannot format the given
     *            object
     */
    public abstract StringBuffer format(Object obj,
                    StringBuffer toAppendTo,
                    FieldPosition pos);

    /**
     * Formats an Object producing an <code>AttributedCharacterIterator</code>.
     * You can use the returned <code>AttributedCharacterIterator</code>
     * to build the resulting String, as well as to determine information
     * about the resulting String.
     * <p>
     * Each attribute key of the AttributedCharacterIterator will be of type
     * <code>Field</code>. It is up to each <code>Format</code> implementation
     * to define what the legal values are for each attribute in the
     * <code>AttributedCharacterIterator</code>, but typically the attribute
     * key is also used as the attribute value.
     * <p>The default implementation creates an
     * <code>AttributedCharacterIterator</code> with no attributes. Subclasses
     * that support fields should override this and create an
     * <code>AttributedCharacterIterator</code> with meaningful attributes.
     *
     * @exception NullPointerException if obj is null.
     * @exception IllegalArgumentException when the Format cannot format the
     *            given object.
     * @param obj The object to format
     * @return AttributedCharacterIterator describing the formatted value.
     * @since 1.4
     */
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return createAttributedCharacterIterator(format(obj));
    }

    /**
     * Parses text from a string to produce an object.
     * <p>
     * The method attempts to parse text starting at the index given by
     * <code>pos</code>.
     * If parsing succeeds, then the index of <code>pos</code> is updated
     * to the index after the last character used (parsing does not necessarily
     * use all characters up to the end of the string), and the parsed
     * object is returned. The updated <code>pos</code> can be used to
     * indicate the starting point for the next call to this method.
     * If an error occurs, then the index of <code>pos</code> is not
     * changed, the error index of <code>pos</code> is set to the index of
     * the character where the error occurred, and null is returned.
     *
     * @param source A <code>String</code>, part of which should be parsed.
     * @param pos A <code>ParsePosition</code> object with index and error
     *            index information as described above.
     * @return An <code>Object</code> parsed from the string. In case of
     *         error, returns null.
     * @exception NullPointerException if <code>pos</code> is null.
     */
    public abstract Object parseObject (String source, ParsePosition pos);

    /**
     * Parses text from the beginning of the given string to produce an object.
     * The method may not use the entire text of the given string.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return An <code>Object</code> parsed from the string.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Object parseObject(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Object result = parseObject(source, pos);
        if (pos.index == 0) {
            throw new ParseException("Format.parseObject(String) failed",
                pos.errorIndex);
        }
        return result;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     */
    @Override
		public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // will never happen
            return null;
        }
    }

    //
    // Convenience methods for creating AttributedCharacterIterators from
    // different parameters.
    //

    /**
     * Creates an <code>AttributedCharacterIterator</code> for the String
     * <code>s</code>.
     *
     * @param s String to create AttributedCharacterIterator from
     * @return AttributedCharacterIterator wrapping s
     */
    AttributedCharacterIterator createAttributedCharacterIterator(String s) {
        AttributedString as = new AttributedString(s);

        return as.getIterator();
    }

    /**
     * Creates an <code>AttributedCharacterIterator</code> containg the
     * concatenated contents of the passed in
     * <code>AttributedCharacterIterator</code>s.
     *
     * @param iterators AttributedCharacterIterators used to create resulting
     *                  AttributedCharacterIterators
     * @return AttributedCharacterIterator wrapping passed in
     *         AttributedCharacterIterators
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
                       AttributedCharacterIterator[] iterators) {
        AttributedString as = new AttributedString(iterators);

        return as.getIterator();
    }

    /**
     * Returns an AttributedCharacterIterator with the String
     * <code>string</code> and additional key/value pair <code>key</code>,
     * <code>value</code>.
     *
     * @param string String to create AttributedCharacterIterator from
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
                      String string, AttributedCharacterIterator.Attribute key,
                      Object value) {
        AttributedString as = new AttributedString(string);

        as.addAttribute(key, value);
        return as.getIterator();
    }

    /**
     * Creates an AttributedCharacterIterator with the contents of
     * <code>iterator</code> and the additional attribute <code>key</code>
     * <code>value</code>.
     *
     * @param iterator Initial AttributedCharacterIterator to add arg to
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
              AttributedCharacterIterator iterator,
              AttributedCharacterIterator.Attribute key, Object value) {
        AttributedString as = new AttributedString(iterator);

        as.addAttribute(key, value);
        return as.getIterator();
    }


    /**
     * Defines constants that are used as attribute keys in the
     * <code>AttributedCharacterIterator</code> returned
     * from <code>Format.formatToCharacterIterator</code> and as
     * field identifiers in <code>FieldPosition</code>.
     *
     * @since 1.4
     */
    public static class Field extends AttributedCharacterIterator.Attribute {

        // Proclaim serial compatibility with 1.4 FCS
        private static final long serialVersionUID = 276966692217360283L;

        /**
         * Creates a Field with the specified name.
         *
         * @param name Name of the attribute
         */
        protected Field(String name) {
            super(name);
        }
    }


    /**
     * FieldDelegate is notified by the various <code>Format</code>
     * implementations as they are formatting the Objects. This allows for
     * storage of the individual sections of the formatted String for
     * later use, such as in a <code>FieldPosition</code> or for an
     * <code>AttributedCharacterIterator</code>.
     * <p>
     * Delegates should NOT assume that the <code>Format</code> will notify
     * the delegate of fields in any particular order.
     *
     * @see FieldPosition.Delegate
     * @see CharacterIteratorFieldDelegate
     */
    interface FieldDelegate {
        /**
         * Notified when a particular region of the String is formatted. This
         * method will be invoked if there is no corresponding integer field id
         * matching <code>attr</code>.
         *
         * @param attr Identifies the field matched
         * @param value Value associated with the field
         * @param start Beginning location of the field, will be >= 0
         * @param end End of the field, will be >= start and <= buffer.length()
         * @param buffer Contains current formatted value, receiver should
         *        NOT modify it.
         */
        public void formatted(Format.Field attr, Object value, int start,
                              int end, StringBuffer buffer);

        public void formatted(Format.Field attr, Object value, int start,
                int end, String[] buffer);

        /**
         * Notified when a particular region of the String is formatted.
         *
         * @param fieldID Identifies the field by integer
         * @param attr Identifies the field matched
         * @param value Value associated with the field
         * @param start Beginning location of the field, will be >= 0
         * @param end End of the field, will be >= start and <= buffer.length()
         * @param buffer Contains current formatted value, receiver should
         *        NOT modify it.
         */
        public void formatted(int fieldID, Format.Field attr, Object value,
                              int start, int end, StringBuffer buffer);


        public void formatted(int fieldID, Format.Field attr, Object value,
                int start, int end, String[] buffer);


    }
}
