package org.a0z.mpdlocal;

import java.util.List;

/**
 * RemoteMPD
 * Created by facetoe on 8/02/14.
 */
public abstract class AbstractCommand {
    private static final boolean DEBUG = false;

    String command = null;
    String[] args = null;

    public static List<String> NON_RETRYABLE_COMMANDS;
    protected boolean sentToServer = false;
    protected boolean synchronous = true;


    public AbstractCommand(String _command, String... _args) {
        this.command = _command;
        this.args = _args;
    }

    public String toString() {
        StringBuffer outBuf = new StringBuffer();
        outBuf.append(command);
        for (String arg : args) {
            if(arg == null)
                continue;
            arg = arg.replaceAll("\"", "\\\\\"");
            outBuf.append(" \"" + arg + "\"");
        }
        outBuf.append("\n");
        final String outString = outBuf.toString();
        return outString;
    }

    public static boolean isRetryable(String command) {
        return !NON_RETRYABLE_COMMANDS.contains(command);
    }

    public boolean isSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(boolean sentToServer) {
        this.sentToServer = sentToServer;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }
}
