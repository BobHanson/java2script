package swingjs.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.helpers.AbstractMarshallerImpl;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import javajs.util.Base64;
import swingjs.JSUtil;
import swingjs.api.js.DOMNode;

/**
 * Relatively simple marshaller -- incomplete implementation
 * 
 * @author hansonr
 *
 */
public class JSJAXBMarshaller extends AbstractMarshallerImpl {

	// TODO: Transpiler needs to accurately indicate which fields and methods to use.
	//       As it is, we rely on @XmlAttribute and propOrder or explicit @XmlElement
	
	private Writer writer;
	private OutputStream outputStream;
	private JAXBContext context;
	private StreamResult result;
	private JAXBElement<?> jaxbElement;

	public JSJAXBMarshaller(JAXBContext context) {
		this.context = context;
	}

	@Override
	public void marshal(Object jaxbElement, Result result) throws JAXBException {
		Object javaObject;
		if (jaxbElement instanceof JAXBElement) {
			jaxbElement = (JAXBElement<?>) jaxbElement;
			javaObject = this.jaxbElement.getValue();
		} else {
			javaObject = jaxbElement;
		}
		this.result = (StreamResult) result;
		this.writer = this.result.getWriter();
		this.outputStream = this.result.getOutputStream();
		Class<?> javaClass = ((JSJAXBContext) context).getjavaClass();
		doMarshal(javaClass, javaObject, null, false);
	}

	/**
	 * The main entry point for all marshalling; iterates for embedded classes
	 * 
	 * @param javaClass    the class being marshalled
	 * @param javaObject   the object being marshalled
	 * @param field   the field for this class; null for the root
	 * @param addXsiType TODO
	 * @throws JAXBException
	 */
	private void doMarshal(Class<?> javaClass, Object javaObject, JSJAXBField field, boolean addXsiType) throws JAXBException {
		// at least for now we rely on fields that are designated.

		JSJAXBClass jaxbClass = new JSJAXBClass(javaClass, javaObject);
		jaxbClass.field = field;
		
		Map<String, Integer> oldMap = null;
		if (field == null) {
			clearQualifierMap();
		} else { 
			oldMap = newQualifierMap(null);
		}
		
		writeXML(jaxbClass, field == null, addXsiType);
		
		newQualifierMap(oldMap);
	}

	
	private static String defaultNamespace;

	private static Map<String, Integer> mapQualifierLevel = new Hashtable<String, Integer>();
	
	private static Map<String, Integer> newQualifierMap(Map<String, Integer> newMap) {
		if (newMap != null) {
			mapQualifierLevel = newMap;
			return null;
		}
		Map<String, Integer> oldMap = mapQualifierLevel;
		mapQualifierLevel = new Hashtable<String, Integer>(oldMap);
		return oldMap;
	}
	private static void clearQualifierMap() {
		// not thread safe
		mapQualifierLevel.clear();
		defaultNamespace = null;
	}
	
	private static String getXmlnsIfUnusedYet(QName qname, boolean isRoot) {
		// TODO Q: Does this have to be hierarchical - first time within this group?
		String xmlns = qname.getNamespaceURI();
		if (xmlns.length() == 0)
			return null;
		if (mapQualifierLevel.isEmpty())
			defaultNamespace = xmlns;
		if (!isRoot && !mapQualifierLevel.containsKey(xmlns)) {
			mapQualifierLevel.put(xmlns, Integer.valueOf(1));
			return xmlns;
		}
		return null;
	}
	
	private static String getXMLQname(QName qname, boolean allowDefault) {
		String l = qname.getLocalPart();
		String ns = qname.getNamespaceURI();
		return (ns == null || ns.length() == 0 
				|| allowDefault && ns.equals(defaultNamespace) ? l : qname.getPrefix() + ":" + l);
	}

	private static boolean isArray(Object value) {
		if (value == null || value instanceof byte[])
			return false;
		/** @j2sNative return !!value.__ARRAYTYPE */
		{
			return false;
		}
	}

private static JSJAXBField getField(JSJAXBClass jaxbClass, String javaName) {
		return jaxbClass.getFieldFromJavaName(javaName);
	}

	private static DOMNode textarea;
	
