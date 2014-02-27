package org.a0z.mpdlocal;

import org.a0z.mpdlocal.exception.MPDServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * ${PROJECT_NAME}
 * Created by facetoe on 30/12/13.
 *
 * This class waits for changes from MPD and then passes the changes to any listeners.

 */
public class MPDRawChangeMonitor extends Thread {

    public interface MPDRawChangeListener {
        void updateChanges(List<String> changes);
        void notifyError(Exception e);
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
                // This always gets thrown when killing the connection
                if(!e.getMessage().equals("The MPD request has been canceled")) {
                    notifyError(e);
                }
            }
        }
    }

    private void notifyChanges(List<String> changes) {
        for (MPDRawChangeListener listener : changeListeners) {
            listener.updateChanges(changes);
        }
    }

    private void notifyError(Exception e) {
        for (MPDRawChangeListener listener : changeListeners) {
            listener.notifyError(e);
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
