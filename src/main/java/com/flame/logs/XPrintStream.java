package com.flame.logs;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 原本 system.out 是系统 standard output stream, 默认是向控制台输出信息，通过System.setOut方法输出信息到Logger中
 * 
 * @author hujin
 *
 */
public class XPrintStream extends PrintStream {
	protected static final Logger LOGGER = LoggerFactory.getLogger("System.out");

	public XPrintStream(PrintStream out) {
		super(out);
	}

	public void println() {
		LOGGER.info("");
	}

	public void println(boolean x) {
		LOGGER.info(Boolean.toString(x));
	}

	public void println(char x) {
		LOGGER.info(String.valueOf(x));
	}

	public void println(int x) {
		LOGGER.info(Integer.toString(x));
	}

	public void println(long x) {
		LOGGER.info(Long.toString(x));
	}

	public void println(float x) {
		LOGGER.info(Float.toString(x));
	}

	public void println(double x) {
		LOGGER.info(Double.toString(x));
	}

	public void println(char x[]) {
		LOGGER.info(String.valueOf(x));
	}

	public void println(String x) {
		LOGGER.info(x);
	}

	public void println(Object x) {
		LOGGER.info(String.valueOf(x));
	}
}
