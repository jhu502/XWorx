package com.xworx.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

import com.flame.util.XConstants;
import com.flame.util.XException;
import com.flame.util.XProperties;

public class XWorxBootstrapStartup {
	protected static void readProcessOutput(InputStream inputStream, PrintStream out) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String xworxHome = System.getProperty(XConstants.XWORX_HOME);
		if (XConstants.isBlank(xworxHome)) {
			xworxHome = System.getenv("XWORX_HOME");
			if (XConstants.isBlank(xworxHome)) {
				throw new XException("请正确设置:" + XConstants.XWORX_HOME);
			} else {
				System.setProperty(XConstants.XWORX_HOME, xworxHome);
			}
		}
		XProperties properties = XProperties.load("config", "application-manager.properties");
		String launcherCommand = properties.getProperty("com.xworx.ServerManager.launcher.command");
		System.out.println(launcherCommand);
		Runtime.getRuntime().exec(launcherCommand);
		/**
		 * readProcessOutput(runningProcess.getInputStream(), System.out);
		 */
	}

}
