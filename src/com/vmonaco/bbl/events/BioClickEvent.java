package com.vmonaco.bbl.events;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

import com.vmonaco.bbl.BioLogger;
import com.vmonaco.bbl.Utility;

public class BioClickEvent implements BioEvent {

	public static final String event_type = "mouseclick";

	public static final String[] HEADER = { "press_time", "release_time", "button_code", "press_x", "press_y",
			"release_x", "release_y", "modifier_code", "modifier_name", "image" };

	public long press_time;
	public long release_time;
	public int button_code;
	public int press_x;
	public int press_y;
	public int release_x;
	public int release_y;
	public int modifier_code;
	public String modifier_name;
	public BufferedImage image;

	@Override
	public String[] header() {
		return HEADER;
	}

	@Override
	public String[] values() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			BioLogger.LOGGER.severe(Utility.createCrashReport(e));
		}
		return new String[] { "" + press_time, "" + release_time, "" + button_code, "" + press_x, "" + press_y,
				"" + release_x, "" + release_y, "" + modifier_code, modifier_name,
				new String(Base64.getEncoder().encode(out.toByteArray())) };
	}

	@Override
	public String toString() {
		return Utility.csvString(this.values());
	}
}
