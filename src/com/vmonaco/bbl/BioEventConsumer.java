package com.vmonaco.bbl;

import java.util.List;

import com.vmonaco.bbl.events.BioEvent;

public interface BioEventConsumer {
	void onEventsReceived(Buffer buffer, List<? extends BioEvent> events);
	void onSessionEnd(Buffer buffer);
}