package com.xworx.zookeeper;

import com.google.common.io.Files;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小，越先加载；
 * 在XWorxManager中，@Order好像不生效，但是实现Ordered接口后就生效了
 */
@Configuration
@Order(200)
public class ZookeeperApplicationRunner implements ApplicationRunner, Ordered {
    private static Pattern PATTERN_KEY = Pattern.compile("^server\\.(\\d)");
    private static Pattern PATTERN_VAL = Pattern.compile("([\\w|.]*):\\d*:\\d*");
    private String zkConfig;

    public void run(ApplicationArguments args) throws Exception {
        org.apache.zookeeper.server.quorum.QuorumPeerMain.main(new String[] { this.zkConfig });
    }

    public void emdebbedZookeeper(ApplicationArguments args) throws Exception {
        InputStream instream = ZookeeperApplicationRunner.class.getResourceAsStream("/my/zookeeperstudy/server/zoo.cfg");
        Properties properties = new Properties();
        try {
            properties.load(instream);
        } finally {
            instream.close();
        }

        for (String key : properties.stringPropertyNames()) {
            Matcher serverKey = PATTERN_KEY.matcher(key);
            if (!serverKey.find())
                continue;

            Matcher serverVal = PATTERN_VAL.matcher(properties.getProperty(key));
            if (!serverVal.find())
                continue;

            String id = serverKey.group(1);
            byte[] bytes = id.getBytes();
            String host = serverVal.group(1);
            String thisHostName = InetAddress.getLocalHost().getHostName();
            String thisHostAddress = InetAddress.getLocalHost().getHostAddress();
            if (host.equals(thisHostName) || host.equals(thisHostAddress)) {
                Files.write(bytes == null ? new byte[0] : bytes, new File(properties.getProperty("dataDir"), "myid"));
                QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
                quorumConfig.parseProperties(properties);

                final ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
                final ServerConfig config = new ServerConfig();
                config.readFrom(quorumConfig);
                zkServer.runFromConfig(config);
            }
        }
    }

    public void setZkConfig(String zkConfig) {
        this.zkConfig = zkConfig;
    }

    @Override
    public int getOrder() {
        Order order = ZookeeperApplicationRunner.class.getAnnotation(Order.class);
        if (order == null)
            return Integer.MAX_VALUE;
        else
            return order.value();
    }
}
