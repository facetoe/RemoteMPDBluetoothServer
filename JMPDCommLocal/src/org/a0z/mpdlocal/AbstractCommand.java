package org.a0z.mpdlocal;

/**
 * RemoteMPD
 * Created by facetoe on 8/02/14.
 */
public abstract class AbstractCommand {

    String command = null;
    String[] args = null;

    protected boolean synchronous = false;

    public AbstractCommand(String _command, String... _args) {
        this.command = _command;
        this.args = _args;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
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
        return outBuf.toString();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
