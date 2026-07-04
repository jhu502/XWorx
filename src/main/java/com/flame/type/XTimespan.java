package com.flame.type;

import java.util.Date;

public class XTimespan {
	private Date _start = null;
	private Date _end = null;
	private String _period = "00:00:00:00:01:00";

	public XTimespan() {
	}

	public XTimespan(Date start, Date end) {
		this._start = start;
		this._end = end;
		this._period = "";
	}

	public Date getStart() {
		return _start;
	}

	public void setStart(Date start) {
		this._start = start;
	}

	public Date getEnd() {
		return _end;
	}

	public void setEnd(Date end) {
		this._end = end;
	}

	public String getPeriod() {
		return _period;
	}

	public void setPeriod(String period) {
		this._period = period;
	}

}
