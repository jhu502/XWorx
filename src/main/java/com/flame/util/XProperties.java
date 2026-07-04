package com.flame.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flame.config.basic.BasicConfiguration;

public class XProperties extends Properties {
	private static final long serialVersionUID = 1L;
	private static final Pattern DYNAMIC = Pattern.compile("\\$\\{([0-9a-zA-z.]+)\\}");
	private static final String CODEBASE = "codebase";
	private File sourceFile;

	public XProperties(File file) {
		this.sourceFile = file;
	}

	public static XProperties load(String path, String fileName) throws IOException {
		String codebase = BasicConfiguration.getXWHome() + File.separator + CODEBASE;
		File source = new File(codebase + File.separator + path + File.separator + fileName);

		XProperties properties = XProperties.load(source);

		return properties;
	}

	public static XProperties load(File file) throws IOException {
		XProperties properties = new XProperties(file);
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			properties.load(bufferedReader);
			properties.loadInclude().loadImport();
		}
		return properties;
	}

    private XProperties loadInclude() throws IOException {
        String includeProfiles = super.getProperty("spring.profiles.include");
        if (includeProfiles == null)
            return this;

        for (String key : includeProfiles.split(",")) {
            try (InputStream stream = new FileInputStream(new File(this.sourceFile.getParent() + File.separator + "application-" + key + ".properties"));) {
                this.load(stream);
            }
        }

        return this;
    }

    private XProperties loadImport() throws IOException {
        String importConfigs = super.getProperty("spring.config.import");
        if (importConfigs == null)
            return this;

        for (String key : importConfigs.split(",")) {
            try (InputStream stream = new FileInputStream(new File(this.sourceFile.getParent() + File.separator + key));) {
                this.load(stream);
            }
        }
        return this;
    }

    /**
     * 在获取值时动态替换值中的占位符号, 处理过程如下：
     *  - 针对键值对：com.xworx.Zookeeper.java.agent=-javaagent:${xworx.home}/codebase/xagent.jar;
     *  - 调用getPropertyValue("com.xworx.Zookeeper.java.agent");返回值之前替换${xworx.home};
     *  - 将进行过占位符替换的值回写到XProperties,下次取值就不需要进行占位符替换;
     * @param key
     * @return
     */
    @Override
	public String getProperty(String key) {
		String value = (String) this.get(key);

		int icount = 0;
		boolean bool = true;
		while (bool) {
			bool = false;
			Matcher matcher = DYNAMIC.matcher(value);
			while (matcher.find()) {
				String express = matcher.group(0);
				if (express != null && express.length() > 3) {
					String elKey = this.parseElKey(express);
					String elVal = super.getProperty(elKey);
					if (elVal == null) {
						elVal = BasicConfiguration.getProperty(elKey);
					}
                    if (elVal == null) {
                        elVal = System.getProperty(elKey);
                    }
					if (elVal != null) {
						value = value.replace(express, elVal);
						bool = true;
					}
				}
			}
			if (icount++ > 200) {
                throw new XException("Key:" + key + " have nested expression.");
            }
		}

        if (icount > 0) {
            this.put(key, value);
        }

		return value;
	}

	/**
	 * 将XProperties加载的properties文件, 解析占位关键字(e.g.: ${xworx.home})后, 将新文件写入targetDir参数目录的同名文件中
	 * @param targetDir
	 * @return
	 * @throws IOException
	 */
    public File cloneProperties(String targetDir) throws IOException {
        if (FlameUtils.isBlank(targetDir)) {
            return null;
        }
        File targetFile = new File(targetDir + File.separator + this.sourceFile.getName());
        File parent = targetFile.getParentFile();
        if (!parent.exists() || !parent.isDirectory()) {
            parent.mkdirs();
        }

        Properties properties = new Properties();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.sourceFile))) {
            properties.load(bufferedReader);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            SortedMap<String, String> sortedMap = new TreeMap<>();
            for (Object object : this.keySet()) {
                if (object == null)
                    continue;
                if (object instanceof String) {
                    String key = (String) object;
                    if (FlameUtils.isBlank(key))
                        continue;

                    sortedMap.put(key, this.getProperty(key));
                }
            }
            for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                if (properties.containsKey(entry.getKey())) {
                    writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
                }
            }
        }

        return targetFile;
    }

	private String parseElKey(String express) {
		if (FlameUtils.isBlank(express))
			return express;

		if (express.startsWith("${") && express.endsWith("}")) {
			return express.substring(2, express.length() - 1);
		}
		return express;
	}

}
