package com.facetoe.bluetoothserver;


import org.a0z.mpdlocal.AbstractCommand;

import java.util.Arrays;
import java.util.List;

/**
 * RemoteMPD
 * Created by facetoe on 9/02/14.
 */
public class BTServerCommand extends AbstractCommand {

    // Playlist commands
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

    // Bulk commands
    public static final String MPD_CMD_START_BULK = "command_list_begin";
    public static final String MPD_CMD_START_BULK_OK = "command_list_ok_begin";
    public static final String MPD_CMD_BULK_SEP = "list_OK";
    public static final String MPD_CMD_END_BULK = "command_list_end";


    public static final int MIN_VOLUME = 0;
    public static final int MAX_VOLUME = 100;

    public static final String MPD_CMD_CLEARERROR = "clearerror";
    public static final String MPD_CMD_CLOSE = "close";
    public static final String MPD_CMD_COUNT = "count";
    public static final String MPD_CMD_CROSSFADE = "crossfade";
    public static final String MPD_CMD_FIND = "find";
    public static final String MPD_CMD_KILL = "kill";
    public static final String MPD_CMD_LIST_TAG = "list";
    public static final String MPD_CMD_LISTALL = "listall";
    public static final String MPD_CMD_LISTALLINFO = "listallinfo";
    public static final String MPD_CMD_LISTPLAYLISTS = "listplaylists";
    public static final String MPD_CMD_LSDIR = "lsinfo";
    public static final String MPD_CMD_NEXT = "next";
    public static final String MPD_CMD_PAUSE = "pause";
    public static final String MPD_CMD_PASSWORD = "password";
    public static final String MPD_CMD_PLAY = "play";
    public static final String MPD_CMD_PLAY_ID = "playid";
    public static final String MPD_CMD_PREV = "previous";
    public static final String MPD_CMD_REFRESH = "update";
    public static final String MPD_CMD_REPEAT = "repeat";
    public static final String MPD_CMD_CONSUME = "consume";
    public static final String MPD_CMD_SINGLE = "single";
    public static final String MPD_CMD_RANDOM = "random";
    public static final String MPD_CMD_SEARCH = "search";
    public static final String MPD_CMD_SEEK = "seek";
    public static final String MPD_CMD_SEEK_ID = "seekid";
    public static final String MPD_CMD_STATISTICS = "stats";
    public static final String MPD_CMD_STATUS = "status";
    public static final String MPD_CMD_STOP = "stop";
    public static final String MPD_CMD_SET_VOLUME = "setvol";
    public static final String MPD_CMD_OUTPUTS = "outputs";
    public static final String MPD_CMD_OUTPUTENABLE = "enableoutput";
    public static final String MPD_CMD_OUTPUTDISABLE = "disableoutput";
    public static final String MPD_CMD_PLAYLIST_INFO = "listplaylistinfo";

    public static final String MPD_CMD_IDLE="idle";
    public static final String MPD_CMD_PING = "ping";

    // deprecated commands
    public static final String MPD_CMD_VOLUME = "volume";

    /**
     * MPD default TCP port.
     */
    public static final int DEFAULT_MPD_PORT = 6600;

    public static final String MPD_FIND_ALBUM = "album";
    public static final String MPD_FIND_ARTIST = "artist";

    public static final String MPD_SEARCH_ALBUM = "album";
    public static final String MPD_SEARCH_ARTIST = "artist";
    public static final String MPD_SEARCH_FILENAME = "filename";
    public static final String MPD_SEARCH_TITLE = "title";
    public static final String MPD_SEARCH_GENRE = "genre";

    public static final String MPD_TAG_ALBUM = "album";
    public static final String MPD_TAG_ARTIST = "artist";
    public static final String MPD_TAG_ALBUM_ARTIST = "albumartist";
    public static final String MPD_TAG_GENRE = "genre";


    protected static List<String> BULK_COMMANDS = Arrays.asList(MPD_CMD_START_BULK, MPD_CMD_START_BULK_OK, MPD_CMD_BULK_SEP, MPD_CMD_END_BULK);

    public BTServerCommand(String _command, String... _args) {
        super(_command, _args);
    }

    public static boolean isBulkCommand(String command) {
        return !BULK_COMMANDS.contains(command);
    }
}
