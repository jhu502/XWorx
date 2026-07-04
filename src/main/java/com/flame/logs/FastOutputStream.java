package com.flame.logs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class FastOutputStream extends OutputStream {
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	public static final int BUFFER_GROWTH_SIZE = 4096;
	private byte[] buf = null;
	private int size = 0;

	public FastOutputStream() {
		this(4096);
	}

	public FastOutputStream(int initSize) {
		this.buf = new byte[initSize];
	}

	private void verifyBufferSize(int sz) {
		if (sz > this.buf.length) {
			byte[] old = this.buf;
			this.buf = new byte[sz + 4096];
			System.arraycopy(old, 0, this.buf, 0, old.length);
			old = null;
		}
	}

	public int getSize() {
		return this.size;
	}

	public int getBufferSize() {
		return this.buf.length;
	}

	public byte[] getByteArray() {
		return this.buf;
	}

	public byte[] toByteArray() {
		return Arrays.copyOf(this.buf, this.size);
	}

	@Override
	public final void write(byte[] b) {
		this.verifyBufferSize(this.size + b.length);
		System.arraycopy(b, 0, this.buf, this.size, b.length);
		this.size += b.length;
	}

	@Override
	public final void write(byte[] b, int off, int len) {
		this.verifyBufferSize(this.size + len);
		System.arraycopy(b, off, this.buf, this.size, len);
		this.size += len;
	}

	@Override
	public final void write(int b) {
		this.verifyBufferSize(this.size + 1);
		this.buf[this.size++] = (byte) b;
	}

	public void reset() {
		this.size = 0;
	}

	public InputStream getInputStream() {
		return new FastInputStream(this.buf, this.size);
	}
}
