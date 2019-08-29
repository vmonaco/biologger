package com.vmonaco.bio.events;

import com.vmonaco.bio.Utility;

public class BioMotionEvent implements BioEvent {

	public static final String event_type = "mousemotion";

	public static final String[] HEADER = { "time", "x", "y", "modifier_code", "modifier_name", "dragged" };

	public long time;
	public int x;
	public int y;
	public int modifier_code;
	public String modifier_name;
	public int dragged;

	@Override
	public String[] header() {
		return HEADER;
	}

	@Override
	public String[] values() {
		return new String[] { "" + time, "" + x, "" + y, "" + modifier_code, modifier_name, "" + dragged };
	}

	@Override
	public String toString() {
		return Utility.csvString(this.values());
	}
}
