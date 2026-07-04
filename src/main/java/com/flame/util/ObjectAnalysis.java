package com.flame.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ObjectAnalysis implements java.io.Serializable {
	private static final long serialVersionUID = 7762411094435947L;

	static class DelayAnalysis implements Runnable {
		private Object obj = null;
		private String path = "";
		private int sleep = 0;

		public DelayAnalysis(Object obj, String path, int millis) {
			this.obj = obj;
			this.path = path;
			this.sleep = millis;
		}

		public void run() {
			if (obj != null && path != null && path.trim().length() > 0) {
				try {
					Thread.sleep(this.sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ObjectAnalysis.generateXML4Object(obj, path);
			}
		}
	}

	public static void generateXML4Object(File file, OutputStream out) {
		generateXML4Object(readObject(file), out);
	}

	public static void generateXML4Object(File file, String path) {
		Object[] objs = readObject(file);
		generateXML4Object(objs, path);
	}

	public static void generateXML4Object(Object obj, OutputStream out) {
		if (obj == null)
			return;
		XStream xStream = new XStream(new DomDriver());
		xStream.toXML(obj, out);
	}

	public static void generateXML4Object(Object obj, String path, int millis) {
		if (obj == null)
			return;
		DelayAnalysis da = new DelayAnalysis(obj, path, millis);
		Thread thread = new Thread(da);
		thread.start();
	}

	public static void generateXML4Object(Object obj, String path) {
		if (obj == null || path == null || path.trim().length() == 0)
			return;
		XStream xStream = new XStream(new DomDriver());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			xStream.toXML(obj, fos);
		} catch (FileNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
		}
	}

	public static Object[] readObject(byte[] in) {
		HashSet<Object> hashset = new LinkedHashSet<Object>();
		if (in == null || in.length == 0)
			return hashset.toArray();
		InputStream is = new ByteArrayInputStream(in);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			while (is.available() > 0) {
				Object obj = readObject(ois);
				hashset.add(obj);
			}
		} catch (FileNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} catch (IOException e) {
			com.flame.util.XException.throwException(e);
		} catch (ClassNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
			}
		}
		return hashset.toArray();
	}

	public static Object[] readObject(File file) {
		List<Object> list = new ArrayList<Object>();
		if (file == null || !file.exists())
			return list.toArray();
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				Object obj = readObject(ois);
				list.add(obj);
			}
		} catch (FileNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} catch (IOException e) {
			com.flame.util.XException.throwException(e);
		} catch (ClassNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
			}
		}
		return list.toArray();
	}

	public static Object readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		Object obj = null;
		if (ois == null)
			return obj;
		try {
			obj = ois.readObject();
		} catch (OptionalDataException e) {
			try {
				obj = ois.readInt();
			} catch (OptionalDataException e0) {
				throw e0;
			}
		}
		return obj;
	}

	public static void writeObject2File(Serializable ser, File file) {
		if (ser == null || file == null)
			return;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			if (ser instanceof Object[]) {
				Object[] objs = (Object[]) ser;
				for (int i = 0; i < objs.length; i++) {
					oos.writeObject(objs[i]);
				}
			} else
				oos.writeObject(ser);
			oos.flush();
		} catch (FileNotFoundException e) {
			com.flame.util.XException.throwException(e);
		} catch (IOException e) {
			com.flame.util.XException.throwException(e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					com.flame.util.XException.throwException(e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					XException.throwException(e);
				}
			}
		}
	}
}