	private static String escapeString(String str, boolean isAttribute) {
		// JavaScript trick here to coerce HTML entities without 
		// having to code them. reading this.innerText gets reverse 
		if (textarea == null)
			textarea = DOMNode.createElement("textarea", null);
		/**
		 * @j2sNative str= (this.textarea.innerHTML=str,this.textarea.innerHTML);
		 */
		if (isAttribute)
			str = str.replace("\"", "&quot;");
		
		/**
		 * quick replacement 
		 * 
		 * @j2sNative
		 * 
		 * for (var i = str.length; --i >= 0;) {
		 *   var c = str.codePointAt(i);
		 * 	 if (c >= 127 || c < 32)
		 * 	   str = str.substring(0, i) + "&#" + c + ";" + str.substring(i + 1);
		 * }
		 * 
		 */
		return str;
	}

	
	/////////// output methods //////////////
	
	private void writeXML(JSJAXBClass jaxbClass, boolean isRoot, boolean addXsiType) throws JAXBException {
		QName qname = (jaxbClass.field == null ? jaxbClass.qname : jaxbClass.field.qualifiedName);
		if (isRoot) {
			outputHeader();
		}
		getXmlnsIfUnusedYet(qname, isRoot);
		writeTagOpen(qname, !isRoot);   
		if (addXsiType) {
			addNameSpaceIfNeeded(jaxbClass.qualifiedTypeName, isRoot);
			outputInstanceType(getEntryType(jaxbClass.qualifiedTypeName, jaxbClass.field.getValue()));
		}
		if (isRoot)
			addDefaultNameSpace();
		addAllNameSpaces(jaxbClass);
		addFields(jaxbClass, true);
		if (!jaxbClass.hasElements()) {
			output(" />");
			return;
		}
		output(">");
		addFields(jaxbClass, false);
		if (isRoot)
			output("\n");
		writeTagClose(qname, !isRoot);
	}
	
	private final static QName xsi = new QName("http://www.w3.org/2001/XMLSchema-instance", "xs", "xsi");
	private final static QName xs = new QName("http://www.w3.org/2001/XMLSchema", "_", "xs");

	private void addAllNameSpaces(JSJAXBClass jaxbClass) throws JAXBException {
		addNameSpaceIfNeeded(jaxbClass.qname, true);
			for (int i = 0, n = jaxbClass.fields.size(); i < n; i++) {
				JSJAXBField f = jaxbClass.fields.get(i);
				addNameSpaceIfNeeded(f.qualifiedName, false);
				if (f.boundListNodes != null)
					addNameSpaceIfNeeded(f.qualifiedName, false);
			}
		addNameSpaceIfNeeded(xsi, false);
		addNameSpaceIfNeeded(xs, false);
	}

	private void addFields(JSJAXBClass jaxbClass, boolean isAttribute) throws JAXBException {
		// no ordering for attributes -- they must be designated explicitly
		JSJAXBField field;
		if (jaxbClass.propOrder.size() == 0 || isAttribute) {
			for (int i = 0, n = jaxbClass.fields.size(); i < n; i++)
				if (isAttribute == (field = jaxbClass.fields.get(i)).isAttribute)
					addField(field);
		} else {
			for (int i = 0, n = jaxbClass.propOrder.size(); i < n; i++) {
				field = getField(jaxbClass, jaxbClass.propOrder.get(i));
				if (!field.isAttribute)
					addField(field);
			}
 		}
	}

	private void addField(JSJAXBField field) throws JAXBException {
		if (field != null)
			addFieldListable(field, field.getValue(), field.holdsObjects != JSJAXBField.NO_OBJECT);
	}

	private void addFieldListable(JSJAXBField field, Object value, boolean addXsiType) throws JAXBException {
		if (value == null) {
			if (field.isNillable)
	 			writeField(field, null, addXsiType);
			return;
		}
		if (JSJAXBClass.needsMarshalling(value)) {
			doMarshal(value.getClass(), value, field, addXsiType);
			return;
		}
		if (value instanceof List) {
			writeFieldList(field, (List<?>) value);
		} else if (value instanceof Map) {
			writeFieldMap(field, (Map<?, ?>) value);
		} else if (isArray(value)) {
			writeFieldArray(field, value);
		} else {
			writeField(field, value, addXsiType);
		}
	}

	private void writeField(JSJAXBField field, Object value, boolean addXsiType) throws JAXBException {
		if (field.isAttribute) {
			writeAttribute(field, value);
		} else if (field.isValue) {
			writeValue(field, value);
		} else {
			writeTagOpen(field.qualifiedName, true);
			if (value == null && field.isNillable) {
				outputNil();
				output(" />");
				return;
			}
			if (addXsiType) {
				outputInstanceType(getEntryType(null, value));
			}
			output(">");
			writeValue(field, value);
			writeTagClose(field.qualifiedName, true);
		}
	}

//  <list1>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">TESTING</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">null</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="complex" ns2:cb="c&quot;&lt;&gt;b" bytes="ZGU=">
//      <ca>ca</ca>
//  </list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:boolean">true</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:byte">1</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:short">2</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:int">3</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:long">4</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:float">6.6</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:double">8.8</list>
//  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:integer">12345678910111213141516</list>
//  </list1>

