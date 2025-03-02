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

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * IdentityHashMap
 * 
 * This is a variant on HashMap which tests equality by reference instead of by
 * value. Basically, keys and values are compared for equality by checking if
 * their references are equal rather than by calling the "equals" function.
 * 
 * IdentityHashMap uses open addressing (linear probing in particular) for
 * collision resolution. This is different from HashMap which uses Chaining.
 * 
 * Like HashMap, IdentityHashMap is not thread safe, so access by multiple
 * threads must be synchronized by an external mechanism such as
 * Collections.synchronizedMap.
 * 
 * @since 1.4
 */
public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements
        Map<K, V>, Serializable, Cloneable {

    private static final long serialVersionUID = 8188218128353913216L;

    /*
     * The internal data structure to hold key value pairs This array holds keys
     * and values in an alternating fashion.
     */
    transient Object[] elementData;

    /* Actual number of key-value pairs. */
    int size;

    /*
     * maximum number of elements that can be put in this map before having to
     * rehash.
     */
    transient int threshold;

    /*
     * default threshold value that an IdentityHashMap created using the default
     * constructor would have.
     */
    private static final int DEFAULT_MAX_SIZE = 21;

    /* Default load factor of 0.75; */
    private static final int loadFactor = 7500;

    /*
     * modification count, to keep track of structural modifications between the
     * IdentityHashMap and the iterator
     */
    transient int modCount = 0;

    /*
     * Object used to represent null keys and values. This is used to
     * differentiate a literal 'null' key value pair from an empty spot in the
     * map.
     */
    private static final Object NULL_OBJECT = new Object();  //$NON-LOCK-1$

    static class IdentityHashMapEntry<K, V> extends MapEntry<K, V> {
        IdentityHashMapEntry(K theKey, V theValue) {
            super(theKey, theValue);
        }

        @Override
        public Object clone() {
            return super.clone();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) object;
                return (key == entry.getKey()) && (value == entry.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(key)
                    ^ System.identityHashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value; //$NON-NLS-1$
        }
    }

    static class IdentityHashMapIterator<E, KT, VT> implements Iterator<E> {
        private int position = 0; // the current position

        // the position of the entry that was last returned from next()
        private int lastPosition = 0;

        final IdentityHashMap<KT, VT> associatedMap;

        int expectedModCount;

        final MapEntry.Type<E, KT, VT> type;

        boolean canRemove = false;

        IdentityHashMapIterator(MapEntry.Type<E, KT, VT> value,
                IdentityHashMap<KT, VT> hm) {
            associatedMap = hm;
            type = value;
            expectedModCount = hm.modCount;
        }

        @Override
		public boolean hasNext() {
            while (position < associatedMap.elementData.length) {
                // if this is an empty spot, go to the next one
                if (associatedMap.elementData[position] == null) {
                    position += 2;
                } else {
                    return true;
                }
            }
            return false;
        }

        void checkConcurrentMod() throws ConcurrentModificationException {
            if (expectedModCount != associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
		public E next() {
            checkConcurrentMod();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            IdentityHashMapEntry<KT, VT> result = associatedMap
                    .getEntry(position);
            lastPosition = position;
            position += 2;

            canRemove = true;
            return type.get(result);
        }

        @Override
		public void remove() {
            checkConcurrentMod();
            if (!canRemove) {
                throw new IllegalStateException();
            }

            canRemove = false;
            associatedMap.remove(associatedMap.elementData[lastPosition]);
            position = lastPosition;
            expectedModCount++;
        }
    }

    static class IdentityHashMapEntrySet<KT, VT> extends
            AbstractSet<Map.Entry<KT, VT>> {
        private final IdentityHashMap<KT, VT> associatedMap;

        public IdentityHashMapEntrySet(IdentityHashMap<KT, VT> hm) {
            associatedMap = hm;
        }

        IdentityHashMap<KT, VT> hashMap() {
            return associatedMap;
        }

        @Override
        public int size() {
            return associatedMap.size;
        }

        @Override
        public void clear() {
            associatedMap.clear();
        }

        @Override
        public boolean remove(Object object) {
            if (contains(object)) {
                associatedMap.remove(((Map.Entry) object).getKey());
                return true;
            }
            return false;
        }

        @Override
        public boolean contains(Object object) {
            if (object instanceof Map.Entry) {
                IdentityHashMapEntry<?, ?> entry = associatedMap
                        .getEntry(((Map.Entry) object).getKey());
                // we must call equals on the entry obtained from "this"
                return entry != null && entry.equals(object);
            }
            return false;
        }

        @Override
        public Iterator<Map.Entry<KT, VT>> iterator() {
            return new IdentityHashMapIterator<Map.Entry<KT, VT>, KT, VT>(
                    new MapEntry.Type<Map.Entry<KT, VT>, KT, VT>() {
                        @Override
						public Map.Entry<KT, VT> get(MapEntry<KT, VT> entry) {
                            return entry;
                        }
                    }, associatedMap);
        }
    }

    /**
     * Create an IdentityHashMap with default maximum size
     */
    public IdentityHashMap() {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Create an IdentityHashMap with the given maximum size parameter
     * 
     * @param maxSize
     *            The estimated maximum number of entries that will be put in
     *            this map.
     */
    public IdentityHashMap(int maxSize) {
        if (maxSize >= 0) {
            this.size = 0;
            threshold = getThreshold(maxSize);
            elementData = newElementArray(computeElementArraySize());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int getThreshold(int maxSize) {
        // assign the threshold to maxSize initially, this will change to a
        // higher value if rehashing occurs.
        return maxSize > 3 ? maxSize : 3;
    }

    private int computeElementArraySize() {
        return (int) (((long) threshold * 10000) / loadFactor) * 2;
    }

    /**
     * Create a new element array
     * 
     * @param s
     *            the number of elements
     * @return Reference to the element array
     */
    private Object[] newElementArray(int s) {
        return new Object[s];
    }

    /**
     * Create an IdentityHashMap using the given Map as initial values.
     * 
     * @param map
     *            A map of (key,value) pairs to copy into the IdentityHashMap
     */
    public IdentityHashMap(Map<? extends K, ? extends V> map) {
        this(map.size() < 6 ? 11 : map.size() * 2);
        putAllImpl(map);
    }

    @SuppressWarnings("unchecked")
    private V massageValue(Object value) {
        return (V) ((value == NULL_OBJECT) ? null : value);
    }

    /**
     * Removes all elements from this Map, leaving it empty.
     * 
     * @exception UnsupportedOperationException
     *                when removing from this Map is not supported
     * 
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear() {
        size = 0;
        for (int i = 0; i < elementData.length; i++) {
            elementData[i] = null;
        }
        modCount++;
    }

    /**
     * Searches this Map for the specified key.
     * 
     * @param key
     *            the object to search for
     * @return true if <code>key</code> is a key of this Map, false otherwise
     */
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            key = NULL_OBJECT;
        }

        int index = findIndex(key, elementData);
        return elementData[index] == key;
    }

    /**
     * Searches this Map for the specified value.
     * 
     * 
     * @param value
     *            the object to search for
     * @return true if <code>value</code> is a value of this Map, false
     *         otherwise
     */
    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            value = NULL_OBJECT;
        }

        for (int i = 1; i < elementData.length; i = i + 2) {
            if (elementData[i] == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Answers the value of the mapping with the specified key.
     * 
     * @param key
     *            the key
     * @return the value of the mapping with the specified key
     */
    @Override
    public V get(Object key) {
        if (key == null) {
            key = NULL_OBJECT;
        }

        int index = findIndex(key, elementData);

        if (elementData[index] == key) {
            Object result = elementData[index + 1];
            return massageValue(result);
        }

        return null;
    }

    private IdentityHashMapEntry<K, V> getEntry(Object key) {
        if (key == null) {
            key = NULL_OBJECT;
        }

        int index = findIndex(key, elementData);
        if (elementData[index] == key) {
            return getEntry(index);
        }

        return null;
    }

    /**
     * Convenience method for getting the IdentityHashMapEntry without the
     * NULL_OBJECT elements
     */
    @SuppressWarnings("unchecked")
    private IdentityHashMapEntry<K, V> getEntry(int index) {
        Object key = elementData[index];
        Object value = elementData[index + 1];

        if (key == NULL_OBJECT) {
            key = null;
        }
        if (value == NULL_OBJECT) {
            value = null;
        }

        return new IdentityHashMapEntry<K, V>((K) key, (V) value);
    }

    /**
     * Returns the index where the key is found at, or the index of the next
     * empty spot if the key is not found in this table.
     */
    private int findIndex(Object key, Object[] array) {
        int length = array.length;
        int index = getModuloHash(key, length);
        int last = (index + length - 2) % length;
        while (index != last) {
            if (array[index] == key || (array[index] == null)) {
                /*
                 * Found the key, or the next empty spot (which means key is not
                 * in the table)
                 */
                break;
            }
            index = (index + 2) % length;
        }
        return index;
    }

    private int getModuloHash(Object key, int length) {
        return ((System.identityHashCode(key) & 0x7FFFFFFF) % (length / 2)) * 2;
    }

    /**
     * Maps the specified key to the specified value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the value of any previous mapping with the specified key or null
     *         if there was no mapping
     */
    @Override
    public V put(K key, V value) {
        Object _key = key;
        Object _value = value;
        if (_key == null) {
            _key = NULL_OBJECT;
        }

        if (_value == null) {
            _value = NULL_OBJECT;
        }

        int index = findIndex(_key, elementData);

        // if the key doesn't exist in the table
        if (elementData[index] != _key) {
            modCount++;
            if (++size > threshold) {
                rehash();
                index = findIndex(_key, elementData);
            }

            // insert the key and assign the value to null initially
            elementData[index] = _key;
            elementData[index + 1] = null;
        }

        // insert value to where it needs to go, return the old value
        Object result = elementData[index + 1];
        elementData[index + 1] = _value;

        return massageValue(result);
    }
    
    /**
     * Copies all the mappings in the given map to this map. These mappings will
     * replace all mappings that this map had for any of the keys currently in
     * the given map.
     * 
     * @param map
     *            the Map to copy mappings from
     * @throws NullPointerException
     *             if the given map is null
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        putAllImpl(map);
    }

    private void rehash() {
        int newlength = elementData.length << 1;
        if (newlength == 0) {
            newlength = 1;
        }
        Object[] newData = newElementArray(newlength);
        for (int i = 0; i < elementData.length; i = i + 2) {
            Object key = elementData[i];
            if (key != null) {
                // if not empty
                int index = findIndex(key, newData);
                newData[index] = key;
                newData[index + 1] = elementData[i + 1];
            }
        }
        elementData = newData;
        computeMaxSize();
    }

    private void computeMaxSize() {
        threshold = (int) ((long) (elementData.length / 2) * loadFactor / 10000);
    }

    /**
     * Removes a mapping with the specified key from this IdentityHashMap.
     * 
     * @param key
     *            the key of the mapping to remove
     * @return the value of the removed mapping, or null if key is not a key in
     *         this Map
     */
    @Override
    public V remove(Object key) {
        if (key == null) {
            key = NULL_OBJECT;
        }

        boolean hashedOk;
        int index, next, hash;
        Object result, object;
        index = next = findIndex(key, elementData);

        if (elementData[index] != key) {
            return null;
        }

        // store the value for this key
        result = elementData[index + 1];

        // shift the following elements up if needed
        // until we reach an empty spot
        int length = elementData.length;
        while (true) {
            next = (next + 2) % length;
            object = elementData[next];
            if (object == null) {
                break;
            }

            hash = getModuloHash(object, length);
            hashedOk = hash > index;
            if (next < index) {
                hashedOk = hashedOk || (hash <= next);
            } else {
                hashedOk = hashedOk && (hash <= next);
            }
            if (!hashedOk) {
                elementData[index] = object;
                elementData[index + 1] = elementData[next + 1];
                index = next;
            }
        }

        size--;
        modCount++;

        // clear both the key and the value
        elementData[index] = null;
        elementData[index + 1] = null;

        return massageValue(result);
    }

    /**
     * Answers a Set of the mappings contained in this IdentityHashMap. Each
     * element in the set is a Map.Entry. The set is backed by this Map so
     * changes to one are reflected by the other. The set does not support
     * adding.
     * 
     * @return a Set of the mappings
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new IdentityHashMapEntrySet<K, V>(this);
    }

    /**
     * Answers a Set of the keys contained in this IdentityHashMap. The set is
     * backed by this IdentityHashMap so changes to one are reflected by the
     * other. The set does not support adding.
     * 
     * @return a Set of the keys
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new AbstractSet<K>() {
                @Override
                public boolean contains(Object object) {
                    return containsKey(object);
                }

                @Override
                public int size() {
                    return IdentityHashMap.this.size();
                }

                @Override
                public void clear() {
                    IdentityHashMap.this.clear();
                }

                @Override
                public boolean remove(Object key) {
                    if (containsKey(key)) {
                        IdentityHashMap.this.remove(key);
                        return true;
                    }
                    return false;
                }

                @Override
                public Iterator<K> iterator() {
                    return new IdentityHashMapIterator<K, K, V>(
                            new MapEntry.Type<K, K, V>() {
                                @Override
								public K get(MapEntry<K, V> entry) {
                                    return entry.key;
                                }
                            }, IdentityHashMap.this);
                }
            };
        }
        return keySet;
    }

    /**
     * Answers a Collection of the values contained in this IdentityHashMap. The
     * collection is backed by this IdentityHashMap so changes to one are
     * reflected by the other. The collection does not support adding.
     * 
     * @return a Collection of the values
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<V>() {
                @Override
                public boolean contains(Object object) {
                    return containsValue(object);
                }

                @Override
                public int size() {
                    return IdentityHashMap.this.size();
                }

                @Override
                public void clear() {
                    IdentityHashMap.this.clear();
                }

                @Override
                public Iterator<V> iterator() {
                    return new IdentityHashMapIterator<V, K, V>(
                            new MapEntry.Type<V, K, V>() {
                                @Override
								public V get(MapEntry<K, V> entry) {
                                    return entry.value;
                                }
                            }, IdentityHashMap.this);
                }

                @Override
                public boolean remove(Object object) {
                    Iterator<?> it = iterator();
                    while (it.hasNext()) {
                        if (object == it.next()) {
                            it.remove();
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        return values;
    }

    /**
     * Compares this map with other objects. This map is equal to another map is
     * it represents the same set of mappings. With this map, two mappings are
     * the same if both the key and the value are equal by reference.
     * 
     * When compared with a map that is not an IdentityHashMap, the equals
     * method is not necessarily symmetric (a.equals(b) implies b.equals(a)) nor
     * transitive (a.equals(b) and b.equals(c) implies a.equals(c)).
     * 
     * @return whether the argument object is equal to this object
     */
    @Override
    public boolean equals(Object object) {
        /*
         * We need to override the equals method in AbstractMap because
         * AbstractMap.equals will call ((Map) object).entrySet().contains() to
         * determine equality of the entries, so it will defer to the argument
         * for comparison, meaning that reference-based comparison will not take
         * place. We must ensure that all comparison is implemented by methods
         * in this class (or in one of our inner classes) for reference-based
         * comparison to take place.
         */
        if (this == object) {
            return true;
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map) object;
            if (size() != map.size()) {
                return false;
            }

            Set<Map.Entry<K, V>> set = entrySet();
            // ensure we use the equals method of the set created by "this"
            return set.equals(map.entrySet());
        }
        return false;
    }

    /**
     * Answers a new IdentityHashMap with the same mappings and size as this
     * one.
     * 
     * @return a shallow copy of this IdentityHashMap
     * 
     * @see java.lang.Cloneable
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Answers if this IdentityHashMap has no elements, a size of zero.
     * 
     * @return true if this IdentityHashMap has no elements, false otherwise
     * 
     * @see #size
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Answers the number of mappings in this IdentityHashMap.
     * 
     * @return the number of mappings in this IdentityHashMap
     */
    @Override
    public int size() {
        return size;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size);
        Iterator<?> iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            MapEntry<?, ?> entry = (MapEntry) iterator.next();
            stream.writeObject(entry.key);
            stream.writeObject(entry.value);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {
        stream.defaultReadObject();
        int savedSize = stream.readInt();
        threshold = getThreshold(DEFAULT_MAX_SIZE);
        elementData = newElementArray(computeElementArraySize());
        for (int i = savedSize; --i >= 0;) {
            K key = (K) stream.readObject();
            put(key, (V) stream.readObject());
        }
        size = savedSize;
    }
    
    private void putAllImpl(Map<? extends K, ? extends V> map) {
        if (map.entrySet() != null) {
            super.putAll(map);
        }
    }
}
