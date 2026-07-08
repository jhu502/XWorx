package com.xworx.boot;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.XWorxLibArchive;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;
import com.xworx.boot.launch.AbstractLauncher;

/**
 * XWorx MethodServer启动入口
 * 
 * @author hujin
 *
 */
public class XWorxIgniteLauncherMain extends AbstractLauncher {
	static {
		if (!(System.out instanceof XPrintStream))
			System.setOut(new XPrintStream(System.out));

		System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
	}

	public XWorxIgniteLauncherMain(Archive archive) {
		super(archive);
	}

	@Override
	protected String getMainClass() throws Exception {
		return "com.ignite.XWorxIgniteApplication";
	}

	public static void main(String[] args) throws Exception {
		String xworxHome = XWorxIgniteLauncherMain.handleEnvironment(args, "Ignite");

		new XWorxIgniteLauncherMain(new XWorxLibArchive(new File(xworxHome))).launch(args);
	}
}
