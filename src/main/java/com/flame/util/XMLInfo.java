package com.flame.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;

public abstract class XMLInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public void beanToXML(OutputStream out) throws IOException, XException {
		try {
			JAXBContext context = JAXBContext.newInstance(this.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, out);
			out.flush();
		} catch (JAXBException e) {
			throw new XException(e);
		} finally {
			out.close();
		}
	}

	public String toXML() throws JAXBException {
		String xml = null;
		JAXBContext context = JAXBContext.newInstance(this.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		m.marshal(this, writer);
		xml = writer.toString();
		return xml;
	}

	@SuppressWarnings("unchecked")
	public static <T extends XMLInfo> T xmlToBean(String xml, Class<T> clazz) {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			throw new XException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends XMLInfo> T xmlToBean(InputStream instream, Class<T> clazz, String charset) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStreamReader isReader = new InputStreamReader(instream, charset);
			return (T) unmarshaller.unmarshal(isReader);
		} catch (JAXBException e) {
			throw new XException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends XMLInfo> T xmlToBean(InputStream instream, Class<T> clazz) {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStreamReader isReader = new InputStreamReader(instream);
			return (T) unmarshaller.unmarshal(isReader);
		} catch (JAXBException e) {
			throw new XException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends XMLInfo> T xmlToBean(File file, Class<T> clazz) throws IOException {
		try (FileInputStream inStream = new FileInputStream(file);) {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStreamReader isReader = new InputStreamReader(inStream);
			return (T) unmarshaller.unmarshal(isReader);
		} catch (JAXBException e) {
			throw new XException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends XMLInfo> T xmlToBean(File file, Class<T> clazz, String charset) throws IOException {
		try (FileInputStream inStream = new FileInputStream(file);) {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStreamReader isReader = new InputStreamReader(inStream, charset);
			return (T) unmarshaller.unmarshal(isReader);
		} catch (JAXBException e) {
			throw new XException(e);
		}
	}

}
