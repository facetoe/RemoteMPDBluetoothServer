package org.a0z.mpdlocal;

import org.a0z.mpdlocal.exception.MPDServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by facetoe on 30/12/13.
 */
public class MPDRawChangeMonitor extends Thread {

    public interface MPDRawChangeListener {
        void updateChanges(List<String> changes);
    }

    protected int delay;
    protected MPD mpd;
    protected boolean giveup;
    private ArrayList<MPDRawChangeListener> changeListeners = new ArrayList<MPDRawChangeListener>();

    /**
     * Constructs a MPDStatusMonitor.
     *
     * @param mpd   MPD server to monitor.
     * @param delay status query interval.
     */
    public MPDRawChangeMonitor(MPD mpd, int delay) {
        this.mpd = mpd;
        this.delay = delay;
        this.giveup = false;
    }

    public void run() {
        while (!giveup) {
            try {
                List<String> changes = mpd.waitForChanges();
                notifyChanges(changes);
            } catch (MPDServerException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyChanges(List<String> changes) {
        for (MPDRawChangeListener listener : changeListeners) {
            listener.updateChanges(changes);
        }
    }

    public void addMPDRawChangeListener(MPDRawChangeListener listener) {
        changeListeners.add(listener);
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
