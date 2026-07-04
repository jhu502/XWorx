package com.flame.logs;

import java.io.InputStream;

public final class FastInputStream extends InputStream {
	private byte[] buf = null;
	private int count = 0;
	private int pos = 0;

	public FastInputStream(byte[] buf) {
		this.buf = buf;
		this.count = buf.length;
	}

	public FastInputStream(byte[] buf, int count) {
		this.buf = buf;
		this.count = count;
	}

	public final int count() {
		return this.count;
	}

	public final int position() {
		return this.pos;
	}

	@Override
	public final int available() {
		return this.count - this.pos;
	}

	@Override
	public final int read() {
		int n = this.pos < this.count ? this.buf[this.pos++] & 255 : -1;
		return n;
	}

	@Override
	public final int read(byte[] b, int off, int len) {
		if (this.pos >= this.count) {
			return -1;
		}
		if (this.pos + len > this.count) {
			len = this.count - this.pos;
		}
		System.arraycopy(this.buf, this.pos, b, off, len);
		this.pos += len;
		return len;
	}

	public final long rewind(int n) {
		this.pos = this.pos - n > 0 ? (this.pos -= n) : 0;
		return this.pos;
	}

	@Override
	public final long skip(long n) {
		if ((long) this.pos + n > (long) this.count) {
			n = this.count - this.pos;
		}
		if (n < 0L) {
			return 0L;
		}
		this.pos = (int) ((long) this.pos + n);
		return n;
	}
}