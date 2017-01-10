package com.vmonaco.bbl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.vmonaco.bbl.events.BioEvent;

public class ConsumerCSV implements BioEventConsumer {

	Map<Class<? extends BioEvent>, PrintWriter> mPrintWriters = new HashMap<Class<? extends BioEvent>, PrintWriter>();

	public ConsumerCSV(Map<Class<? extends BioEvent>, String> classMap, String outDir) {
		for (Class<? extends BioEvent> c : classMap.keySet()) {
			try {
				File file = new File(outDir, classMap.get(c));
				boolean writeHeader = !file.exists();
				PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
				if (writeHeader) {
					pw.write(Utility.csvString(c.newInstance().header()) + "\n");
					pw.flush();
				}
				mPrintWriters.put(c, pw);
			} catch (InstantiationException | IllegalAccessException | FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Override
	public void onEventsReceived(Buffer buffer, List<? extends BioEvent> eventList) {
		for (BioEvent event : eventList) {
			mPrintWriters.get(event.getClass()).write(Utility.csvString(event.values()) + "\n");
		}

		for (PrintWriter pw : mPrintWriters.values()) {
			pw.flush();
		}
	}

	@Override
	public void onSessionEnd(Buffer buffer) {
		for (PrintWriter pw : this.mPrintWriters.values()) {
			pw.close();
		}
	}

}