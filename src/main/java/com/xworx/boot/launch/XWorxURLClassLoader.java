package com.xworx.boot.launch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.net.protocol.jar.JarUrlClassLoader;

/**
 * XWorxURLClassLoader本来是用来去加载xworx.home/lib下的jar包，但是在执行
 *
 * @author hujin
 */
public class XWorxURLClassLoader extends JarUrlClassLoader {
    private final Path codebasePath;

    /**
     * Create a new {@link XWorxURLClassLoader} instance.
     *
     * @param rootArchive the root archive or {@code null}
     * @param jarUrls     the URLs from which to load classes and resources
     * @param parent      the parent class loader for delegation
     * @since 2.3.1
     */
    public XWorxURLClassLoader(Archive rootArchive, URL[] jarUrls, ClassLoader parent) {
        super(jarUrls, parent);
        this.codebasePath = rootArchive.getRootDirectory().toPath().resolve("codebase");
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        /**
         * 由于jdk17的安全机制，不允许自定义的ClassLoader去加载下面开头的package
         */
        if (className.startsWith("java.") || className.startsWith("jdk.internal.reflect.")) {
            return this.getParent().loadClass(className);
        }
        /**
         * 首先从$WT_HOME/codebase目录下去加载对应的class
         */
        try {
            Path classFilePath = codebasePath.resolve(className.replace('.', '/') + ".class");
            byte[] classBytes = Files.readAllBytes(classFilePath);
            Class<?> definedClass = defineClass(className, classBytes, 0, classBytes.length);
            definePackageIfNecessary(className);
            return definedClass;
        } catch (Exception e) {
            /**
             * Ignore:
             * 1. 如果className在codebase目录下不存在就会抛出IOException异常，则需要从lib/*.jar去加载
             * 2. jackson会将日期格式作为一个类来加载，导致出现InvalidPathException，而jackson则是捕获ClassNotFoundException异常后再进行时间格式转换
             */
        }
        /**
         * 然后使用JarUrlClassLoader从$WT_HOME/lib的jar上加载
         */
        return super.loadClass(className, resolve);
    }

    private Class<?> loadClassInLaunchedClassLoader(String className) throws ClassNotFoundException {
        try {
            String internalName = className.replace('.', '/') + ".class";
            try (InputStream inputStream = getParent().getResourceAsStream(internalName); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (inputStream == null) {
                    throw new ClassNotFoundException(className);
                }
                inputStream.transferTo(outputStream);
                byte[] bytes = outputStream.toByteArray();
                Class<?> definedClass = defineClass(className, bytes, 0, bytes.length);
                definePackageIfNecessary(className);
                return definedClass;
            }
        } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + className + "]", ex);
        }
    }
}