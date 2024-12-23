/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.json.stream;


import java.io.Closeable;
import java.math.BigDecimal;

/**
 * Provides forward, read-only access to JSON data in a streaming way. This
 * is the most efficient way for reading JSON data. The class
 * {@link javax.json.Json} contains methods to create parsers from input
 * sources ({@link java.io.InputStream} and {@link java.io.Reader}).
 *
 * <p>
 * The following example demonstrates how to create a parser from a string
 * that contains an empty JSON array:
 * <pre>
 * <code>
 * JsonParser parser = Json.createParser(new StringReader("[]"));
 * </code>
 * </pre>
 *
 * <p>
 * The class {@link JsonParserFactory} also contains methods to create
 * {@code JsonParser} instances. {@link JsonParserFactory} is preferred
 * when creating multiple parser instances. A sample usage is shown
 * in the following example:
 * <pre>
 * <code>
 * JsonParserFactory factory = Json.createParserFactory();
 * JsonParser parser1 = factory.createParser(...);
 * JsonParser parser2 = factory.createParser(...);
 * </code>
 * </pre>
 * 
 * <p>
 * {@code JsonParser} parses JSON using the pull parsing programming model. 
 * In this model the client code controls the thread and calls the method 
 * {@code next()} to advance the parser to the next state after
 * processing each element. The parser can generate the following events: 
 * {@code START_OBJECT}, {@code END_OBJECT}, {@code START_ARRAY}, 
 * {@code END_ARRAY}, {@code KEY_NAME}, {@code VALUE_STRING},
 * {@code VALUE_NUMBER}, {@code VALUE_TRUE}, {@code VALUE_FALSE}, 
 * and {@code VALUE_NULL}. 
 *
 * <p>
 * <b>For example</b>, for an empty JSON object ({ }), the parser generates the event
 * {@code START_OBJECT} with the first call to the method {@code next()} and the
 * event {@code END_OBJECT} with the second call to the method {@code next()}.
 * The following code demonstrates how to access these events:
 *
 * <pre>
 * <code>
 * Event event = parser.next(); // START_OBJECT
 * event = parser.next();       // END_OBJECT
 * </code>
 * </pre>
 *
 * <p>
 * <a id="JsonParserExample2"/>
 * <p>
 * <b>For example</b>, for the following JSON:
 * <pre>
 * {
 *   "firstName": "John", "lastName": "Smith", "age": 25,
 *   "phoneNumber": [
 *       { "type": "home", "number": "212 555-1234" },
 *       { "type": "fax", "number": "646 555-4567" }
 *    ]
 * }
 * </pre>
 *
 * <p>calls to the method {@code next()} result in parse events at the specified
 * locations below (marked in bold):
 *
 * <p>
 * <pre>
 * {<B>START_OBJECT</B>
 *   "firstName"<B>KEY_NAME</B>: "John"<B>VALUE_STRING</B>, "lastName"<B>KEY_NAME</B>: "Smith"<B>VALUE_STRING</B>, "age"<B>KEY_NAME</B>: 25<B>VALUE_NUMBER</B>,
 *   "phoneNumber"<B>KEY_NAME</B> : [<B>START_ARRAY</B>
 *       {<B>START_OBJECT</B> "type"<B>KEY_NAME</B>: "home"<B>VALUE_STRING</B>, "number"<B>KEY_NAME</B>: "212 555-1234"<B>VALUE_STRING</B> }<B>END_OBJECT</B>,
 *       {<B>START_OBJECT</B> "type"<B>KEY_NAME</B>: "fax"<B>VALUE_STRING</B>, "number"<B>KEY_NAME</B>: "646 555-4567"<B>VALUE_STRING</B> }<B>END_OBJECT</B>
 *    ]<B>END_ARRAY</B>
 * }<B>END_OBJECT</B>
 * </pre>
 *
 * <p>
 * The methods {@code next()} and {@code hasNext()} enable iteration over
 * parser events to process JSON data. {@code JsonParser} provides get methods
 * to obtain the value at the current state of the parser. For example, the
 * following code shows how to obtain the value "John" from the JSON above:
 *
 * <p>
 * <pre>
 * <code>
 * Event event = parser.next(); // START_OBJECT
 * event = parser.next();       // KEY_NAME
 * event = parser.next();       // VALUE_STRING
 * parser.getString();          // "John"
 * </code>
 * </pre>
 *
 * @see javax.json.Json
 * @see JsonParserFactory
 * @author Jitendra Kotamraju
 */
public interface JsonParser extends /*Auto*/Closeable {

    /**
     * An event from {@code JsonParser}.
     */
    enum Event {
        /**
         * Start of a JSON array. The position of the parser is after '['.
         */
        START_ARRAY,
        /**
         * Start of a JSON object. The position of the parser is after '{'.
         */
        START_OBJECT,
        /**
         * Name in a name/value pair of a JSON object. The position of the parser
         * is after the key name. The method {@link #getString} returns the key
         * name.
         */
        KEY_NAME,
        /**
         * String value in a JSON array or object. The position of the parser is
         * after the string value. The method {@link #getString}
         * returns the string value.
         */
        VALUE_STRING,
        /**
         * Number value in a JSON array or object. The position of the parser is
         * after the number value. {@code JsonParser} provides the following
         * methods to access the number value: {@link #getInt},
         * {@link #getLong}, and {@link #getBigDecimal}.
         */
        VALUE_NUMBER,
        /**
         * {@code true} value in a JSON array or object. The position of the
         * parser is after the {@code true} value. 
         */
        VALUE_TRUE,
        /**
         * {@code false} value in a JSON array or object. The position of the
         * parser is after the {@code false} value.
         */
        VALUE_FALSE,
        /**
         * {@code null} value in a JSON array or object. The position of the
         * parser is after the {@code null} value.
         */
        VALUE_NULL,
        /**
         * End of a JSON object. The position of the parser is after '}'.
         */
        END_OBJECT,
        /**
         * End of a JSON array. The position of the parser is after ']'.
         */
        END_ARRAY
    }

