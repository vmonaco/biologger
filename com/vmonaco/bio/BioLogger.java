package com.vmonaco.bio;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import com.vmonaco.bio.events.BioClickEvent;
import com.vmonaco.bio.events.BioEvent;
import com.vmonaco.bio.events.BioKeystrokeEvent;
import com.vmonaco.bio.events.BioMotionEvent;
import com.vmonaco.bio.events.BioWheelEvent;

public class BioLogger {
	public static final Logger LOGGER = Logger.getLogger("com.vmonaco.bbl");

	private ConsumerCSV mFileCSV;
	private Buffer mBuffer;
	private Listener mListener;
	private GUI mGui;
	private String mOutDir;
	private Map<Class<? extends BioEvent>, String> mClassMap;

	public BioLogger(String outDir, boolean showWindow, Map<Class<? extends BioEvent>, String> classMap) {
		mOutDir = outDir;
		mClassMap = classMap;

		mFileCSV = new ConsumerCSV(classMap, outDir);
		mBuffer = new Buffer(mFileCSV);
		mListener = new Listener(mBuffer);

		if (showWindow) {
			mGui = new GUI(this, outDir);
		}
	}

	public void startLogging() {

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			BioLogger.LOGGER.severe("There was a problem registering the native hook.");
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				BioLogger.LOGGER.severe("Make sure that Assistive Devices is enabled.");
				BioLogger.LOGGER.severe(
						"Go to System Preferences->Security & Privacy->Accessibility");
			}
			e.printStackTrace();
			return;
		}

		if (mClassMap.containsKey(BioKeystrokeEvent.class)) {
			GlobalScreen.addNativeKeyListener(mListener);
		}

		if (mClassMap.containsKey(BioClickEvent.class)) {
			GlobalScreen.addNativeMouseListener(mListener);
		}

		if (mClassMap.containsKey(BioMotionEvent.class)) {
			GlobalScreen.addNativeMouseMotionListener(mListener);
		}

		if (mClassMap.containsKey(BioWheelEvent.class)) {
			GlobalScreen.addNativeMouseWheelListener(mListener);
		}
	}

	public void stopLogging() {
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		mBuffer.stop();
	}

	public void close() {
		System.runFinalization();
		System.exit(0);
	}

	public static void printKeyMap() {
		System.out.println("key_code,key_name");
		for (int i = 0; i < 65536; i++) {
			String keyName = NativeKeyEvent.getKeyText(i).toLowerCase().replace(' ', '_');
			if (!keyName.contains("unknown")) {
				System.out.println("" + i + "," + keyName);
			}
		}
	}

	public static void main(String[] args) {
		// Disable jnativehook logging
		LogManager.getLogManager().reset();
		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

		Options options = new Options();
		options.addOption("o", "output", true, "output directory");
		options.addOption("nw", "no-window", false, "don't start the user interface");
		options.addOption("ik", "ignore-keystroke", false, "ignore keystroke events");
		options.addOption("im", "ignore-motion", false, "ignore ignore mouse motion events");
		options.addOption("ic", "ignore-click", false, "ignore mouse click events");
		options.addOption("iw", "ignore-wheel", false, "ignore mouse wheel events");
		options.addOption("pk", "print-keys", false, "print the full key map and exit");
		options.addOption("v", "verbose", false, "verbose mode");
		options.addOption("h", "help", false, "print this help message");

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("biologger", options);

			System.exit(1);
			return;
		}

		if (cmd.hasOption("help")) {
			formatter.printHelp("biologger", options);
			System.exit(0);
		}

		if (cmd.hasOption("print-keys")) {
			printKeyMap();
			System.exit(0);
		}

		String outDir = System.getProperty("user.dir");
		if (cmd.hasOption("output")) {
			outDir = cmd.getOptionValue("output");
		}

		boolean showWindow = !cmd.hasOption("no-window");

		Map<Class<? extends BioEvent>, String> classMap = new HashMap<Class<? extends BioEvent>, String>();
		if (!cmd.hasOption("ik")) {
			classMap.put(BioKeystrokeEvent.class, "keystroke.csv");
		}

		if (!cmd.hasOption("im")) {
			classMap.put(BioMotionEvent.class, "mousemotion.csv");
		}

		if (!cmd.hasOption("ic")) {
			classMap.put(BioClickEvent.class, "mouseclick.csv");
		}

		if (!cmd.hasOption("iw")) {
			classMap.put(BioWheelEvent.class, "mousewheel.csv");
		}

		if (classMap.size() == 0) {
			System.out.println("Cannot ignore all event types.");
			System.exit(1);
		}

		if (cmd.hasOption("v")) {
			LOGGER.setLevel(Level.ALL);
			ConsoleHandler handler = new ConsoleHandler();
			handler.setLevel(Level.ALL);
			handler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(handler);
		}

		BioLogger app = new BioLogger(outDir, showWindow, classMap);
		app.startLogging();
	}
}
