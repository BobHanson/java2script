/* Copyright 1998, 2005 The Apache Software Foundation or its licensors, as applicable
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang.reflect;

/**
 * This class must be implemented by the vm vendor. This class provides methods
 * to dynamically create and access arrays.
 * 
 * @j2sPrefix
 * Array.getComponentType = function () {
 * 	return Object;
 * };
 */
public final class Array {

	/**
	 * Return the element of the array at the specified index. This reproduces
	 * the effect of <code>array[index]</code> If the array component is a
	 * base type, the result is automatically wrapped.
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element, possibly wrapped
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native Object get(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * boolean if possible. This reproduces the effect of
	 * <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native boolean getBoolean(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * byte if possible. This reproduces the effect of <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native byte getByte(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * char if possible. This reproduces the effect of <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native char getChar(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * double if possible. This reproduces the effect of
	 * <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native double getDouble(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * float if possible. This reproduces the effect of
	 * <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native float getFloat(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to an
	 * int if possible. This reproduces the effect of <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native int getInt(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the length of the array. This reproduces the effect of
	 * <code>array.length</code>
	 * 
	 * @param array
	 *            the array
	 * @return the length
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array
	 */
	public static native int getLength(Object array)
			throws IllegalArgumentException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * long if possible. This reproduces the effect of <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native long getLong(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Return the element of the array at the specified index, converted to a
	 * short if possible. This reproduces the effect of
	 * <code>array[index]</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @return the requested element
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the element cannot be
	 *                converted to the requested type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native short getShort(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	private static native Object multiNewArrayImpl(Class<?> componentType,
			int dimensions, int[] dimensionsArray);

	private static native Object newArrayImpl(Class<?> componentType, int dimension);

	/**
	 * Return a new multidimensional array of the specified component type and
	 * dimensions. This reproduces the effect of
	 * <code>new componentType[d0][d1]...[dn]</code> for a dimensions array of {
	 * d0, d1, ... , dn }
	 * 
	 * @param componentType
	 *            the component type of the new array
	 * @param dimensions
	 *            the dimensions of the new array
	 * @return the new array
	 * @exception java.lang.NullPointerException
	 *                if the component type is null
	 * @exception java.lang.NegativeArraySizeException
	 *                if any of the dimensions are negative
	 * @exception java.lang.IllegalArgumentException
	 *                if the array of dimensions is of size zero, or exceeds the
	 *                limit of the number of dimension for an array (currently
	 *                255)
	 * 
	 * @j2sIgnore
	 */
	public static Object newInstance(Class<?> componentType, int[] dimensions)
			throws NegativeArraySizeException, IllegalArgumentException {
		return null;
	}

	/**
	 * Return a new array of the specified component type and length. This
	 * reproduces the effect of <code>new componentType[size]</code>
	 * 
	 * @param componentType
	 *            the component type of the new array
	 * @param size
	 *            the length of the new array
	 * @return the new array
	 * @exception java.lang.NullPointerException
	 *                if the component type is null
	 * @exception java.lang.NegativeArraySizeException
	 *                if the size if negative
     * 
     * @j2sNative
     * return Clazz.newArray (length); 
	 */
	public static Object newInstance(Class<?> componentType, int size)
			throws NegativeArraySizeException {
		return null;
	}

	/**
	 * Set the element of the array at the specified index to the value. This
	 * reproduces the effect of <code>array[index] = value</code> If the array
	 * component is a base type, the value is automatically unwrapped
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void set(Object array, int index, Object value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the boolean value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setBoolean(Object array, int index, boolean value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the byte value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setByte(Object array, int index, byte value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the char value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setChar(Object array, int index, char value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the double value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setDouble(Object array, int index, double value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the float value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setFloat(Object array, int index, float value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the int value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setInt(Object array, int index, int value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the long value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setLong(Object array, int index, long value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

	/**
	 * Set the element of the array at the specified index to the short value.
	 * This reproduces the effect of <code>array[index] = value</code>
	 * 
	 * @param array
	 *            the array
	 * @param index
	 *            the index
	 * @param value
	 *            the new value
	 * @exception java.lang.NullPointerException
	 *                if the array is null
	 * @exception java.lang.IllegalArgumentException
	 *                if the array is not an array or the value cannot be
	 *                converted to the array type by a widening conversion
	 * @exception java.lang.ArrayIndexOutOfBoundsException
	 *                if the index is out of bounds -- negative or greater than
	 *                or equal to the array length
	 */
	public static native void setShort(Object array, int index, short value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

}
