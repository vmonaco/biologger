package com.vmonaco.bio;

import java.util.List;

import com.vmonaco.bio.events.BioEvent;

public interface Consumer {
	void onEventsReceived(Buffer buffer, List<? extends BioEvent> events);

	void onSessionEnd(Buffer buffer);
}