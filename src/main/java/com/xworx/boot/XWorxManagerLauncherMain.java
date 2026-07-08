package com.xworx.boot;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.XWorxLibArchive;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;
import com.xworx.boot.launch.AbstractLauncher;

/**
 * XWorx Manager启动入口
 * 
 * @author hujin
 *
 */
public class XWorxManagerLauncherMain extends AbstractLauncher {
	static {
		if (!(System.out instanceof XPrintStream))
			System.setOut(new XPrintStream(System.out));

		System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
	}

	public XWorxManagerLauncherMain(Archive archive) {
		super(archive);
	}

	@Override
	protected void launch(String[] args) throws Exception {
		super.launch(args);
	}

	@Override
	protected String getMainClass() throws Exception {
		return "com.xworx.XWorxManagerApplication";
	}

	public static void main(String[] args) throws Exception {
		String xworxHome = XWorxManagerLauncherMain.handleEnvironment(args, "Manager");
		new XWorxManagerLauncherMain(new XWorxLibArchive(new File(xworxHome))).launch(args);

		//	System.setSecurityManager(new SecurityManager() {
		//		@Override
		//		public void checkPermission(Permission perm) {
		//		}
		//
		//		@Override
		//		public void checkPermission(Permission perm, Object context) {
		//		}
		//
		//		@Override
		//		public void checkExit(int status) {
		//			super.checkExit(status);
		//			throw new SecurityException();
		//		}
		//	});
	}

}
