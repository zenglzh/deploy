/**
 * 
 */
package com.jiuqi.deplay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;



/**
 * XML¹¤¾ß
 * @author huangkaibin
 *
 */
public class XmlUtils {

	public static Element readXml(String filePath) throws JDOMException,
			IOException {
		if (!IOUtils.existsFile(filePath)) {
			return null;
		}
		return readXml(new FileInputStream(filePath));
	}

	public static Element readXml(File file) throws JDOMException, IOException {
		if (!IOUtils.existsFile(file)) {
			return null;
		}
		return readXml(new FileInputStream(file));
	}

	public static Element readXml(InputStream input) throws JDOMException,
			IOException {
		InputStream in = null;
		try {
			in = new UTFInputStream(input);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			return doc.getRootElement();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static void saveXml(Element rootElement, String filepath)
			throws IOException {
		saveXml(rootElement, new File(filepath));
	}

	public static void saveXml(Element rootElement, File file)
			throws IOException {
		IOUtils.makeDirectory(file.getParentFile());
		saveXml(rootElement, new FileOutputStream(file));
	}

	public static void saveXml(Element rootElement, OutputStream out)
			throws IOException {
		try {
			Document doc = rootElement.getDocument();
			if (doc == null) {
				doc = new Document(rootElement);
			}
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, out);
		} finally {
			out.close();
		}
	}
	
	
	public static String getChildText(Element element, String childTag) {
		Element child = element.getChild(childTag);
		return child != null ? child.getText() : null;
	}

	public static void setChildText(Element element, String childTag, String text) {
		Element child = element.getChild(childTag);
		if (child == null) {
			child = new Element(childTag);
			element.addContent(child);
		}
		child.setText(text);
	}

	public static void forEachChild(Element element, String childTag,
			XmlElementVisitor visitor) {
		forEachChild(element.getChildren(childTag), visitor);
	}

	public static void forEachChild(Element element, String childTag1,
			String childTag2, XmlElementVisitor visitor) {
		forEachChild(element, visitor, childTag1, childTag2);
	}

	public static void forEachChild(Element element, XmlElementVisitor visitor,
			String... childTags) {
		Element e = element;
		for (int i = 0; i < childTags.length - 1; ++i) {
			e = e.getChild(childTags[i]);
			if (e == null) {
				return;
			}
		}
		forEachChild(e.getChildren(childTags[childTags.length - 1]), visitor);
	}

	private static void forEachChild(List<?> list, XmlElementVisitor visitor) {
		if (list == null || visitor == null) {
			return;
		}
		for (Object o : list) {
			Element element = (Element) o;
			visitor.visit(element);
		}
	}

	public static Element safeGetChild(Element element, String childTag) {
		Element child = new Element(childTag);
		element.addContent(child);
		return child;
	}

	public static void setValue(Element element, String name, String value) {
		if (value != null) {
			element.setAttribute(name, value);
		} else {
			element.removeAttribute(name);
		}
	}

	public static boolean getBooleanValue(Element element, String name) {
		return "1".equals(element.getAttributeValue(name));
	}

	public static void setBooleanValue(Element element, String name, boolean value) {
		if (value) {
			element.setAttribute(name, "1");
		} else {
			element.removeAttribute(name);
		}
	}

	public static boolean getBooleanValue2(Element element, String name) {
		return Boolean.parseBoolean(element.getAttributeValue(name));
	}

	public static void setBooleanValue2(Element element, String name, boolean value) {
		if (value) {
			element.setAttribute(name, "true");
		} else {
			element.removeAttribute(name);
		}
	}



	public static int getIntValue(Element element, String name) {
		String value = element.getAttributeValue(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static void setIntValue(Element element, String name, int value) {
		element.setAttribute(name, String.valueOf(value));
	}

	public static long getLongValue(Element element, String name) {
		String value = element.getAttributeValue(name);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static void setLongValue(Element element, String name, long value) {
		element.setAttribute(name, String.valueOf(value));
	}

}
