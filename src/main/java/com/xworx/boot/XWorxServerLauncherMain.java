package com.xworx.boot;

import java.io.IOException;

import com.flame.util.XException;

public class XWorxServerLauncherMain {
	private static Process runningProcess;

	public static void main(String[] args) {
		try {
			runningProcess = Runtime.getRuntime().exec("");
			
			Thread.sleep(6000);
		} catch (IOException e) {
			throw new XException(e);
		} catch (InterruptedException e) {
			throw new XException(e);
		}
	}
}
