package com.xworx.boot;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.XWorxLibArchive;

import com.flame.util.XConstants;
import com.xworx.boot.launch.AbstractLauncher;

/**
 * XWorx Zookeeper启动入口
 * 
 * @author hujin
 *
 */
public class ZookeeperLauncherMain extends AbstractLauncher {
	static {
		System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
	}

	public ZookeeperLauncherMain(Archive archive) {
		super(archive);
	}

	@Override
	protected String getMainClass() throws Exception {
		return "com.zookeeper.XWorxZookeeperApplication";
	}

	public static void main(String[] args) throws Exception {
		String xworxHome = ZookeeperLauncherMain.handleEnvironment(args, "Zookeeper");
		new ZookeeperLauncherMain(new XWorxLibArchive(new File(xworxHome))).launch(args);

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
