package com.flame.loader;

import com.flame.util.XException;
import com.flame.util.XMLInfo;
import com.flame.orm.XPersistable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlameDataLoad extends XMLInfo {
	private static final long serialVersionUID = 1L;
	private String loader = "";
	private List<LoadFile> files = new ArrayList<>();
	private List<LoadClass> models = new ArrayList<>();
	private List<LoadObject> data = new ArrayList<>();

	public static class LoadFile {
		private String filename = "";

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}

	public static class LoadClass {
		private String classname = "";

		public String getClassname() {
			return classname;
		}

		public void setClassname(String classname) {
			this.classname = classname;
		}
	}

	public static class LoadObject {
		private String type = "";
		private Class<?> clazz = null;
		private String where = "";
		private Map<String, String> attributes = new HashMap<>();
		private List<Link> links = new ArrayList<>();

		public Class<?> getClazz() {
			return this.clazz;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
			try {
				this.clazz = Class.forName(this.type);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}

		public String getWhere() {
			return where;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public String getAttribute(String name) {
			return this.attributes.get(name);
		}

		public List<Link> getLink() {
			return links;
		}

		public void addLink(Link link) {
			this.links.add(link);
		}
	}

	public static class Link {
		private String type = "";
		private Class<?> clazz = null;
		private String refer = null;
		private Map<String, String> attributes = new HashMap<>();
		private Where where;

		public String getType() {
			return type;
		}

		public Class<?> getClazz() {
			return this.clazz;
		}

		public void setType(String type) {
			this.type = type;
			try {
				this.clazz = Class.forName(this.type);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}

		public String getRefer() {
			return refer;
		}

		public void setRefer(String refer) {
			this.refer = refer;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public String getAttribute(String name) {
			return this.attributes.get(name);
		}

		public Where getWhere() {
			return where;
		}

		public Where setWhere(Where fobj) {
			this.where = fobj;
			return this.where;
		}
	}

	public static class Where {
		private String type = "";
		private Class<?> clazz = null;
		private String refer = null;
		private Map<String, String> attributes = new HashMap<>();

		public String getType() {
			return type;
		}

		public <T extends XPersistable> Class<T> getClazz() {
			return (Class<T>) this.clazz;
		}

		public void setType(String type) {
			this.type = type;
			try {
				this.clazz = Class.forName(this.type);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public String getAttribute(String name) {
			return this.attributes.get(name);
		}

		public String getRefer() {
			return refer;
		}

		public void setRefer(String refer) {
			this.refer = refer;
		}
		
		public String toString() {
			return this.type + this.attributes.toString();
		}
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public List<LoadObject> getData() {
		return data;
	}

	public void setData(List<LoadObject> list) {
		this.data = list;
	}

	public void addData(LoadObject data) {
		this.data.add(data);
	}

	public List<LoadFile> getFiles() {
		return files;
	}

	public void setFiles(List<LoadFile> files) {
		this.files = files;
	}

	public void addFile(LoadFile file) {
		this.files.add(file);
	}

	public List<LoadClass> getModels() {
		return models;
	}

	public void setModels(List<LoadClass> models) {
		this.models = models;
	}

	public void addModel(LoadClass model) {
		this.models.add(model);
	}
}
