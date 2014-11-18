package com.ddk.smmp.client.ws.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.client.ws.xmlObject.Binding;
import com.ddk.smmp.client.ws.xmlObject.Def;
import com.ddk.smmp.client.ws.xmlObject.Operation;
import com.ddk.smmp.client.ws.xmlObject.Param;
import com.ddk.smmp.client.ws.xmlObject.Port;
import com.ddk.smmp.client.ws.xmlObject.PortType;
import com.ddk.smmp.client.ws.xmlObject.Service;

/**
 * @author leeson 2014年7月24日 上午10:12:56 li_mr_ceo@163.com <br>
 * 
 */
public class Jdom {
	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		//Document doc = builder.build(new File("smsi.wsdl"));
		//Document doc = builder.build(new File("geoipservice.asmx.xml"));
		Document doc = builder.build(new File("webService.xml"));
		Element definition = doc.getRootElement();
		
		/*===========获取definition===========*/
		Def def = new Def();
		def.setName(definition.getAttributeValue("name"));
		def.setNameSpace(definition.getAttributeValue("targetNamespace"));
		for(Object nsp : definition.getAdditionalNamespaces()){
			Namespace namespace = (Namespace)nsp;
			if(namespace.getURI().equalsIgnoreCase(def.getNameSpace())){
				def.setPrefix(namespace.getPrefix());
			}
		}
		
		final String nodePrefix = definition.getNamespacePrefix();
		
		/*===========获取service===========*/
		List<Element> services = definition.getContent(new Filter() {
			private static final long serialVersionUID = -2303656210661749907L;
			@Override
			public boolean matches(Object arg0) {
				if(arg0 instanceof Element){
					Element el = (Element)arg0;
					if(el.getName().equalsIgnoreCase("service")){
						return true;
					}
				}
				return false;
			}
		});
		
		for(Element el : services){
			Service service = new Service();
			service.setName(el.getAttributeValue("name"));
			
			/*===========获取port===========*/
			List<Element> ports = el.getContent(new Filter() {
				private static final long serialVersionUID = -4769433000956969166L;

				@Override
				public boolean matches(Object arg0) {
					if(arg0 instanceof Element){
						Element el = (Element)arg0;
						if(el.getName().equalsIgnoreCase("port")){
							return true;
						}
					}
					return false;
				}
			});
			
			for(Element port_ : ports){
				Port port = new Port();
				port.setName(port_.getAttributeValue("name"));
				port.setAddress(((Element)port_.getChildren().get(0)).getAttributeValue("location"));
				
				service.getPortList().add(port);
				
				/*===========获取bind===========*/
				final String bindName = port_.getAttributeValue("binding").replace(def.getPrefix() + ":", "");
				
				List<Element> bindings = definition.getContent(new Filter() {
					private static final long serialVersionUID = -4754235257534212266L;
					@Override
					public boolean matches(Object arg0) {
						if(arg0 instanceof Element){
							Element el = (Element)arg0;
							if(el.getName().equalsIgnoreCase("binding")){
								if(el.getAttributeValue("name").equals(bindName)){
									return true;
								}
							}
						}
						return false;
					}
				});
				
				Element bind_ = bindings.get(0);
				Binding binding = new Binding();
				binding.setName(bind_.getAttributeValue("name"));
				//获取协议类型
				String protocolStr = ((Element)bind_.getChildren().get(0)).getNamespacePrefix();
				if(protocolStr.equalsIgnoreCase("soap") || protocolStr.equalsIgnoreCase("soap12")){
					binding.setProtocol(protocolStr);
				}
				if(protocolStr.equalsIgnoreCase("http")){
					binding.setProtocol(((Element)bind_.getChildren().get(0)).getAttributeValue("verb"));
				}
				port.setBinding(binding);
				
				/*===========获取portType ===========*/
				final String portTypeName = bind_.getAttributeValue("type").replace(def.getPrefix() + ":", "");
				List<Element> portTypes = definition.getContent(new Filter() {
					private static final long serialVersionUID = -4044611653581176928L;
					@Override
					public boolean matches(Object arg0) {
						if(arg0 instanceof Element){
							Element el = (Element)arg0;
							if(el.getName().equalsIgnoreCase("portType")){
								if(el.getAttributeValue("name").equals(portTypeName)){
									return true;
								}
							}
						}
						return false;
					}
				});
				
				Element portType_ = portTypes.get(0);
				PortType portType = new PortType();
				portType.setName(portType_.getAttributeValue("name"));
				
				binding.setPortType(portType);
				
				/*===========获取operation ===========*/
				List<Element> operations = portType_.getContent(new Filter() {
					private static final long serialVersionUID = -8144179824220839482L;
					@Override
					public boolean matches(Object arg0) {
						if(arg0 instanceof Element){
							Element el = (Element)arg0;
							if(el.getName().equalsIgnoreCase("operation")){
								return true;
							}
						}
						return false;
					}
				});
				
				for(Element operation_ : operations){
					Operation operation = new Operation();
					operation.setName(operation_.getAttributeValue("name"));
					
					final String operatorName = operation_.getAttributeValue("name");
					List<Element> operator_temp = bind_.getContent(new Filter() {
						private static final long serialVersionUID = -7100509421566120733L;
						@Override
						public boolean matches(Object arg0) {
							if(arg0 instanceof Element){
								Element el = (Element)arg0;
								if(el.getName().equalsIgnoreCase("operation")){
									if(el.getAttributeValue("name").equals(operatorName)){
										return true;
									}
								}
							}
							return false;
						}
					});
					
					Element el_ = (Element)operator_temp.get(0).getChildren().get(0);
					if(el_.getNamespacePrefix().equalsIgnoreCase("http")){
						operation.setUrl(el_.getAttributeValue("location"));
					}
					if(el_.getNamespacePrefix().equalsIgnoreCase("soap") || el_.getNamespacePrefix().equalsIgnoreCase("soap12")){
						operation.setUrl(el_.getAttributeValue("soapAction"));
					}
					
					portType.getOperationList().add(operation);
					
					/*===========获取message===========*/
					List opChildList = operation_.getChildren();
					for(Object obj : opChildList){
						if(obj instanceof Element){
							Element el__ = (Element)obj;
							
							/*===========input===========*/
							if(el__.getName().equals("input")){
								final String inputNameStr = el__.getAttributeValue("message").replace(def.getPrefix() + ":", "");
								
								List<Param> inputParamList = getParamList(definition, inputNameStr, def.getPrefix());
								operation.getInput().addAll(inputParamList);
							}
							
							/*===========output===========*/
							if(el__.getName().equals("output")){
								final String outputNameStr = el__.getAttributeValue("message").replace(def.getPrefix() + ":", "");
								
								List<Param> outputParamList = getParamList(definition, outputNameStr, def.getPrefix());
								operation.getOutput().addAll(outputParamList);
							}
						}
					}
				}
			}
			def.getServiceList().add(service);
		}
		
