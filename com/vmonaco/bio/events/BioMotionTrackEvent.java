package com.vmonaco.bio.events;

import com.vmonaco.bio.Utility;

public class BioMotionTrackEvent implements BioEvent {

	public static final String event_type = "mousetrack";

	public static final String[] HEADER = { "start_time", "end_time", "start_x", "start_y", "end_x", "end_y", "distance_x", "distance_y", "dragged" };

	public long start_time;
	public int start_x;
	public int start_y;
	public long end_time;
	public int end_x;
	public int end_y;
	public int distance_x;
	public int distance_y;
	public int dragged;

	@Override
	public String[] header() {
		return HEADER;
	}

	@Override
	public String[] values() {
		return new String[] { "" + start_time, "" + end_time, "" + start_x, "" + start_y, "" + end_x, "" + end_y, "" + distance_x, "" + distance_y, "" + dragged };
	}

	@Override
	public String toString() {
		return Utility.csvString(this.values());
	}
}