	private String getEntryType(QName qname, Object value) throws JAXBException {
		String className = value.getClass().getName();
		switch (className) {
		case "Integer":
			return "xs:int";
		case "Boolean":
		case "Byte":
		case "Short":
		case "Long":
		case "Float":
		case "Double":
			return "xs:" + className.toLowerCase();
		case "java.lang.String":
			return "xs:string";
		case "java.math.BigInteger":
			return "xs:integer";
		default:
			if (qname != null)
				return getXMLQname(qname, true);
			className = JSJAXBClass.getXmlNameFromClassName(className);
			return className.substring(0, 1).toLowerCase() + className.substring(1);
		}
	}

	private void writeAttribute(JSJAXBField field, Object value) throws JAXBException {
		// null attributes are not allowed
		if (value == null)
			return;
		addNameSpaceIfNeeded(field.qualifiedName, false);
		output(" " + getXMLQname(field.qualifiedName, true) + "=\"");
		writeValue(field, value);
		output("\"");
	}

	private void writeTagOpen(QName qname, boolean allowDefault) throws JAXBException {
		output("\n<" + getXMLQname(qname, allowDefault));
		addNameSpaceIfNeeded(qname, !allowDefault);
	}

	private void addDefaultNameSpace() throws JAXBException {
		if (defaultNamespace != null)
			output(" xmlns=\"" + defaultNamespace + "\"");
			
	}
	private void addNameSpaceIfNeeded(QName qname, boolean isRoot) throws JAXBException {
		String ns = getXmlnsIfUnusedYet(qname, isRoot);
		if (ns != null)
			output(" xmlns:" + qname.getPrefix() + "=\"" + ns + "\"");
	}

	private void writeTagClose(QName qname, boolean allowDefault) throws JAXBException {
		output("</" + getXMLQname(qname, allowDefault) + ">");
	}

	private void writeValue(JSJAXBField field, Object value) throws JAXBException {
		if (field.xmlSchema != null) {
			writeSchema(field, value);
		} else if (field.typeAdapter != null) {
			writeTypeAdapter(field, value);
		} else if (field.mimeType != null) {
			writeMimeType(field, value);
		} else {
			outputSimple(field, value);
		}
	}

	private void writeMimeType(JSJAXBField field, Object value) throws JAXBException {
		JSUtil.notImplemented(field.text);
		outputSimple(field, value);
	}

	private void writeTypeAdapter(JSJAXBField field, Object value) throws JAXBException {
		XmlAdapter adapter = field.getAdapter();
		if (adapter == null) {
			writeValue(field, value);
		} else {
			try {
				output((String) adapter.marshal(value));
			} catch (Exception e) {
				System.out.println(e + " trying to marshal " + field.text);
				outputSimple(field, value);				
			}
		}
			
		
	}

	private void writeSchema(JSJAXBField field, Object value) throws JAXBException {
		if (value instanceof JSXMLGregorianCalendarImpl){
			writeDate(field, value);
		} else {
			switch (field.xmlSchema) {
			case "hexBinary":
				output(DatatypeConverter.printHexBinary((byte[]) value));
			break;
			case "base64Binary":
				output(DatatypeConverter.printBase64Binary((byte[]) value));
				break;
			default:
				System.out.println("schema not supported " + field.xmlSchema);
				// fall through //
			case "xsd:ID":
				outputSimple(field, value);
				break;
			}
		}
	}
	private void writeDate(JSJAXBField field, Object value) throws JAXBException {
		JSXMLGregorianCalendarImpl cal = ((JSXMLGregorianCalendarImpl) value);
		QName schema = cal.xmlSchema;
		if (field.xmlSchema != null)
			cal.setXMLSchemaType(field.xmlSchema);
		output(cal.toXMLFormat());
		cal.xmlSchema = schema;
	}
	
	private static final QName qnEntryKey = new QName("","key","");

	private static final QName qnEntryValue = new QName("","value","");

