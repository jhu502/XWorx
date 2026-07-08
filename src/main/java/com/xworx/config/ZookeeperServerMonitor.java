package com.xworx.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.flame.config.basic.BasicConfiguration;
import com.flame.util.XConstants;
import com.flame.util.XProperties;
import com.xworx.zookeeper.ZookeeperApplicationRunner;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小，越先加载；
 * 在XWorxManager中，@Order好像不生效，但是实现Ordered接口后就生效了
 */
@Configuration
@Order(200)
public class ZookeeperServerMonitor extends AbstractProcessMonitor implements ApplicationRunner, Ordered {
	@Value("${xworx.home}")
	private String xworxHome;
	@Value("${com.xworx.Zookeeper.enabled}")
	private boolean enabledZookeeper = false;
	@Value("${com.xworx.Zookeeper.embedded}")
	private boolean embeddedZookeeper = false;
	@Value("${zookeeper.connectString}")
	private String connectString;
	@Value("${com.xworx.Zookeeper.launcher.command}")
	private String zkLauncherCommand;
	@Value("${com.xworx.Zookeeper.timeout:15000}")
	private int startTimeout = 15000;
	private ZooKeeper zkClient = null;

	public void run(ApplicationArguments args) throws Exception {
		if (enabledZookeeper) {
			if (embeddedZookeeper) {
				logger.info("Zookeeper embedded Start.");

				String zkConfig = xworxHome + SEP + "codebase" + SEP + "config" + SEP + "application-zookeeper.properties";

				ZookeeperApplicationRunner zkRunner = new ZookeeperApplicationRunner();
				logger.info("Zookeeper config:" + zkConfig);
				XProperties properties = XProperties.load(new File(zkConfig));
				String dataDir = properties.getProperty("dataDir");
				String serverId = properties.getProperty("serverId");
				logger.info("Zookeeper dataDir:" + dataDir + "  serverId:" + serverId);
				/**
				 * ZooKeeper集群环境下，根据zookeeper.serverId的设置，自动在dataDir目录创建myId文件
				 */
				this.genMyId(dataDir + SEP + "myid", serverId);
				/**
				 * application-zookeeper.properties包含有占位key, 需要将占位key解析成新properties文件
				 */
				File newConfig = properties.cloneProperties(BasicConfiguration.getXWHome() + File.separator + XConstants.XWORX_TEMP);
				zkRunner.setZkConfig(newConfig.getAbsolutePath());

				XWorxThreadRunnable worxThreadRunnable = XWorxThreadRunnable.newInstance(zkRunner);
				worxThreadRunnable.start(args);
			} else {
				logger.info(zkLauncherCommand);
				XWorxProcessRunable processRunable = XWorxProcessRunable.newInstance(zkLauncherCommand, XProcessType.Zookeeper, 0);
				processRunable.start();
				this.addMonitorProcess("com.xworx.Zookeeper.launcher.command", processRunable);
				Thread.sleep(startTimeout);
			}
		}

		final CountDownLatch connectedSignal = new CountDownLatch(1);
		this.zkClient = new ZooKeeper(connectString, 20000, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if (KeeperState.SyncConnected == event.getState() && EventType.None == event.getType()) {
					connectedSignal.countDown();
				} else {
					logger.info("----------:" + event);
				}
			}
		});
		connectedSignal.await();
		logger.info(this.zkClient);
	}

	private void genMyId(String path, String content) throws IOException {
		File myId = new File(path);
		if (myId.exists())
			myId.delete();

		if (!myId.getParentFile().exists())
			myId.getParentFile().mkdirs();

		myId.createNewFile();
		try (FileWriter writer = new FileWriter(myId); BufferedWriter out = new BufferedWriter(writer)) {
			out.write(content);
			out.flush(); // 把缓存区内容压入文件
		}
	}

	/**
	 * Zookeeper、Ignite、Method服务进程要依次启动，@Order好像不生效，但通过实现Ordered接口后就能生效
	 */
	@Override
	public int getOrder() {
		Order order = ZookeeperServerMonitor.class.getAnnotation(Order.class);
		if (order == null)
			return Integer.MAX_VALUE;
		else
			return order.value();
	}
}
