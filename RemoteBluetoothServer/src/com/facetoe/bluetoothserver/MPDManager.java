package com.facetoe.bluetoothserver;

import com.google.gson.Gson;
import org.a0z.mpdlocal.*;
import org.a0z.mpdlocal.event.StatusChangeListener;
import org.a0z.mpdlocal.event.TrackPositionListener;
import org.a0z.mpdlocal.exception.MPDServerException;

import javax.microedition.io.StreamConnection;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by facetoe on 31/12/13.
 */
public class MPDManager implements TrackPositionListener, StatusChangeListener {
    private final boolean DEBUG = true;
    private final MPD mpdComm;
    private final Gson gson = new Gson();
    private MessageDigest md5;
    private String oldMusicListHash = "";

    private StreamConnection connection;
    private BufferedReader inputStream;
    private PrintStream outputStream;

    private static final Pattern digit = Pattern.compile("\\d+");
    private String host;
    private int port;
    private String password;
    private MPDStatusMonitor statusMonitor;

    public MPDManager(StreamConnection connection, String password, int port, String host)
            throws MPDServerException, IOException {

        this.connection = connection;
        this.password = password;
        this.port = port;
        this.host = host;
        this.mpdComm = new MPD();
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        initConnection();
    }

    private void initConnection() throws MPDServerException, IOException {
        mpdComm.connect(host, port, password);

        statusMonitor = new MPDStatusMonitor(mpdComm, 1000);
        statusMonitor.addStatusChangeListener(this);
        statusMonitor.addTrackPositionListener(this);
        statusMonitor.start();

        inputStream = new BufferedReader(new InputStreamReader(connection.openInputStream()));
        outputStream = new PrintStream(new BufferedOutputStream(connection.openOutputStream()), true, "UTF-8");

        oldMusicListHash = computeMD5Hash(getMusicList());
    }

    public void run() throws IOException {
        String input;
        while (true) {
            if (DEBUG) System.out.println("Waiting for input...");

            input = inputStream.readLine();

            // If input is null the remote side closed the connection.
            if (input == null) {
                shutDown();
                break;
            } else {
                processCommand(input);
            }
        }
    }

    private void shutDown() throws IOException {
        try {
            statusMonitor.giveup();
            mpdComm.disconnect();
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (MPDServerException e) {
            System.err.println("Error closing mpd: " + e.getMessage());
        }
        if (DEBUG) System.out.println("Connection closed.");
    }

    private void processCommand(String command) {
        if(command.isEmpty()) return;

        try {
            if (command.equals(MPDCommand.MPD_CMD_PLAY)) mpdComm.play();
            else if (command.equals(MPDCommand.MPD_CMD_NEXT)) mpdComm.next();
            else if (command.equals(MPDCommand.MPD_CMD_PREV)) mpdComm.previous();
            else if (command.equals(MPDCommand.MPD_CMD_VOLUME)) mpdComm.adjustVolume(extractInt(command));
            else if (command.startsWith(MPDCommand.MPD_CMD_PLAY_ID)) mpdComm.skipToId(extractInt(command));
            else if (command.startsWith(MPDCommand.MPD_CMD_PLAYLIST_CHANGES)) updatePlaylist();
            else if (command.equals(MPDCommand.MPD_CMD_STOP)) mpdComm.stop();
            else if (DEBUG) System.out.println("Unknown command: " + command);
        } catch (MPDServerException e) {
            e.printStackTrace();
        }
    }

    private int extractInt(String str) {
        Matcher m = digit.matcher(str);
        if (m.find())
            return Integer.parseInt(m.group());
        else
            return -1;
    }

    private void write(Object obj) {
        write(gson.toJson(obj));
    }

    private void write(String message) {
        outputStream.println(message);
    }

    @Override
    public void volumeChanged(MPDStatus mpdStatus, int oldVolume) {
        if (DEBUG) System.out.println("Volume changed");
        write(new MPDResponse(MPDResponse.EVENT_VOLUME, mpdStatus, oldVolume));
    }

    @Override
    public void playlistChanged(MPDStatus mpdStatus, int oldPlaylistVersion) {
        if (DEBUG) System.out.println("Playlist changed");
        maybeUpdatePlaylist();
    }

    private void maybeUpdatePlaylist() {
        if(playlistHashChanged())
            updatePlaylist();
    }

    private boolean playlistHashChanged() {
        List<Music> musicList = getMusicList();
        String newMusicListHash = computeMD5Hash(musicList);
        return !oldMusicListHash.equals(newMusicListHash);
    }

    private String computeMD5Hash(Object data) {
        String hash = "";
        try {
            byte[] dataBytes = data.toString().getBytes("UTF-8");
            hash =  (new HexBinaryAdapter()).marshal(md5.digest(dataBytes));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private void updatePlaylist() {
        if(DEBUG) System.out.println("Updating playlist");
        List<Music> music = getMusicList();
        oldMusicListHash = computeMD5Hash(music);
        write(new MPDResponse(MPDResponse.EVENT_UPDATE_PLAYLIST, music));
    }

    private List<Music> getMusicList() {
        mpdComm.getPlaylist().playlistChanged(null, -1); // Force a playlist refresh
        return mpdComm.getPlaylist().getMusicList();
    }

    @Override
    public void trackChanged(MPDStatus mpdStatus, int oldTrack) {
        if (DEBUG) System.out.println("Track changed");
        write(new MPDResponse(MPDResponse.EVENT_TRACK, mpdStatus, oldTrack));
    }

    @Override
    public void stateChanged(MPDStatus mpdStatus, String oldState) {
        if (DEBUG) System.out.println("State changed");
        write(new MPDResponse(MPDResponse.EVENT_STATE, mpdStatus, oldState));
    }

    @Override
    public void repeatChanged(boolean repeating) {
        if (DEBUG) System.out.println("Repeat changed");
        write(new MPDResponse(MPDResponse.EVENT_REPEAT, repeating));
    }

    @Override
    public void randomChanged(boolean random) {
        if (DEBUG) System.out.println("Random changed");
        write(new MPDResponse(MPDResponse.EVENT_RANDOM, random));
    }

    @Override
    public void connectionStateChanged(boolean connected, boolean connectionLost) {
        if (DEBUG) System.out.println("Connection State changed");
        write(new MPDResponse(MPDResponse.EVENT_CONNECTIONSTATE, connected, connectionLost));
    }

    @Override
    public void libraryStateChanged(boolean updating) {
        if (DEBUG) System.out.println("Library state changed");
        write(new MPDResponse(MPDResponse.EVENT_UPDATESTATE, updating));
    }

    @Override
    public void trackPositionChanged(MPDStatus status) {
        if (DEBUG) System.out.println("Track position changed");
        write(new MPDResponse(MPDResponse.EVENT_TRACKPOSITION, status));
    }
}
