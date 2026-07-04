package com.flame.config.system;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flame.config.basic.BasicConfiguration;

import jakarta.annotation.PreDestroy;

/**
 * XMethod服务器启动后, 自动注册自身信息进XManager的ZooKeeper中
 *
 * 解决SessionExpiredException:
 * 1. 增大sessionTimeout至5分钟，防止长时间AI任务阻塞heartbeat导致session超时
 * 2. 使用独立的ExecutorService处理连接状态变化，避免阻塞Curator的EventThread
 * 3. 增强重试策略，提高重试次数
 */
@Configuration
public class XFlameZookeeperConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(XFlameZookeeperConfigurer.class);
	private final ExecutorService stateChangeExecutor = Executors.newSingleThreadExecutor();
	private String namespace;
	@Value("${zookeeper.connectString}")
	private String connectString;
	private int maxRetry = 3;
	private int sessionTimeout = 300000;
	private int connectTimeout = 30000;

	/**
	 * CuratorFramework在curatorFramework方法中初始化后, 自动调用initMethod中定义的start去启动连接
	 * 使用独立线程池处理连接状态变化，防止阻塞Curator的EventThread
	 * @return
	 */
	@Bean(initMethod = "start")
	public CuratorFramework curatorFramework() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, maxRetry);
		CuratorFramework framework = CuratorFrameworkFactory.builder().namespace(this.getNamespace())
				.connectString(this.getConnectString()).sessionTimeoutMs(this.getSessionTimeout())
				.connectionTimeoutMs(this.getConnectTimeout()).retryPolicy(retryPolicy).build();
		framework.getConnectionStateListenable().addListener(new XFlameConnectionListener(framework), stateChangeExecutor);
		return framework;
	}

	public class XFlameConnectionListener implements ConnectionStateListener {
		private final CuratorFramework framework;

		public XFlameConnectionListener(CuratorFramework framework) {
			this.framework = framework;
		}

		public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
			if (connectionState == ConnectionState.LOST) {
				logger.warn("-------------------:监听到zk节点发生LOST事件, session已过期");
			} else if (connectionState == ConnectionState.SUSPENDED) {
				logger.warn("-------------------:监听到zk节点发生SUSPENDED事件, 连接暂时中断");
			} else if (connectionState == ConnectionState.CONNECTED) {
				logger.info("-------------------:监听到zk节点发生CONNECTED事件");
			} else if (connectionState == ConnectionState.RECONNECTED) {
				logger.info("-------------------:监听到zk节点发生RECONNECTED事件, 连接已恢复");
			}
		}

		public String getHostAddress() throws UnknownHostException {
			return Inet4Address.getLocalHost().getHostAddress();
		}
	}

	@PreDestroy
	private void destroyClient() {
		stateChangeExecutor.shutdown();
		CuratorFramework framework = BasicConfiguration.getFramework();
		if (framework != null) {
			framework.close();
			logger.info("Curator:" + framework.toString() + " closed successfully.");
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}
