package com.facetoe.bluetoothserver;

import com.google.gson.Gson;
import org.a0z.mpdlocal.MPD;
import org.a0z.mpdlocal.MPDCommand;
import org.a0z.mpdlocal.MPDRawChangeMonitor;
import org.a0z.mpdlocal.exception.MPDServerException;

import javax.microedition.io.StreamConnection;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by facetoe on 31/12/13.
 */

public class MPDManager implements MPDRawChangeMonitor.MPDRawChangeListener {
    private final boolean DEBUG = true;
    private final MPD mpd;
    private final Gson gson = new Gson();

    private StreamConnection connection;
    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    private String host;
    private int port;
    private String password;
    private MPDRawChangeMonitor changeMonitor;
    private boolean readingBulkCommandList = false;

    public MPDManager(StreamConnection connection, String password, int port, String host)
            throws MPDServerException, IOException {

        this.connection = connection;
        this.password = password;
        this.port = port;
        this.host = host;
        this.mpd = new MPD();
        initConnection();
    }

    private void initConnection() throws MPDServerException, IOException {
        mpd.connect(host, port, password);

        changeMonitor = new MPDRawChangeMonitor(mpd, 1000);
        changeMonitor.addMPDRawChangeListener(this);
        changeMonitor.start();

        inputStream = new BufferedReader(
                new InputStreamReader(
                        connection.openInputStream(),
                        Charset.forName("UTF-8")));

        outputStream = new BufferedWriter(
                new OutputStreamWriter(
                        connection.openOutputStream(),
                        Charset.forName("UTF-8")));
    }

    public void run() throws IOException {
        String input;
        while (true) {
            if (DEBUG) System.out.println("Waiting for input...");

            input = inputStream.readLine();
            System.out.println("Received: " + input);

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
            changeMonitor.giveup();
            mpd.disconnect();
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (MPDServerException e) {
            System.err.println("Error closing mpd: " + e.getMessage());
        }
        if (DEBUG) System.out.println("Connection closed.");
    }

    private void processCommand(String input) {
        if (input.isEmpty()) return;
        BTServerCommand btCommand = gson.fromJson(input, BTServerCommand.class);
        String command = btCommand.getCommand();

        try {
            if (btCommand.isSynchronous()) {
                handleSyncronous(btCommand);

            } else if (BTServerCommand.isBulkCommand(command) || readingBulkCommandList) {
                processBulkCommand(command);

            } else {
                mpd.getMpdConnection().sendCommand(new MPDCommand(btCommand.getCommand(), btCommand.getArgs()));
            }

        } catch (MPDServerException e) {
            e.printStackTrace();
        }
    }

    private void handleSyncronous(BTServerCommand btCommand) throws MPDServerException {
        List<String> result = mpd.getMpdConnection().sendCommand(btCommand.getCommand(), btCommand.getArgs());
        System.out.println("Got " + result.size() + " results");
        MPDResponse response = new MPDResponse(MPDResponse.SYNC_READ_WRITE, result);
        response.setSynchronous(true);
        sendResponse(response);

    }

    private void processBulkCommand(String input) throws MPDServerException {
        BTServerCommand btCommand = gson.fromJson(input, BTServerCommand.class);

        if (btCommand.getCommand().equals(BTServerCommand.MPD_CMD_START_BULK)) {
            readingBulkCommandList = true;
        } else if (input.equals(BTServerCommand.MPD_CMD_END_BULK)) {
            mpd.getMpdConnection().sendCommandQueue();
            readingBulkCommandList = false;
        } else {
            mpd.getMpdConnection().queueCommand(btCommand.getCommand(), btCommand.getArgs());
        }
    }

    private void sendResponse(Object obj) {
        write(gson.toJson(obj));
        System.out.println("Sent: " + obj.toString());
    }

    private void write(String message) {
        try {
            outputStream.write(message + "\n");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateChanges(List<String> changes) {
        MPDResponse changeResponse = new MPDResponse(MPDResponse.EVENT_UPDATE_RAW_CHANGES, changes);
        sendResponse(changeResponse);
    }
}
