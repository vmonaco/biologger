package com.vmonaco.bio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vmonaco.bio.events.BioEvent;

public class Buffer {
	public static final String TAG = "BioKeyboard/Buffer";

	private final List<Consumer> mConsumers; // Immutable

	// Guarded by this. We still need to synchronize access so the operations on the queue are atomic
	private final BlockingQueue<BioEvent> eventQueue = new ArrayBlockingQueue<BioEvent>(BUFFER_CAPACITY);
	private boolean mStopped = false; // Guarded by this

	private final ExecutorService mDrainingExecutor = Executors.newSingleThreadExecutor(); // Thread safe
	private final Runnable mDrainingTask = new Runnable() { // Thread safe
		@Override
		public void run() {
			List<BioEvent> batch = new ArrayList<BioEvent>();
			synchronized (Buffer.this) {
				eventQueue.drainTo(batch, eventQueue.size());
			}

			List<BioEvent> unmodifiableBatch = Collections.unmodifiableList(batch);
			for (Consumer consumer : mConsumers) {
				consumer.onEventsReceived(Buffer.this, unmodifiableBatch);
			}
		}
	};
	private final Runnable mShutdownTask = new Runnable() { // Thread safe
		@Override
		public void run() {
			for (Consumer consumer : mConsumers) {
				consumer.onSessionEnd(Buffer.this);
			}
		}
	};

	private static final int BUFFER_CAPACITY = 1000;
	private static final int SENDING_THRESHOLD = 1; // For safety

	public Buffer(Consumer... eventConsumers) {
		mConsumers = Collections.unmodifiableList(new ArrayList<Consumer>(Arrays.asList(eventConsumers)));
	}

	public void addEvent(BioEvent event) {
		int queueSize;
		synchronized (this) {
			if (mStopped)
				throw new IllegalStateException("The buffer has already been stopped, no new events are accepted");

			eventQueue.add(event);
			queueSize = eventQueue.size();
		}
		if (queueSize >= SENDING_THRESHOLD)
			flush();
	}

	private void flush() {
		mDrainingExecutor.submit(mDrainingTask);
	}

	public void stop() {
		synchronized (this) {
			if (mStopped)
				return;
			mStopped = true;
		}

		flush();

		mDrainingExecutor.submit(mShutdownTask);
		mDrainingExecutor.shutdown();
	}
}