    /**
     * Returns {@code true} if there are more parsing states. This method returns
     * {@code false} if the parser reaches the end of the JSON text.
     *
     * @return {@code true} if there are more parsing states.
     * @throws javax.json.JsonException if an i/o error occurs (IOException
     * would be cause of JsonException)
     * @throws JsonParsingException if the parser encounters invalid JSON
     * when advancing to next state.
     */
    boolean hasNext();

    /**
     * Returns the event for the next parsing state.
     *
     * @throws javax.json.JsonException if an i/o error occurs (IOException
     * would be cause of JsonException)
     * @throws JsonParsingException if the parser encounters invalid JSON
     * when advancing to next state.
     * @throws java.util.NoSuchElementException if there are no more parsing
     * states.
     */
    Event next();

    /**
     * Returns a {@code String} for the name in a name/value pair,
     * for a string value or a number value. This method should only be called
     * when the parser state is {@link Event#KEY_NAME}, {@link Event#VALUE_STRING},
     * or {@link Event#VALUE_NUMBER}.
     *
     * @return a name when the parser state is {@link Event#KEY_NAME}
     *         a string value when the parser state is {@link Event#VALUE_STRING}
     *         a number value when the parser state is {@link Event#VALUE_NUMBER}
     * @throws IllegalStateException when the parser state is not
     *      {@code KEY_NAME}, {@code VALUE_STRING}, or {@code VALUE_NUMBER}
     */
    String getString();

    /**
     * Returns true if the JSON number at the current parser state is a
     * integral number. A {@link BigDecimal} may be used to store the value
     * internally and this method semantics are defined using its
     * {@code scale()}. If the scale is zero, then it is considered integral
     * type. This integral type information can be used to invoke an
     * appropriate accessor method to obtain a numeric value as in the
     * following example:
     *
     * <pre>
     * <code>
     * JsonParser parser = ...
     * if (parser.isIntegralNumber()) {
     *     parser.getInt();     // or other methods to get integral value
     * } else {
     *     parser.getBigDecimal();
     * }
     * </code>
     * </pre>
     *
     * @return true if this number is a integral number, otherwise false
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}
     */
    boolean isIntegralNumber();

    /**
     * Returns a JSON number as an integer. The returned value is equal
     * to {@code new BigDecimal(getString()).intValue()}. Note that
     * this conversion can lose information about the overall magnitude
     * and precision of the number value as well as return a result with
     * the opposite sign. This method should only be called when the parser
     * state is {@link Event#VALUE_NUMBER}.
     *
     * @return an integer for a JSON number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}
     * @see java.math.BigDecimal#intValue()
     */
    int getInt();

    /**
     * Returns a JSON number as a long. The returned value is equal
     * to {@code new BigDecimal(getString()).longValue()}. Note that this
     * conversion can lose information about the overall magnitude and
     * precision of the number value as well as return a result with
     * the opposite sign. This method is only called when the parser state is
     * {@link Event#VALUE_NUMBER}.
     *
     * @return a long for a JSON number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}
     * @see java.math.BigDecimal#longValue()
     */
    long getLong();

    /**
     * Returns a JSON number as a {@code BigDecimal}. The {@code BigDecimal}
     * is created using {@code new BigDecimal(getString())}. This
     * method should only called when the parser state is
     * {@link Event#VALUE_NUMBER}.
     *
     * @return a {@code BigDecimal} for a JSON number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}
     */
    BigDecimal getBigDecimal();

    /**
     * Return the location that corresponds to the parser's current state in
     * the JSON input source. The location information is only valid in the
     * current parser state (or until the parser is advanced to a next state).
     *
     * @return a non-null location corresponding to the current parser state
     * in JSON input source
     */
    JsonLocation getLocation();

    /**
     * getJsonValue(JsonObject.class) is valid in the START_OBJECT state and
     * moves the cursor to END_OBJECT.
     *
     * getJsonValue(JsonArray.class) is valid in the START_ARRAY state
     * and moves the cursor to END_ARRAY.
     *
     * getJsonValue(JsonString.class) is valid in the VALUE_STRING state.
     *
     * getJsonValue(JsonNumber.class) is valid in the VALUE_NUMBER state.
     *
     * @param clazz
     * @return
     *
    public <T extends JsonValue> T getJsonValue(Class<T> clazz);
     */

    /**
     * Closes this parser and frees any resources associated with the
     * parser. This method closes the underlying input source.
     *
     * @throws javax.json.JsonException if an i/o error occurs (IOException
     * would be cause of JsonException)
     */
    @Override
    void close();

}