	private void writeFieldArray(JSJAXBField field, Object values) throws JAXBException {
		Object[] list = (Object[]) values;
		boolean asList = field.asList;
		boolean isNillable = !asList && field.isNillable;
		QName wrapName = field.qualifiedWrapName;
		boolean isNull = (values == null);
		boolean isEmpty = (!isNull && list.length == 0);
		if (asList)
			wrapName = field.qualifiedName;
		if (wrapName != null) {
 			writeTagOpen(wrapName, true);
			output(">");
		} else 	if (isEmpty && !isNillable) {
			return;
		}
		for (int i = 0, pt = 0, n = list.length; i < n; i++) {
			Object value = list[i];
			if (asList) {
				if (value == null)
					continue;
				if (pt++ > 0)
					output(" ");
				outputSimple(field, value);
			} else {
				if (value == null && !isNillable)
					continue;
				addFieldListable(field, value, field.javaClassName.equals("Object[]"));
			}
		}
		if (wrapName != null) {
			if (!asList)
				output("\n");
			writeTagClose(wrapName, true);
		}
	}

	private void writeFieldList(JSJAXBField field, List<?> list) throws JAXBException {
		boolean asList = field.asList;
		boolean isNillable = !asList && field.isNillable;
		QName wrapName = field.qualifiedWrapName;
		boolean isNull = (list == null);
		boolean isEmpty = (!isNull && list.isEmpty());
		if (asList)
			wrapName = field.qualifiedName;
		if (wrapName != null) {
 			writeTagOpen(wrapName, true);
			output(">");
		} else 	if (isEmpty && !isNillable) {
			return;
		}
		for (int i = 0, pt = 0, n = list.size(); i < n; i++) {
			Object value = list.get(i);
			if (asList) {
				if (value == null)
					continue;
				if (pt++ > 0)
					output(" ");
				outputSimple(field, value);
			} else {
				if (value == null && !isNillable)
					continue;
				addFieldListable(field, value, field.holdsObjects != JSJAXBField.NO_OBJECT);
			}
		}
		if (wrapName != null) {
			if (!asList)
				output("\n");
			writeTagClose(wrapName, true);
		}
	}

	private void writeFieldMap(JSJAXBField field, Map<?, ?> map) throws JAXBException {
//	    <hm>
//        <entry>
//            <key>null</key>
//        </entry>
//        <entry>
//            <key>testing</key>
//            <value>TESTING</value>
//        </entry>
//    </hm>
		boolean isNillable = field.isNillable;
		QName wrapName = field.qualifiedWrapName;
		if (wrapName == null)
			wrapName = field.qualifiedName;
		boolean isNull = (map == null);
		boolean isEmpty = (!isNull && map.isEmpty());
		writeTagOpen(wrapName, true);
		output(">\n");
		QName qn = field.qualifiedName;
		for (Entry<?, ?> e : map.entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();
			output("<entry>");
			field.qualifiedName = qnEntryKey;
			field.entryValue = key;
			addFieldListable(field, key, ((field.holdsObjects & JSJAXBField.MAP_KEY_OBJECT) != 0));			
			if (value != null || isNillable) {
				field.entryValue = value;
				field.qualifiedName = qnEntryValue;
				addFieldListable(field, value, ((field.holdsObjects & JSJAXBField.MAP_VALUE_OBJECT) != 0));
			}
			field.entryValue = null;
			output("\n</entry>\n");
		}
		field.entryValue = null;
		field.qualifiedName = qn;
		writeTagClose(wrapName, true);
    }

	private void outputHeader() throws JAXBException {
		output("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	}

	private void outputSimple(JSJAXBField field, Object value) throws JAXBException {
		if (value == null)
			return;
		String sval = null;
		/**
		 * @j2sNative sval = (value.toString ? value.toString() : "" + value);
		 */
		output(escapeString(sval, field.isAttribute));
	}

	private void outputNil() throws JAXBException {
			output(" xsi:nil=\"true\"");
	}

	private void outputInstanceType(String type) throws JAXBException {
		output(" xsi:type=\""+type+"\"");
	}

	/**
	 * Every write ends up here.
	 * 
	 * @param s
	 * @throws JAXBException
	 */
	private void output(String s) throws JAXBException {
		try {
			if (writer != null) {
				writer.write(s);
			} else if (outputStream != null) {
				outputStream.write(s.getBytes());
			}
		} catch (IOException e) {
			throw new JAXBException("Error writing string " + s);
		}
	}

	// general findings:
	//
	//                 PROPERTY         FIELD           PUBLIC_MEMBER    NONE
	//
	// field default   @XmlTransient    @XmlElement     @XmlTransient    @XmlTransient
	//                                                   if not public
	//                                                  @XmlElement
	//                                                   otherwise
	//
	// method default @XmlTransient     @XmlTransient    same as above;  @XmlTransient
	//                 unless there                      if both field
	//                 are matching                      and method are
	//                 get/is and set                    public, ONE must
    //                                                   be explicitly
	//                                                   @XmlElement
	

	
}