package com.xworx.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.flame.util.XException;

public class XWorxProcessRunable implements Runnable {
	private Log logger = LogFactory.getLog(XWorxProcessRunable.class);
	private XProcessType processType;
	private String launcherCommond;
	private Process runningProcess;
	private Thread monitorThread;
	private int serial = 0;

	public static XWorxProcessRunable newInstance(String command, XProcessType processType, int serial) {
		XWorxProcessRunable flameProcess = new XWorxProcessRunable();
		flameProcess.launcherCommond = command;
		flameProcess.processType = processType;
		flameProcess.serial = serial;

		return flameProcess;
	}

	protected void readProcessOutput(InputStream inputStream, PrintStream out) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

	public void start() {
		if (this.monitorThread == null) {
			this.monitorThread = new Thread(this);
			this.monitorThread.start();
		}
	}

	public String getLauncherCommond() {
		return this.launcherCommond;
	}

	public Process getRunningProcess() {
		return this.runningProcess;
	}

	public XProcessType getProcessType() {
		return this.processType;
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.info(processType.name() + " Server:" + this.serial + " starting.");

				runningProcess = Runtime.getRuntime().exec(launcherCommond);
				readProcessOutput(runningProcess.getInputStream(), System.out);

				logger.error(processType.name() + " Server:" + this.serial + " abnormal exit.");

				Thread.sleep(6000);
			} catch (IOException e) {
				throw new XException(e);
			} catch (InterruptedException e) {
				throw new XException(e);
			}
		}
	}
}
