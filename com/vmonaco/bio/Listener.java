package com.vmonaco.bio;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import com.vmonaco.bio.events.BioClickEvent;
import com.vmonaco.bio.events.BioKeystrokeEvent;
import com.vmonaco.bio.events.BioMotionEvent;
import com.vmonaco.bio.events.BioWheelEvent;

public class Listener implements NativeKeyListener, NativeMouseWheelListener, NativeMouseInputListener {
	private static final int CAPTURE_DELTA_X = 100;
	private static final int CAPTURE_DELTA_Y = 100;

	private Buffer mBuffer;

	private Map<Integer, BioKeystrokeEvent> mActiveKeys;
	private Map<Integer, BioClickEvent> mActiveButtons;

	public Listener(Buffer buffer) {
		mBuffer = buffer;
		mActiveKeys = new HashMap<Integer, BioKeystrokeEvent>();
		mActiveButtons = new HashMap<Integer, BioClickEvent>();
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent event) {
		BioWheelEvent bioEvent = new BioWheelEvent();

		bioEvent.time = event.getWhen();
		bioEvent.rotation = event.getWheelRotation();
		bioEvent.amount = event.getScrollAmount();
		bioEvent.type = event.getScrollType();
		bioEvent.x = event.getX();
		bioEvent.y = event.getY();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
				event.getModifiers()).toLowerCase().replace(' ','_');

		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, event.paramString()); //"mousewheel," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent event) {
		BioMotionEvent bioEvent = new BioMotionEvent();

		bioEvent.time = event.getWhen();
		bioEvent.x = event.getX();
		bioEvent.y = event.getY();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
				event.getModifiers()).toLowerCase().replace(' ','_');
		bioEvent.dragged = 1;

		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "mousedrag," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent event) {
		BioMotionEvent bioEvent = new BioMotionEvent();

		bioEvent.time = event.getWhen();
		bioEvent.x = event.getX();
		bioEvent.y = event.getY();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
				event.getModifiers()).toLowerCase().replace(' ','_');
		bioEvent.dragged = 0;

		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "mousemove," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent event) {
		if (mActiveButtons.containsKey(event.getButton())) {
			return;
		}

		BioClickEvent bioEvent = new BioClickEvent();

		bioEvent.press_time = event.getWhen();
		bioEvent.button_code = event.getButton();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
				event.getModifiers()).toLowerCase().replace(' ','_');
		bioEvent.press_x = event.getX();
		bioEvent.press_y = event.getY();

		Rectangle capture = new Rectangle(event.getX() - CAPTURE_DELTA_X, event.getY() - CAPTURE_DELTA_Y,
				CAPTURE_DELTA_X * 2, CAPTURE_DELTA_Y * 2);
		bioEvent.image = Utility.screenCapture(capture);

		mActiveButtons.put(event.getButton(), bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "mousedown," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent event) {
		BioClickEvent bioEvent = mActiveButtons.remove(event.getButton());

		bioEvent.release_time = event.getWhen();
		bioEvent.release_x = event.getX();
		bioEvent.release_y = event.getY();

		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "mouseup," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		if (mActiveKeys.containsKey(event.getRawCode())) {
			return;
		}

		BioKeystrokeEvent bioEvent = new BioKeystrokeEvent();

		bioEvent.press_time = event.getWhen();
		bioEvent.key_code = event.getKeyCode();
		bioEvent.key_string = NativeKeyEvent.getKeyText(event.getKeyCode()).toLowerCase().replace(' ', '_');
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeKeyEvent.getModifiersText(event.getModifiers()).toLowerCase().replace(' ', '_');
		bioEvent.key_location = event.getKeyLocation();

		mActiveKeys.put(event.getRawCode(), bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "keydown," + event.getWhen()); //Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		BioKeystrokeEvent bioEvent = mActiveKeys.remove(event.getRawCode());
		bioEvent.release_time = event.getWhen();
		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "keyup," + event.getWhen()); //Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		// Empty
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// Empty
	}
}
