package org.a0z.mpdlocal;

/**
 * Created by facetoe on 30/12/13.
 */
public class Main {
    public static void main(String[] args) {
       System.out.println(new MPDCommand(MPDCommand.MPD_CMD_VOLUME, "20"));
    }
}
