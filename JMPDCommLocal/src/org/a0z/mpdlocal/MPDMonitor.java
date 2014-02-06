package org.a0z.mpdlocal;

import org.a0z.mpdlocal.event.StatusChangeListener;
import org.a0z.mpdlocal.event.TrackPositionListener;

import java.util.LinkedList;

/**
 * Created by facetoe on 30/12/13.
 */
public class MPDMonitor extends Thread {
    protected int delay;
    protected MPD mpd;
    protected boolean giveup;

    protected LinkedList<StatusChangeListener> statusChangedListeners;
    protected LinkedList<TrackPositionListener> trackPositionChangedListeners;

    /**
     * Constructs a MPDStatusMonitor.
     *
     * @param mpd
     *           MPD server to monitor.
     * @param delay
     *           status query interval.
     */
    public MPDMonitor(MPD mpd, int delay) {
        this.mpd = mpd;
        this.delay = delay;
        this.giveup = false;
        this.statusChangedListeners = new LinkedList<StatusChangeListener>();
        this.trackPositionChangedListeners = new LinkedList<TrackPositionListener>();
    }

    /**
     * Main thread method
     */
    public void run() {
    }

    /**
     * Adds a <code>StatusChangeListener</code>.
     *
     * @param listener
     *           a <code>StatusChangeListener</code>.
     */
    public void addStatusChangeListener(StatusChangeListener listener) {
        statusChangedListeners.add(listener);
    }

    /**
     * Adds a <code>TrackPositionListener</code>.
     *
     * @param listener
     *           a <code>TrackPositionListener</code>.
     */
    public void addTrackPositionListener(TrackPositionListener listener) {
        trackPositionChangedListeners.add(listener);
    }

    /**
     * Gracefully terminate tread.
     */
    public void giveup() {
        this.giveup = true;
    }

    public boolean isGivingUp() {
        return this.giveup;
    }
}
