package com.vmonaco.bbl.events;

import com.vmonaco.bbl.Utility;

public class BioKeystrokeEvent implements BioEvent {

	public static final String event_type = "keystroke";
	public static final String[] HEADER = { "press_time", "release_time", "key_code", "key_name", "modifier_code",
			"modifier_name", "location" };

	public long press_time;
	public long release_time;
	public int key_code;
	public String key_string;
	public int modifier_code;
	public String modifier_name;
	public int key_location;

	@Override
	public String[] header() {
		return HEADER;
	}

	@Override
	public String[] values() {
		return new String[] { "" + press_time, "" + release_time, "" + key_code, key_string, "" + modifier_code,
				modifier_name, "" + key_location };
	}

	@Override
	public String toString() {
		return Utility.csvString(this.values());
	}
}
