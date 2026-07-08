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
public class XWorxMethodLauncherMain extends AbstractLauncher {
	static {
		if (!(System.out instanceof XPrintStream))
			System.setOut(new XPrintStream(System.out));

		System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
	}

	public XWorxMethodLauncherMain(Archive archive) {
		super(archive);
	}

	@Override
	protected String getMainClass() throws Exception {
		return "com.xworx.method.XWorxMethodApplication";
	}

	public static void main(String[] args) throws Exception {
		String xworxHome = XWorxMethodLauncherMain.handleEnvironment(args, "Method");
		new XWorxMethodLauncherMain(new XWorxLibArchive(new File(xworxHome))).launch(args);

		/**
		 * -拦截调用System.exit(0)后导致Eclipse停止SpringBoot失败的问题;
		 * -ThingWorx停止时会调用ThingWorxBootstrapper.contextDestroyed()->ThingWorxServer.getInstance().shutDownPlatform()->System.exit(0);
		 * -导致SpringBoot加载ThingWorx后不能够正常的在Eclipse中停止;
		 */
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
