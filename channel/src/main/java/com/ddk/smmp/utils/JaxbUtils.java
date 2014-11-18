package com.ddk.smmp.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author lixin li_mr_ceo@163.com
 * @version 2013-5-9 下午01:42:10
 * @desc
 */
public class JaxbUtils {
	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @param writer
	 * @return
	 */
	public static String convertToXml(Object obj) {
		return convertToXml(obj, "UTF-8");
	}

	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			System.err.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + ",000  WARN (com.gloryscience.util.JaxbUtils:50) convert error!");
		}
		return result;
	}

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJavaBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			System.err.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + ",000  WARN (com.gloryscience.util.JaxbUtils:70) convert error!");
		}
		return t;
	}
	
	public static void main(String[] args){
		TestXmlParseObj parseObj = new TestXmlParseObj();
		parseObj.setEl1("value1");
		parseObj.setEl2("value2");
		
		List<String> testList = new ArrayList<String>();
		testList.add("listvalue1");
		testList.add("listvalue2");
		testList.add("listvalue3");
		parseObj.setTestList(testList);
		
		String xml = JaxbUtils.convertToXml(parseObj);
		System.out.println(xml);
		
		TestXmlParseObj parseObj1 = JaxbUtils.converyToJavaBean(xml, TestXmlParseObj.class);
		System.out.println(parseObj1.toString());
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "testXmlParseObj")
	@XmlType
	public static class TestXmlParseObj{
		@XmlElement
		private String el1;
		
		@XmlElement
		private String el2;
		
		@XmlElementWrapper(name = "testList")
		@XmlElement(name = "list")
		private List<String> testList;
		
		public String getEl1() {
			return el1;
		}
		
		public void setEl1(String el1) {
			this.el1 = el1;
		}
		
		public String getEl2() {
			return el2;
		}
		
		public void setEl2(String el2) {
			this.el2 = el2;
		}
		
		public List<String> getTestList() {
			return testList;
		}
		
		public void setTestList(List<String> testList) {
			this.testList = testList;
		}

		@Override
		public String toString() {
			return el1.toString() + "#" + el2.toLowerCase() + "#" + testList.toString();
		}
	}
}