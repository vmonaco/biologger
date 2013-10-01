package com.vmonaco.bbl;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import com.vmonaco.bbl.events.BioClickEvent;
import com.vmonaco.bbl.events.BioKeystrokeEvent;
import com.vmonaco.bbl.events.BioMotionEvent;
import com.vmonaco.bbl.events.BioScrollEvent;
import com.vmonaco.bbl.events.BioStylometryEvent;

public class Listener  implements 
NativeKeyListener, 
NativeMouseWheelListener,
NativeMouseInputListener
{
    private static final int CAPTURE_DELTA_X = 100;
    private static final int CAPTURE_DELTA_Y = 100;
    
    private Buffer mBuffer;
    
    private Map<Integer, BioKeystrokeEvent> mActiveKeys;
    private Map<Integer, BioClickEvent> mActiveButtons;
    
    private Set<Integer> mPrintableKeys;
    private Set<Integer> mPrintableModifierKeys;
    
    private Stack<Character> mKeysTyped;
    private BioStylometryEvent mCurrentStylometry;
    
    public Listener(Buffer buffer) {
        mBuffer = buffer;
        
        mActiveKeys = new HashMap<Integer, BioKeystrokeEvent>();
        mActiveButtons = new HashMap<Integer, BioClickEvent>();
        mKeysTyped = new Stack<Character>();
        mPrintableKeys = new HashSet<Integer>();
        mPrintableModifierKeys = new HashSet<Integer>();
        
        mPrintableModifierKeys.add(NativeKeyEvent.VK_CAPS_LOCK);
        mPrintableModifierKeys.add(NativeKeyEvent.VK_SHIFT);
    }
    
    private static String lowerCSVString(String str) {
        return str.trim().toLowerCase().replace("+", ",");
    }
    
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent event) {
        BioScrollEvent bioEvent = new BioScrollEvent();
        
        bioEvent.time = event.getWhen();
        bioEvent.rotation = event.getWheelRotation();
        bioEvent.amount = event.getScrollAmount();
        bioEvent.type = event.getScrollType();
        bioEvent.x = event.getX();
        bioEvent.y = event.getY();
        bioEvent.modifier_code = event.getModifiers();
        bioEvent.modifier_string = lowerCSVString(NativeMouseEvent.getModifiersText(event.getModifiers()));
        
        mBuffer.addScrollEvent(bioEvent);
        BioLogger.LOGGER.log(Level.INFO, bioEvent.logString());
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent event) {
        BioMotionEvent bioEvent = new BioMotionEvent();
        
        bioEvent.time = event.getWhen();
        bioEvent.x = event.getX();
        bioEvent.y = event.getY();
        bioEvent.modifier_code = event.getModifiers();
        bioEvent.modifier_string = lowerCSVString(NativeMouseEvent.getModifiersText(event.getModifiers()));
        bioEvent.dragged = 1;
        
        mBuffer.addMotionEvent(bioEvent);
        BioLogger.LOGGER.info(bioEvent.logString());
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent event) {
        BioMotionEvent bioEvent = new BioMotionEvent();
        
        bioEvent.time = event.getWhen();
        bioEvent.x = event.getX();
        bioEvent.y = event.getY();
        bioEvent.modifier_code = event.getModifiers();
        bioEvent.modifier_string = lowerCSVString(NativeMouseEvent.getModifiersText(event.getModifiers()));
        bioEvent.dragged = 0;
        
        mBuffer.addMotionEvent(bioEvent);
        BioLogger.LOGGER.info(bioEvent.logString());
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent event) {
        // a mouse click may have changed the context of input, reset stylometry
        resetStylometry();
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
        bioEvent.modifier_string = lowerCSVString(NativeMouseEvent.getModifiersText(event.getModifiers()));
        bioEvent.press_x = event.getX();
        bioEvent.press_y = event.getY();
        
        Rectangle capture = new Rectangle(event.getX() - CAPTURE_DELTA_X, event.getY() - CAPTURE_DELTA_Y, 
                CAPTURE_DELTA_X * 2, CAPTURE_DELTA_Y * 2);
        bioEvent.image = Utility.screenCapture(capture);
        
        mActiveButtons.put(event.getButton(), bioEvent);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent event) {
        BioClickEvent bioEvent = mActiveButtons.remove(event.getButton());
        
        bioEvent.release_time = event.getWhen();
        bioEvent.release_x = event.getX();
        bioEvent.release_y = event.getY();
        
        mBuffer.addClickEvent(bioEvent);
        BioLogger.LOGGER.info(bioEvent.logString());
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
//        Application.LOGGER.log(Level.INFO, "Pressed:" + event.getRawCode());
        
        if (mActiveKeys.containsKey(event.getRawCode())) {
            return;
        }
        
        BioKeystrokeEvent bioEvent = new BioKeystrokeEvent();
        
        bioEvent.press_time = event.getWhen();
        bioEvent.key_code = event.getKeyCode();
        bioEvent.key_string = lowerCSVString(NativeKeyEvent.getKeyText(event.getKeyCode()));
        bioEvent.modifier_code = event.getModifiers();
        bioEvent.modifier_string = lowerCSVString(NativeKeyEvent.getModifiersText(event.getModifiers()));
        bioEvent.key_location = event.getKeyLocation();
        
        mActiveKeys.put(event.getRawCode(), bioEvent);
        
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        
        // if the key that was released is not a printable character or
        // a "printable" modifier, then reset the stylometry. This indicates
        // that the input context may have changed (ie. switch windows, some 
        // other shortcut)
        if (! mPrintableModifierKeys.contains(event.getKeyCode())
                && ! mPrintableKeys.contains(event.getRawCode())) {
            resetStylometry();
        }
        
        BioKeystrokeEvent bioEvent = mActiveKeys.remove(event.getRawCode());
        bioEvent.release_time = event.getWhen();
        mBuffer.addKeystrokeEvent(bioEvent);
        BioLogger.LOGGER.info(bioEvent.logString());
    }

    @Override
    /**
     * This should only fire when a _printable_ character is typed. Even then,
     * some error checking need to be done to keep track of the context in which
     * the key was typed.
     */
    public void nativeKeyTyped(NativeKeyEvent event) {
        
        // if the key has a modifier that isn't shift, don't do anything with it
        if (event.getModifiers() != 0 && event.getModifiers() != 1) {
            return;
        }
        
        // reset on enter and tab since this may change focus
        if (event.getKeyChar() ==  '\t'
                || event.getKeyChar() == '\n'
                || event.getKeyChar() == '\r'
                ) {
            resetStylometry();
            return;
        }
        
        // this is a printable key, keep track of it
        mPrintableKeys.add(event.getRawCode());
        
        if (mCurrentStylometry == null) {
            mCurrentStylometry = new BioStylometryEvent();
            mCurrentStylometry.start_time = event.getWhen();
        }
        
        mCurrentStylometry.end_time = event.getWhen();
        
        
        boolean isBackspace = (event.getKeyChar() == '\b');
        // don't store backspaces, pop the stack instead
        if (! isBackspace) {
            BioLogger.LOGGER.info("Key typed: " + event.getKeyChar());
            mKeysTyped.push(event.getKeyChar());
        } else if (! mKeysTyped.empty()) {
            mKeysTyped.pop();
        }
    }
    
    private void resetStylometry() {
        if (mCurrentStylometry == null) {
            return;
        }
        
        // this should _always_ iterate the stack in FIFO order, but does it?
        for (Character s : mKeysTyped) {
            mCurrentStylometry.text += s;
        }
        
        mBuffer.addStylometryEvent(mCurrentStylometry);
        BioLogger.LOGGER.info(mCurrentStylometry.logString());
        mCurrentStylometry = null;
        mKeysTyped.clear();
    }
}
