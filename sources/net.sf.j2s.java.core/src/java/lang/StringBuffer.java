/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

/**
 * A thread-safe, mutable sequence of characters.
 * A string buffer is like a {@link String}, but can be modified. At any
 * point in time it contains some particular sequence of characters, but
 * the length and content of the sequence can be changed through certain
 * method calls.
 * <p>
 * String buffers are safe for use by multiple threads. The methods
 * are synchronized where necessary so that all the operations on any
 * particular instance behave as if they occur in some serial order
 * that is consistent with the order of the method calls made by each of
 * the individual threads involved.
 * <p>
 * The principal operations on a {@code StringBuffer} are the
 * {@code append} and {@code insert} methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string buffer. The
 * {@code append} method always adds these characters at the end
 * of the buffer; the {@code insert} method adds the characters at
 * a specified point.
 * <p>
 * For example, if {@code z} refers to a string buffer object
 * whose current contents are {@code "start"}, then
 * the method call {@code z.append("le")} would cause the string
 * buffer to contain {@code "startle"}, whereas
 * {@code z.insert(4, "le")} would alter the string buffer to
 * contain {@code "starlet"}.
 * <p>
 * In general, if sb refers to an instance of a {@code StringBuffer},
 * then {@code sb.append(x)} has the same effect as
 * {@code sb.insert(sb.length(), x)}.
 * <p>
 * Whenever an operation occurs involving a source sequence (such as
 * appending or inserting from a source sequence), this class synchronizes
 * only on the string buffer performing the operation, not on the source.
 * Note that while {@code StringBuffer} is designed to be safe to use
 * concurrently from multiple threads, if the constructor or the
 * {@code append} or {@code insert} operation is passed a source sequence
 * that is shared across threads, the calling code must ensure
 * that the operation has a consistent and unchanging view of the source
 * sequence for the duration of the operation.
 * This could be satisfied by the caller holding a lock during the
 * operation's call, by using an immutable source sequence, or by not
 * sharing the source sequence across threads.
 * <p>
 * Every string buffer has a capacity. As long as the length of the
 * character sequence contained in the string buffer does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer array. If the internal buffer overflows, it is
 * automatically made larger.
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 * <p>
 * As of  release JDK 5, this class has been supplemented with an equivalent
 * class designed for use by a single thread, {@link StringBuilder}.  The
 * {@code StringBuilder} class should generally be used in preference to
 * this one, as it supports all of the same operations but it is faster, as
 * it performs no synchronization.
 *
 * @author      Arthur van Hoff
 * @see     java.lang.StringBuilder
 * @see     java.lang.String
 * @since   JDK1.0
 */
 public final class StringBuffer
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence
{

//    /**
//     * A cache of the last value returned by toString. Cleared
//     * whenever the StringBuffer is modified.
//     */
//    private transient char[] toStringCache;
//
    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    static final long serialVersionUID = 3388685877147921107L;

    /**
     * Constructs a string buffer with no characters in it and an
     * initial capacity of 16 characters.
     */
    public StringBuffer() {
        super(16);
    }

    /**
     * Constructs a string buffer with no characters in it and
     * the specified initial capacity.
     *
     * @param      capacity  the initial capacity.
     * @exception  NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     */
    public StringBuffer(int capacity) {
        super(capacity);
    }

    /**
     * Constructs a string buffer initialized to the contents of the
     * specified string. The initial capacity of the string buffer is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    public StringBuffer(String str) {
        super(str.length() + 16);
        append(str);
    }

    /**
     * Constructs a string buffer that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string buffer is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     * <p>
     * If the length of the specified {@code CharSequence} is
     * less than or equal to zero, then an empty buffer of capacity
     * {@code 16} is returned.
     *
     * @param      seq   the sequence to copy.
     * @since 1.5
     */
    public StringBuffer(CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

//    @Override
//    public synchronized int length() {
//        return count;
//    }
//
//    @Override
//    public synchronized int capacity() {
//        return value.length;
//    }
//
//
//    @Override
//    public synchronized void ensureCapacity(int minimumCapacity) {
//        if (minimumCapacity > value.length) {
//            expandCapacity(minimumCapacity);
//        }
//    }
//
//    /**
//     * @since      1.5
//     */
//    @Override
//    public synchronized void trimToSize() {
//        super.trimToSize();
//    }
//
//    /**
//     * @throws IndexOutOfBoundsException {@inheritDoc}
//     * @see        #length()
//     */
//    @Override
//    public synchronized void setLength(int newLength) {
//        //toStringCache = null;
//        super.setLength(newLength);
//    }
//
//    /**
//     * @throws IndexOutOfBoundsException {@inheritDoc}
//     * @see        #length()
//     */
//    @Override
//    public synchronized char charAt(int index) {
//        if ((index < 0) || (index >= count))
//            throw new StringIndexOutOfBoundsException(index);
//        return value[index];
//    }
//
//    /**
//     * @since      1.5
//     */
//    @Override
//    public synchronized int codePointAt(int index) {
//        return super.codePointAt(index);
//    }
//
//    /**
//     * @since     1.5
//     */
//    @Override
//    public synchronized int codePointBefore(int index) {
//        return super.codePointBefore(index);
//    }
//
//    /**
//     * @since     1.5
//     */
//    @Override
//    public synchronized int codePointCount(int beginIndex, int endIndex) {
//        return super.codePointCount(beginIndex, endIndex);
//    }
//
//    /**
//     * @since     1.5
//     */
//    @Override
//    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
//        return super.offsetByCodePoints(index, codePointOffset);
//    }
//
//    /**
//     * @throws IndexOutOfBoundsException {@inheritDoc}
//     */
//    @Override
//    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
//                                      int dstBegin)
//    {
//        super.getChars(srcBegin, srcEnd, dst, dstBegin);
//    }
//
//    /**
//     * @throws IndexOutOfBoundsException {@inheritDoc}
//     * @see        #length()
//     */
//    @Override
//    public synchronized void setCharAt(int index, char ch) {
//        if ((index < 0) || (index >= count))
//            throw new StringIndexOutOfBoundsException(index);
//        //toStringCache = null;
//        value[index] = ch;
//    }

    @Override
    public synchronized StringBuffer append(Object obj) {
        //toStringCache = null;
        return (StringBuffer) super.append(obj);
        
    }

    @Override
    public synchronized StringBuffer append(String str) {
        //toStringCache = null;
        return (StringBuffer) super.append(str);
        
    }

    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended,
     * in order, to the contents of this {@code StringBuffer}, increasing the
     * length of this {@code StringBuffer} by the length of the argument.
     * If {@code sb} is {@code null}, then the four characters
     * {@code "null"} are appended to this {@code StringBuffer}.
     * <p>
     * Let <i>n</i> be the length of the old character sequence, the one
     * contained in the {@code StringBuffer} just prior to execution of the
     * {@code append} method. Then the character at index <i>k</i> in
     * the new character sequence is equal to the character at index <i>k</i>
     * in the old character sequence, if <i>k</i> is less than <i>n</i>;
     * otherwise, it is equal to the character at index <i>k-n</i> in the
     * argument {@code sb}.
     * <p>
     * This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code sb}).
     *
     * @param   sb   the {@code StringBuffer} to append.
     * @return  a reference to this object.
     * @since 1.4
     */
    @Override
	public synchronized StringBuffer append(StringBuffer sb) {
        //toStringCache = null;
        return (StringBuffer) super.append(sb == null ? "null" : "" + sb);        
    }

    /**
     * @since 1.8
     */
    @Override
    synchronized StringBuffer append(AbstractStringBuilder asb) {
        //toStringCache = null;
        return (StringBuffer) super.append(asb == null ? "null" : "" + asb);
        
    }

    /**
     * Appends the specified {@code CharSequence} to this
     * sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are appended,
     * in order, increasing the length of this sequence by the length of the
     * argument.
     *
     * <p>The result of this method is exactly the same as if it were an
     * invocation of this.append(s, 0, s.length());
     *
     * <p>This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code s}).
     *
     * <p>If {@code s} is {@code null}, then the four characters
     * {@code "null"} are appended.
     *
     * @param   s the {@code CharSequence} to append.
     * @return  a reference to this object.
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer append(CharSequence s) {
        //toStringCache = null;
        return (StringBuffer) super.append(s == null ? "null" : "" + s);
        
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public synchronized StringBuffer append(CharSequence s, int start, int end)
    {
        //toStringCache = null;
        return (StringBuffer) super.append(s, start, end);
        
    }

    @Override
    public synchronized StringBuffer append(char[] str) {
        //toStringCache = null;
        return (StringBuffer) super.append(str);
        
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer append(char[] str, int offset, int len) {
        //toStringCache = null;
        return (StringBuffer) super.append(str, offset, len);
        
    }

    @Override
    public synchronized StringBuffer append(boolean b) {
        //toStringCache = null;
        return (StringBuffer) super.append(b ? "true" : "false");
        
    }

    @Override
    public synchronized StringBuffer append(char c) {
        //toStringCache = null;
        return (StringBuffer) super.append(c);
        
    }

    @Override
    public synchronized StringBuffer append(int i) {
        //toStringCache = null;
        return (StringBuffer) super.append(i);
        
    }

    /**
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer appendCodePoint(int codePoint) {
        //toStringCache = null;
        return (StringBuffer) super.appendCodePoint(codePoint);
        
    }

    @Override
    public synchronized StringBuffer append(long lng) {
        //toStringCache = null;
        return (StringBuffer) super.append(lng);
        
    }

    @Override
    public synchronized StringBuffer append(float f) {
        //toStringCache = null;
        return (StringBuffer) super.append(f);
        
    }

    @Override
    public synchronized StringBuffer append(double d) {
        //toStringCache = null;
        return (StringBuffer) super.append(d);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer delete(int start, int end) {
        //toStringCache = null;
        return (StringBuffer) super.delete(start, end);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer deleteCharAt(int index) {
        //toStringCache = null;
        return (StringBuffer) super.deleteCharAt(index);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer replace(int start, int end, String str) {
        //toStringCache = null;
        return (StringBuffer) super.replace(start, end, str);
        
    }

//    /**
//     * @throws StringIndexOutOfBoundsException {@inheritDoc}
//     * @since      1.2
//     */
//    @Override
//    public synchronized String substring(int start) {
//        return substring(start, count);
//    }
//
//    /**
//     * @throws IndexOutOfBoundsException {@inheritDoc}
//     * @since      1.4
//     */
//    @Override
//    public synchronized CharSequence subSequence(int start, int end) {
//        return super.substring(start, end);
//    }
//
//    /**
//     * @throws StringIndexOutOfBoundsException {@inheritDoc}
//     * @since      1.2
//     */
//    @Override
//    public synchronized String substring(int start, int end) {
//        return super.substring(start, end);
//    }
//
    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer insert(int index, char[] str, int offset,
                                            int len)
    {
        //toStringCache = null;
        return (StringBuffer) super.insert(index, str, offset, len);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, Object obj) {
        //toStringCache = null;
        return (StringBuffer) super.insert(offset, "" + obj);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, String str) {
        //toStringCache = null;
        return (StringBuffer) super.insert(offset, str);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, char[] str) {
        //toStringCache = null;
        return (StringBuffer) super.insert(offset, str);
        
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public StringBuffer insert(int index, CharSequence s) {
        // Note, synchronization achieved via invocations of other StringBuffer methods
        // after narrowing of s to specific type
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(index, s == null ? "null" : "" + s);
        
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s,
            int start, int end)
    {
        //toStringCache = null;
        return (StringBuffer) super.insert(dstOffset, s, start, end);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public  StringBuffer insert(int offset, boolean b) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of b to String by super class method
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(offset, b);
        
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, char c) {
        //toStringCache = null;
        return (StringBuffer) super.insert(offset, c);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, int i) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of i to String by super class method
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(offset, i);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, long l) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of l to String by super class method
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(offset, l);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, float f) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of f to String by super class method
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(offset, f);
        
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, double d) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of d to String by super class method
        // Ditto for toStringCache clearing
        return (StringBuffer) super.insert(offset, d);
        
    }
