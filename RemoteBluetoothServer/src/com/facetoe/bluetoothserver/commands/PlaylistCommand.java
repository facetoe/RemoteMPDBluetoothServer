package com.facetoe.bluetoothserver.commands;


import org.a0z.mpdlocal.AbstractCommand;

/**
 * RemoteMPD
 * Created by facetoe on 9/02/14.
 */
public class PlaylistCommand extends AbstractCommand {
    public static final String MPD_CMD_PLAYLIST_ADD = "add";
    public static final String MPD_CMD_PLAYLIST_CLEAR = "clear";
    public static final String MPD_CMD_PLAYLIST_DELETE = "rm";
    public static final String MPD_CMD_PLAYLIST_LIST = "playlistid";
    public static final String MPD_CMD_PLAYLIST_CHANGES = "plchanges";
    public static final String MPD_CMD_PLAYLIST_LOAD = "load";
    public static final String MPD_CMD_PLAYLIST_MOVE = "move";
    public static final String MPD_CMD_PLAYLIST_MOVE_ID = "moveid";
    public static final String MPD_CMD_PLAYLIST_REMOVE = "delete";
    public static final String MPD_CMD_PLAYLIST_REMOVE_ID = "deleteid";
    public static final String MPD_CMD_PLAYLIST_SAVE = "save";
    public static final String MPD_CMD_PLAYLIST_SHUFFLE = "shuffle";
    public static final String MPD_CMD_PLAYLIST_SWAP = "swap";
    public static final String MPD_CMD_PLAYLIST_SWAP_ID = "swapid";

    public PlaylistCommand(String _command, String... _args) {
        super(_command, _args);
    }
}