		JSONObject jsonObject = new JSONObject(true);
		System.out.println(jsonObject.toJSONString(def, true));
	}
	
	/**
	 * 获取标准类型参数
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Element getParamElement(Element root, final String name){
		List<Element> typesList = root.getContent(new Filter() {
			private static final long serialVersionUID = 7407286128255502243L;
			@Override
			public boolean matches(Object arg0) {
				if(arg0 instanceof Element){
					Element el = (Element)arg0;
					if(el.getName().equalsIgnoreCase("types")){
						return true;
					}
				}
				return false;
			}
		});
		
		if(typesList.size() > 0){
			Element types = typesList.get(0);
			List<Element> schemaList = types.getContent(new Filter() {
				private static final long serialVersionUID = 7407286128255502243L;
				@Override
				public boolean matches(Object arg0) {
					if(arg0 instanceof Element){
						Element el = (Element)arg0;
						if(el.getName().equalsIgnoreCase("schema")){
							return true;
						}
					}
					return false;
				}
			});
			
			if(schemaList.size() > 0){
				Element schema = schemaList.get(0);
				List<Element> elementList = schema.getContent(new Filter() {
					private static final long serialVersionUID = 7407286128255502243L;
					@Override
					public boolean matches(Object arg0) {
						if(arg0 instanceof Element){
							Element el = (Element)arg0;
							if(el.getName().equalsIgnoreCase("element")){
								if(el.getAttributeValue("name").equals(name)){
									return true;
								}
							}
						}
						return false;
					}
				});
				
				if(elementList.size() > 0){
					return elementList.get(0);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 获取复杂类型参数
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Element getParamComplexType(Element root, final String name){
		List<Element> typesList = root.getContent(new Filter() {
			private static final long serialVersionUID = 7407286128255502243L;
			@Override
			public boolean matches(Object arg0) {
				if(arg0 instanceof Element){
					Element el = (Element)arg0;
					if(el.getName().equalsIgnoreCase("types")){
						return true;
					}
				}
				return false;
			}
		});
		
		if(typesList.size() > 0){
			Element types = typesList.get(0);
			List<Element> schemaList = types.getContent(new Filter() {
				private static final long serialVersionUID = 7407286128255502243L;
				@Override
				public boolean matches(Object arg0) {
					if(arg0 instanceof Element){
						Element el = (Element)arg0;
						if(el.getName().equalsIgnoreCase("schema")){
							return true;
						}
					}
					return false;
				}
			});
			
			if(schemaList.size() > 0){
				Element schema = schemaList.get(0);
				List<Element> elementList = schema.getContent(new Filter() {
					private static final long serialVersionUID = 7407286128255502243L;
					@Override
					public boolean matches(Object arg0) {
						if(arg0 instanceof Element){
							Element el = (Element)arg0;
							if(el.getName().equalsIgnoreCase("complexType")){
								if(el.getAttributeValue("name").equals(name)){
									return true;
								}
							}
						}
						return false;
					}
				});
				
				if(elementList.size() > 0){
					return elementList.get(0);
				}
			}
		}
		
		return null;
	}
	/**
	 * 获取message
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Element getMessage(Element root, final String name){
		List<Element> messageList = root.getContent(new Filter() {
			private static final long serialVersionUID = 7407286128255502243L;
			@Override
			public boolean matches(Object arg0) {
				if(arg0 instanceof Element){
					Element el = (Element)arg0;
					if(el.getName().equalsIgnoreCase("message")){
						if(el.getAttributeValue("name").equals(name)){
							return true;
						}
					}
				}
				return false;
			}
		});
		
		if(messageList.size() > 0){
			return messageList.get(0);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Param> getParamList(Element root, String paramMessageName, String prefix){
		List<Param> params = new LinkedList<Param>();
		
		Element element = getMessage(root, paramMessageName);
		
		List<Element> partList = element.getContent(new Filter() {
			private static final long serialVersionUID = 7407286128255502243L;
			@Override
			public boolean matches(Object arg0) {
				if(arg0 instanceof Element){
					Element el = (Element)arg0;
					if(el.getName().equalsIgnoreCase("part")){
						return true;
					}
				}
				return false;
			}
		});
		
		for(Element part : partList){
			String type = part.getAttributeValue("type");
			if(StringUtils.isNotEmpty(type)){
				//基本类型
				Param param = new Param();
				param.setName(part.getAttributeValue("name"));
				param.setType(type);
				params.add(param);
			}else{
				//复杂类型
				final String name_ = part.getAttributeValue("element").replace(prefix + ":", "");
				
				Element parameElement = getParamElement(root, name_);
				
				if(StringUtils.isNotEmpty(parameElement.getAttributeValue("type"))){
					final String name__ = parameElement.getAttributeValue("type").replace(prefix + ":", "");
					Element parameComplexType = getParamComplexType(root, name__);
					
					Element e1 = parameComplexType.getChild("sequence", parameComplexType.getNamespace());
					if(null == e1){
						e1 = parameComplexType.getChild("all", parameComplexType.getNamespace());
					}
					if(null != e1){
						List<Element> parameterList = e1.getChildren("element", parameComplexType.getNamespace());
						
						for(Element el : parameterList){
							Param param = new Param();
							param.setName(el.getAttributeValue("name"));
							param.setType(el.getAttributeValue("type"));
							params.add(param);
						}
					}
				}else{
					Element ct = parameElement.getChild("complexType", parameElement.getNamespace());
					if(null != ct){
						Element e1 = ct.getChild("sequence", ct.getNamespace());
						if(null == e1){
							e1 = ct.getChild("all", ct.getNamespace());
						}
						if(null != e1){
							List<Element> parameterList = e1.getChildren("element", ct.getNamespace());
							
							for(Element el : parameterList){
								if(el.getAttributeValue("type").startsWith(prefix)){
									final String name__ = el.getAttributeValue("type").replace(prefix + ":", "");
									Element parameComplexType = getParamComplexType(root, name__);
									
									Element e1_ = parameComplexType.getChild("sequence", parameComplexType.getNamespace());
									if(null == e1_){
										e1_ = parameComplexType.getChild("all", parameComplexType.getNamespace());
									}
									if(null != e1_){
										List<Element> parameterList_ = e1_.getChildren("element", parameComplexType.getNamespace());
										
										for(Element el_ : parameterList_){
											Param param = new Param();
											param.setName(el_.getAttributeValue("name"));
											param.setType(el_.getAttributeValue("type"));
											params.add(param);
										}
									}
								}else{
									Param param = new Param();
									param.setName(el.getAttributeValue("name"));
									param.setType(el.getAttributeValue("type"));
									params.add(param);
								}
							}
						}
					}
				}
			}
		}
		
		return params;
	}
}
