package com.xworx.config;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.flame.util.XConstants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.flame.util.XException;

public class XWorxThreadRunnable implements Runnable {
    private ApplicationRunner appRunner;
    private ApplicationArguments appArguments;

    public static XWorxThreadRunnable newInstance(ApplicationRunner runner) {
        XWorxThreadRunnable threadRunnable = new XWorxThreadRunnable();
        threadRunnable.appRunner = runner;

        return threadRunnable;
    }

    @Override
    public void run() {
        try {
            this.appRunner.run(this.appArguments);
        } catch (Exception e) {
            throw new XException(e);
        }
    }

    public void start(ApplicationArguments args) {
        start(args, false);
    }

    public void start(ApplicationArguments args, boolean isolatedClassLoader) {
        this.appArguments = args;
        Thread thread = new Thread(this);
        if (isolatedClassLoader) {
            thread.setContextClassLoader(ZkURLClassLoader.createClassLoader());
        }
        thread.start();
    }

    /**
     * 子优先 ClassLoader：先从自己的 URL 加载类，找不到再委托给父 ClassLoader。
     * 解决 ZooKeeper 内嵌 Jetty 9 与主 classpath 中 Jetty 12 的版本冲突。
     */
    private static class ZkURLClassLoader extends URLClassLoader {
        ZkURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        private static ClassLoader createClassLoader() {
            String xworxHome = System.getProperty(XConstants.XWORX_HOME);
            File zkLibDir = new File(xworxHome, "zookeeper" + File.separator + "lib");
            List<URL> urls = new ArrayList<>();
            if (zkLibDir.exists() && zkLibDir.isDirectory()) {
                File[] jars = zkLibDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
                if (jars != null) {
                    for (File jar : jars) {
                        try {
                            urls.add(jar.toURI().toURL());
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return new ZkURLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            // 排除 java.* 等系统类，必须由父 ClassLoader 加载
            if (name.startsWith("java.")) {
                return super.loadClass(name, resolve);
            }
            // 先从自身 URL 查找
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) {
                return clazz;
            }
            try {
                clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException e) {
                // 找不到再委托父 ClassLoader
                return super.loadClass(name, resolve);
            }
        }
    }
}
