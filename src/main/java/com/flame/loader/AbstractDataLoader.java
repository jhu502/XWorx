package com.flame.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.XException;

public abstract class AbstractDataLoader implements IDataLoader {
	public static void load(String path) throws Exception {
		String loadFile = BasicConfiguration.getXWHome() + File.separator + path;
		try (InputStream instream = new FileInputStream(loadFile)) {
			FlameDataLoad flameData = AbstractDataLoader.load(instream);
			try {
				Constructor<?> constructor = Class.forName(flameData.getLoader()).getConstructor();
				IDataLoader dataLoader = (IDataLoader) constructor.newInstance();
				dataLoader.executeLoad(flameData);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new XException(e);
			}
		}
	}

	public static FlameDataLoad load(InputStream instream) {
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(instream);
			Element rootElement = document.getRootElement();
			FlameDataLoad flameLoad = new FlameDataLoad();
			flameLoad.setLoader(rootElement.attribute("loader").getStringValue());
			for (Element element : rootElement.elements()) {
				if ("LoadObject".equals(element.getName())) {
					FlameDataLoad.LoadObject loadObject = new FlameDataLoad.LoadObject();
					loadObject.setType(element.attribute("type").getStringValue());
					loadObject.setWhere(element.attribute("where").getStringValue());
					for (Element e : element.elements()) {
						if ("attr".equals(e.getName())) {
							loadObject.getAttributes().put(e.attributeValue("name"), e.getText());
						} else if ("link".equals(e.getName())) {
							FlameDataLoad.Link link = new FlameDataLoad.Link();
							loadObject.addLink(link);
							link.setType(e.attributeValue("type"));
							link.setRefer(e.attributeValue("refer"));
							for (Element o : e.elements()) {
								if ("attr".equals(o.getName())) {
									link.getAttributes().put(o.attributeValue("name"), o.getText());
								} else if ("where".equals(o.getName())) {
									FlameDataLoad.Where where = link.setWhere(new FlameDataLoad.Where());
									where.setRefer(o.attributeValue("refer"));
									where.setType(o.attributeValue("type"));
									for (Element p : o.elements()) {
										if ("attr".equals(p.getName())) {
											where.getAttributes().put(p.attributeValue("name"), p.getText());
										}
									}
								}
							}
						}
					}
					flameLoad.addData(loadObject);
				} else if ("LoadFile".equals(element.getName())) {
					FlameDataLoad.LoadFile loadFile = new FlameDataLoad.LoadFile();
					loadFile.setFilename(element.attribute("filename").getStringValue());
					flameLoad.addFile(loadFile);
				} else if ("LoadClass".equals(element.getName())) {
					FlameDataLoad.LoadClass loadModel = new FlameDataLoad.LoadClass();
					loadModel.setClassname(element.attribute("classname").getStringValue());
					flameLoad.addModel(loadModel);
				}
			}

			return flameLoad;
		} catch (DocumentException e) {
			throw new XException(e);
		}
	}

	public List<?> queryObject(Class<?> clazz, Object[][] params) {
		StringBuffer hql = new StringBuffer();
		hql.append("select a from ").append(clazz.getSimpleName()).append(" a ");

		Object[][] _params = new Object[params.length][];
		if (params.length > 0) {
			hql.append(" where ");
			boolean bool = true;
			int i = 0;
			for (Object[] objs : params) {
				String field = (String) objs[0];

				String param = "A" + i;
				_params[i] = new Object[] { param, objs[1] };
				if (bool) {
					hql.append("a.").append(field).append("=").append(":").append(param);
				} else {
					hql.append(" and ").append("a.").append(field).append("=").append(":").append(param);
				}
				bool = false;
				i = i + 1;
			}
		}
		return PersistenceHelper.service().query(hql.toString(), _params);
	}

	public List<?> queryObject(FlameDataLoad.LoadObject data) {
		Class<?> clazz = data.getClazz();
		String identity = data.getWhere();
		String[] strs = identity.split(",");
		Object[][] params = new Object[strs.length][];
		for (int i = 0; i < strs.length; i++) {
			String key = strs[i];
			String value = data.getAttribute(key);
			params[i] = new Object[]{key, value};
		}

		return this.queryObject(clazz, params);
	}

	public List<?> queryObject(FlameDataLoad.Where where) {
		where.getAttributes();
		Object[][] params = new Object[where.getAttributes().size()][];
		int i = 0;
		for (Entry<String, String> entry : where.getAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			params[i++] = new Object[]{key, value};
		}

		return this.queryObject(where.getClazz(), params);
	}

	public List<?> queryLink(Class<?> clazz, XObject left, XObject right) {
		StringBuffer hql = new StringBuffer();
		hql.append("select a from ").append(clazz.getSimpleName()).append(" a where a.left.id = :left and a.right.id = :right");
		Object[][] params = new Object[2][];
		params[0] = new Object[] { "left", left.getXid() };
		params[1] = new Object[] { "right", right.getXid() };

		return PersistenceHelper.service().query(hql.toString(), params);
	}

	/**
	 * 通过反射设置对象的字段值
	 * <p>
	 * 根据字段名查找对应的setter方法并调用，如果对象不支持该字段则忽略
	 *
	 * @param obj       目标对象
	 * @param fieldName 字段名（如 "prompt"）
	 * @param value     要设置的值
	 * @return 如果设置成功返回true，否则返回false
	 */
	protected boolean setFieldIfSupported(Object obj, String fieldName, Object value) {
		if (obj == null || fieldName == null || fieldName.isEmpty()) {
			return false;
		}

		String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		try {
			Class<?> valueClass = value != null ? value.getClass() : String.class;
			Method setter = obj.getClass().getMethod(setterName, valueClass);
			setter.invoke(obj, value);
			return true;
		} catch (NoSuchMethodException e) {
			// 尝试使用String参数
			if (value != null && !(value instanceof String)) {
				try {
					Method setter = obj.getClass().getMethod(setterName, String.class);
					setter.invoke(obj, value.toString());
					return true;
				} catch (Exception ex) {
					// 忽略
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public abstract void executeLoad(FlameDataLoad dataLoad) throws Exception;

	public static void main(String[] args) throws Exception {
		AbstractDataLoader.load("loadFiles/com/flame/auths/NavMenu-FlameSystem.xml");
	}
}
