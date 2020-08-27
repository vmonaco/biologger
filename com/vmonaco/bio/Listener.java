package com.vmonaco.bio;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.time.Instant;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import com.vmonaco.bio.events.BioClickEvent;
import com.vmonaco.bio.events.BioKeystrokeEvent;
import com.vmonaco.bio.events.BioMotionEvent;
import com.vmonaco.bio.events.BioMotionTrackEvent;
import com.vmonaco.bio.events.BioWheelEvent;

public class Listener implements NativeKeyListener, NativeMouseWheelListener, NativeMouseInputListener {
	private static final int CAPTURE_DELTA_X = 100;
	private static final int CAPTURE_DELTA_Y = 100;

	private Buffer mBuffer;

	private Map<Integer, BioKeystrokeEvent> mActiveKeys;
	private Map<Integer, BioClickEvent> mActiveButtons;
	private BioMotionTrackEvent mActiveMotionTrack;
	private long mMotionThreshold;
	private int mPrevX;
	private int mPrevY;

	public Listener(Buffer buffer, long motionThreshold) {
		mBuffer = buffer;
		mActiveKeys = new HashMap<Integer, BioKeystrokeEvent>();
		mActiveButtons = new HashMap<Integer, BioClickEvent>();
		mActiveMotionTrack = null;
		mMotionThreshold = motionThreshold;
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent event) {
		long now = System.currentTimeMillis();

		BioWheelEvent bioEvent = new BioWheelEvent();
		bioEvent.time = now;
		bioEvent.rotation = event.getWheelRotation();
		bioEvent.amount = event.getScrollAmount();
		bioEvent.type = event.getScrollType();
		bioEvent.x = event.getX();
		bioEvent.y = event.getY();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
				event.getModifiers()).toLowerCase().replace(' ','_');

		mBuffer.addEvent(bioEvent);
		BioLogger.LOGGER.log(Level.INFO, "mousewheel," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent event) {
		long now = System.currentTimeMillis();
		this.mouseMoved(event, now, 1);
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent event) {
		long now = System.currentTimeMillis();
		this.mouseMoved(event, now, 0);
	}

	private void mouseMoved(NativeMouseEvent event, long now, int dragged) {
		// Create motion event
		BioMotionEvent bioEvent = new BioMotionEvent();
		bioEvent.time = now;
		bioEvent.x = event.getX();
		bioEvent.y = event.getY();
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeMouseEvent.getModifiersText(
		event.getModifiers()).toLowerCase().replace(' ','_');
		bioEvent.dragged = dragged;

		mBuffer.addEvent(bioEvent);
		// BioLogger.LOGGER.log(Level.INFO, "mousemove," + Utility.csvString(bioEvent.values()));

		// Create motion track event
		if (mActiveMotionTrack == null) {
			mActiveMotionTrack = new BioMotionTrackEvent();
			mActiveMotionTrack.start_time = now;
			mActiveMotionTrack.end_time = now;
			mActiveMotionTrack.start_x = event.getX();
			mActiveMotionTrack.start_y = event.getY();
			mActiveMotionTrack.end_x = event.getX();
			mActiveMotionTrack.end_y = event.getY();
			mActiveMotionTrack.distance_x = 0;
			mActiveMotionTrack.distance_y = 0;
			mActiveMotionTrack.dragged = dragged;
		} else {
			if ((now - mActiveMotionTrack.end_time) < mMotionThreshold) {
				mActiveMotionTrack.end_time = now;
				mActiveMotionTrack.distance_x += Math.abs(mActiveMotionTrack.end_x - event.getX());
				mActiveMotionTrack.distance_y += Math.abs(mActiveMotionTrack.end_y - event.getY());
				mActiveMotionTrack.end_x = event.getX();
				mActiveMotionTrack.end_y = event.getY();
			} else {
				mBuffer.addEvent(mActiveMotionTrack);
				BioLogger.LOGGER.log(Level.INFO, "mousetrack," + Utility.csvString(mActiveMotionTrack.values()));

				mActiveMotionTrack = new BioMotionTrackEvent();
				mActiveMotionTrack.start_time = now;
				mActiveMotionTrack.end_time = now;
				mActiveMotionTrack.start_x = mPrevX;
				mActiveMotionTrack.start_y = mPrevY;
				mActiveMotionTrack.end_x = event.getX();
				mActiveMotionTrack.end_y = event.getY();
				mActiveMotionTrack.distance_x = Math.abs(event.getX() - mPrevX);
				mActiveMotionTrack.distance_y = Math.abs(event.getY() - mPrevY);
				mActiveMotionTrack.dragged = dragged;
			}
		}

		mPrevX = event.getX();
		mPrevY = event.getY();
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent event) {
		long now = System.currentTimeMillis();

		if (mActiveButtons.containsKey(event.getButton())) {
			return;
		}

		BioClickEvent bioEvent = new BioClickEvent();
		bioEvent.press_time = now;
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
		long now = System.currentTimeMillis();

		BioClickEvent bioEvent = mActiveButtons.remove(event.getButton());
		bioEvent.release_time = now;
		bioEvent.release_x = event.getX();
		bioEvent.release_y = event.getY();

		mBuffer.addEvent(bioEvent);
		BioLogger.LOGGER.log(Level.INFO, "mouseup," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		long now = System.currentTimeMillis();

		if (mActiveKeys.containsKey(event.getRawCode())) {
			return;
		}

		BioKeystrokeEvent bioEvent = new BioKeystrokeEvent();
		bioEvent.press_time = now;
		bioEvent.key_code = event.getKeyCode();
		bioEvent.key_string = NativeKeyEvent.getKeyText(event.getKeyCode()).toLowerCase().replace(' ', '_');
		bioEvent.modifier_code = event.getModifiers();
		bioEvent.modifier_name = NativeKeyEvent.getModifiersText(event.getModifiers()).toLowerCase().replace(' ', '_');
		bioEvent.key_location = event.getKeyLocation();

		mActiveKeys.put(event.getRawCode(), bioEvent);
		BioLogger.LOGGER.log(Level.INFO, "keydown," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		long now = System.currentTimeMillis();

		BioKeystrokeEvent bioEvent = mActiveKeys.remove(event.getRawCode());
		bioEvent.release_time = now;
		mBuffer.addEvent(bioEvent);

		BioLogger.LOGGER.log(Level.INFO, "keyup," + Utility.csvString(bioEvent.values()));
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		// Empty
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// Empty
	}

	public void stop() {
		if (mActiveMotionTrack == null) {
			mBuffer.addEvent(mActiveMotionTrack);
		}
	}
}
