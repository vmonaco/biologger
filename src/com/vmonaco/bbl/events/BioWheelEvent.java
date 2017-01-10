package com.vmonaco.bbl.events;

import com.vmonaco.bbl.Utility;

public class BioWheelEvent implements BioEvent {

	public static final String event_type = "mousewheel";

	public static final String[] HEADER = { "time", "rotation", "amount", "type", "x", "y", "modifier_code",
			"modifier_name" };

	public long time;
	public int rotation;
	public int amount;
	public int type; // 0 for unit, 1 for block
	public int x;
	public int y;
	public int modifier_code;
	public String modifier_name;

	@Override
	public String[] header() {
		return HEADER;
	}

	@Override
	public String[] values() {
		return new String[] { "" + time, "" + rotation, "" + amount, "" + type, "" + x, "" + y, "" + modifier_code,
				modifier_name };
	}

	@Override
	public String toString() {
		return Utility.csvString(this.values());
	}
}