//
//    /**
//     * @since      1.4
//     */
//    @Override
//    public int indexOf(String str) {
//        // Note, synchronization achieved via invocations of other StringBuffer methods
//        return super.indexOf(str);
//    }
//
//    /**
//     * @since      1.4
//     */
//    @Override
//    public synchronized int indexOf(String str, int fromIndex) {
//        return super.indexOf(str, fromIndex);
//    }
//
//    /**
//     * @since      1.4
//     */
//    @Override
//    public int lastIndexOf(String str) {
//        // Note, synchronization achieved via invocations of other StringBuffer methods
//        return super.lastIndexOf(str);
//    }
//
//    /**
//     * @since      1.4
//     */
//    @Override
//    public synchronized int lastIndexOf(String str, int fromIndex) {
//        return super.lastIndexOf(str, fromIndex);
//    }

    /**
     * @since   JDK1.0.2
     */
    @Override
    public synchronized StringBuffer reverse() {
        //toStringCache = null;
        return (StringBuffer) super.reverse();
        
    }

    @Override
    public synchronized String toString() {
    	return 秘s;
//        if (toStringCache == null) {
//            toStringCache = Arrays.copyOfRange(value, 0, count);
//        }
//        return new String(toStringCache, true);
    }
//
//    /**
//     * Serializable fields for StringBuffer.
//     *
//     * @serialField value  char[]
//     *              The backing character array of this StringBuffer.
//     * @serialField count int
//     *              The number of characters in this StringBuffer.
//     * @serialField shared  boolean
//     *              A flag indicating whether the backing array is shared.
//     *              The value is ignored upon deserialization.
//     */
//    private static final java.io.ObjectStreamField[] serialPersistentFields =
//    {
//        new java.io.ObjectStreamField("value", char[].class),
//        new java.io.ObjectStreamField("count", Integer.TYPE),
//        new java.io.ObjectStreamField("shared", Boolean.TYPE),
//    };
//
//    /**
//     * readObject is called to restore the state of the StringBuffer from
//     * a stream.
//     */
//    private synchronized void writeObject(java.io.ObjectOutputStream s)
//        throws java.io.IOException {
//        java.io.ObjectOutputStream.PutField fields = s.putFields();
//        fields.put("value", value);
//        fields.put("count", count);
//        fields.put("shared", false);
//        s.writeFields();
//    }
//
//    /**
//     * readObject is called to restore the state of the StringBuffer from
//     * a stream.
//     */
//    private void readObject(java.io.ObjectInputStream s)
//        throws java.io.IOException, ClassNotFoundException {
//        java.io.ObjectInputStream.GetField fields = s.readFields();
//        value = (char[])fields.get("value", null);
//        count = fields.get("count", 0);
//    }
}
