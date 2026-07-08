package com.flame.config;

import com.flame.config.basic.BasicConfiguration;
import com.flame.util.XConstants;
import com.flame.util.XProperties;
import com.xworx.config.XWorxThreadRunnable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

/**
 * ZooKeeper 启动初始化器，在 Spring 刷新上下文之前启动 ZooKeeper 并等待连接就绪。
 *
 * <p>通过 {@link ApplicationContextInitializer} 机制在 {@code spring.factories} 中注册，
 * 确保在所有 {@code @Configuration} 类之前执行。ZooKeeper 就绪后，
 * 其他依赖 ZooKeeper 的 Bean（Ignite、Method 等）才能正常初始化。</p>
 *
 * <p>启动顺序：Zookeeper → Ignite → Method</p>
 */
public class ZookeeperServerStart implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Log logger = LogFactory.getLog(ZookeeperServerStart.class);
    private static final String SEP = File.separator;
    private ZooKeeper zkClient = null;

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String connectString = env.getProperty("zookeeper.connectString");
        boolean enabledZookeeper = Boolean.parseBoolean(env.getProperty("com.xworx.Zookeeper.enabled", "false"));
        if (enabledZookeeper) {
            String xworxHome = env.getProperty(XConstants.XWORX_HOME);
            logger.info("Zookeeper embedded Start.");

            String zkConfig = xworxHome + SEP + "codebase" + SEP + "config" + SEP + "application-zookeeper.properties";

            ZookeeperApplicationRunner zkRunner = new ZookeeperApplicationRunner();
            logger.info("Zookeeper config:" + zkConfig);
            try {
                XProperties properties = XProperties.load(new File(zkConfig));
                String dataDir = properties.getProperty("dataDir");
                String serverId = properties.getProperty("serverId");
                logger.info("Zookeeper dataDir:" + dataDir + "  serverId:" + serverId);
                this.genMyId(dataDir + SEP + "myid", serverId);
                File newConfig = properties.cloneProperties(BasicConfiguration.getXWHome() + File.separator + XConstants.XWORX_TEMP);
                zkRunner.setZkConfig(newConfig.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Zookeeper config: " + zkConfig, e);
            }

            XWorxThreadRunnable worxThreadRunnable = XWorxThreadRunnable.newInstance(zkRunner);
            worxThreadRunnable.start(new DefaultApplicationArguments(), true);
            waitForZkPort(connectString, 30000);
        }

        final CountDownLatch connectedSignal = new CountDownLatch(1);
        try {
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
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("ZooKeeper connection failed", e);
        }
    }

    private void genMyId(String path, String content) {
        File myId = new File(path);
        if (myId.exists())
            myId.delete();

        if (!myId.getParentFile().exists())
            myId.getParentFile().mkdirs();

        try {
            myId.createNewFile();
            try (FileWriter writer = new FileWriter(myId); BufferedWriter out = new BufferedWriter(writer)) {
                out.write(content);
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create myId file: " + path, e);
        }
    }

    private void waitForZkPort(String connectString, int timeoutMs) {
        String[] parts = connectString.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts.length > 1 ? parts[1] : "2181");
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 1000);
                logger.info("ZooKeeper port " + port + " is ready");
                return;
            } catch (IOException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        logger.warn("ZooKeeper port " + port + " not ready after " + timeoutMs + "ms");
    }

    public class ZookeeperApplicationRunner implements ApplicationRunner {
        private String zkConfig;

        public void run(ApplicationArguments args) throws Exception {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> mainClass = classLoader.loadClass("org.apache.zookeeper.server.quorum.QuorumPeerMain");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{this.zkConfig});
        }

        public void setZkConfig(String zkConfig) {
            this.zkConfig = zkConfig;
        }
    }
}
