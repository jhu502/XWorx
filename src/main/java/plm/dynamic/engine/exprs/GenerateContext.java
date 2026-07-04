package plm.dynamic.engine.exprs;

import java.lang.Thread.UncaughtExceptionHandler;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import plm.dynamic.engine.type.MatrixType;
import com.flame.util.XException;

import javassist.ClassPool;

public class GenerateContext implements Runnable {
	private Generator generator;

	public GenerateContext(Generator generator) {
		this.generator = generator;
	}

	private void setClassLoaderContext() {
		ExpressClassLoader loader = ExpressClassLoader.newClassLoader();
		Thread.currentThread().setContextClassLoader(loader);
	}

	public static Thread execGenContext(Generator generator) {
		GenerateContext context = new GenerateContext(generator);
		Thread thread = new Thread(context);
		thread.setUncaughtExceptionHandler(new ErrHandler());
		thread.start();

		return thread;
	}

	/**
	 * JDK9开始，AppClassLoader他爹再也不是 URLClassLoader，因此自定义ExpressClassLoader需要继承于SecureClassLoader
	 * @author hujin
	 */
	static class ExpressClassLoader extends SecureClassLoader {
		protected ExpressClassLoader(String name, ClassLoader parent) {
			super(name, parent);
		}

		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return super.loadClass(name);
		}

		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> clas = super.findClass(name);
			return clas;
		}

		static ExpressClassLoader newClassLoader() {
			ClassLoader base = ClassLoader.getSystemClassLoader();

			return new ExpressClassLoader("XWorx-Express-Loader", base);
		}
	}

	static class ErrHandler implements UncaughtExceptionHandler {
		/**
		 * 这里可以做任何针对异常的处理,比如记录日志等等
		 */
		public void uncaughtException(Thread a, Throwable e) {
			XException.throwException(e);
		}
	}

	@Override
	public void run() {
		setClassLoaderContext();
		try {
			ClassPool clsPool = ClassPool.getDefault();
			clsPool.importPackage(MatrixType.class.getName());
			Map<String, Class<Expression>> duplication = new HashMap<>();
			generator.execute(clsPool, duplication);
		} catch (Exception e) {
			generator.setException(e);
		}
	}
}
