package com.xworx.boot.launch;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.Launcher;

import com.flame.util.XConstants;

/**
 * Base class for executable archive {@link AbstractLauncher}s.
 *
 * @author Hujin
 * @since 1.0.0
 */
public abstract class AbstractLauncher extends Launcher {
	private static final Log LOGGER = LogFactory.getLog(AbstractLauncher.class);
	private static final String BASE_LIB = "lib" + File.separator;
	private final Archive archive;
	private final Set<URL> jarsSet = new HashSet<>();

	protected AbstractLauncher(Archive archive) {
		try {
			this.archive = archive;

			File baseHome = this.getArchive().getRootDirectory();
			if (baseHome.exists()) {
				/**
				 * 收集$WT_HOME/codebase/lib下所有的jar
				 */
				File baseLib = new File(baseHome, BASE_LIB);
				if (baseLib.exists() && baseLib.isDirectory()) {
					for (File file : baseLib.listFiles()) {
						if (!file.isFile() || !file.getName().toLowerCase().endsWith(".jar"))
							continue;
						this.jarsSet.add(file.toURI().toURL());
					}
				}
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	protected Set<URL> getClassPathUrls() throws Exception {
		return this.jarsSet;
	}

	@Override
	protected boolean isExploded() {
		return this.archive.isExploded();
	}

	@Override
	protected final Archive getArchive() {
		return this.archive;
	}

	@Override
	protected ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
		return new XWorxURLClassLoader(getArchive(), urls.toArray(new URL[0]), this.getClass().getClassLoader());
	}

	/**
	 * 用来处理XWorx_Home的目录
	 * 
	 * @param args
	 * @param name
	 */
	protected static String handleEnvironment(String[] args, String name) {
		/**
		 * ApplicationArguments是用于获取应该程序的命令行参数的接口, 获取从命令行传递的参数
		 */
		ApplicationArguments arguments = new DefaultApplicationArguments(args);
		List<String> options = arguments.getOptionValues(XConstants.XWORX_HOME);
		/**
		 * xworx.home:指向XWorx Server资源文件&class的存放目录, 例如：D:\SourceSpace\SpaceFlame\XServer
		 */
		String xworxHome = "";
		if (options != null) {
			xworxHome = options.get(0);
			System.setProperty(XConstants.XWORX_HOME, xworxHome);
		} else {
			xworxHome = System.getProperty("user.dir");
			System.setProperty(XConstants.XWORX_HOME, xworxHome);
		}
		/**
		 * xworx.jvm.id被logback.xml使用去输出日志文件名包括jvm.id，例如:XWorx.20210722-13080@jhu502.log
		 */
		LOGGER.info("XWorx " + name + " starting:" + System.getProperty("xworx.jvm.id"));
		
		/**
		 * 配置 Arthas 输出目录，避免在 XServer 根目录生成 arthas-output
		 */
		String arthasOutputPath = xworxHome + File.separator + "storage" + File.separator + "arthas";
		System.setProperty("arthas.outputPath", arthasOutputPath);
		System.setProperty("arthas.dumpPath", arthasOutputPath);
		File arthasDir = new File(arthasOutputPath);
		if (!arthasDir.exists()) {
			arthasDir.mkdirs();
		}
		
		return xworxHome;
	}
}
