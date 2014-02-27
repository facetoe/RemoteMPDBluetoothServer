package com.facetoe.bluetoothserver;

import com.google.gson.Gson;
import org.a0z.mpdlocal.*;
import org.a0z.mpdlocal.exception.MPDServerException;

import javax.microedition.io.StreamConnection;
import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by facetoe on 31/12/13.
 */

public class MPDManager implements MPDRawChangeMonitor.MPDRawChangeListener {
    private final boolean VERBOSE = true;
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

    public MPDManager(StreamConnection connection, String password, int port, String host) throws IOException {

        this.connection = connection;
        this.password = password;
        this.port = port;
        this.host = host;
        this.mpd = new MPD();
        initConnection();
    }

    private void initConnection() throws IOException {
        inputStream = new BufferedReader(
                new InputStreamReader(
                        connection.openInputStream(),
                        Charset.forName("UTF-8")));

        outputStream = new BufferedWriter(
                new OutputStreamWriter(
                        connection.openOutputStream(),
                        Charset.forName("UTF-8")));
        connectToMPD();
    }

    private void connectToMPD()  {
        try {
            mpd.connect(host, port, password);
            changeMonitor = new MPDRawChangeMonitor(mpd, 1000);
            changeMonitor.addMPDRawChangeListener(this);
            changeMonitor.start();
        } catch (MPDServerException e) {
            handleError(e);
        } catch (UnknownHostException e) {
            handleError(e);
        }
    }

    // Send an error response to the server and shutdown.
    private void handleError(Exception e) {
        System.err.println("Sending error response: " + e.getMessage());
        MPDResponse errorResponse = new MPDResponse(MPDResponse.EVENT_ERROR, e.getMessage());
        sendResponse(errorResponse);
        shutDown();
    }

    public void run() throws IOException {
        String input;
        while (true) {
            if (VERBOSE) System.out.println("Waiting for input...");

            input = inputStream.readLine();
            if (VERBOSE) System.out.println("Received: " + input);

            // If input is null the remote side closed the connection.
            if (input == null) {
                shutDown();
                break;
            } else {
                processCommand(input);
            }
        }
    }

    private void shutDown() {
        try {
            if (changeMonitor != null) {
                changeMonitor.giveup();
            }
            mpd.disconnect();
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (MPDServerException e) {
            System.err.println("Error closing mpd: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error shutting down server: " + e.getMessage());
        }
        if (VERBOSE) System.out.println("Connection closed.");
    }

    private void processCommand(String input) {
        if (input.isEmpty()) return;
        BTServerCommand btCommand = gson.fromJson(input, BTServerCommand.class);
        String command = btCommand.getCommand();

        try {
            if (command.equals(BTServerCommand.SERVER_CAN_PROCEED)) {
                handleConnectionCheck();

            } else if (btCommand.isSynchronous()) {
                handleSyncronous(btCommand);

            } else if (BTServerCommand.isBulkCommand(command) || readingBulkCommandList) {
                processBulkCommand(command);

            } else {
                sendMpdCommand(new MPDCommand(btCommand.getCommand(), btCommand.getArgs()));
            }

        } catch (MPDServerException e) {
            handleError(e);
        }
    }

    private void handleConnectionCheck() {
        List<String> status = new ArrayList<String>();
        MPDResponse response;
        try {
            status.add("OK " + mpd.getMpdVersion());
            response = new MPDResponse(MPDResponse.SYNC_READ_WRITE, status);
            response.setSynchronous(true);
            sendResponse(response);
        } catch (MPDServerException e) {
            handleError(e);
        }
    }

    private void handleSyncronous(BTServerCommand btCommand) throws MPDServerException {
        List<String> result = sendMpdCommand(btCommand.getCommand(), btCommand.getArgs());
        if (VERBOSE) System.out.println("Got " + result.size() + " results for command: " + btCommand.getCommand());
        MPDResponse response = new MPDResponse(MPDResponse.SYNC_READ_WRITE, result);
        response.setSynchronous(true);
        sendResponse(response);

    }

    private List<String> sendMpdCommand(String command, String[] args) throws MPDServerException {
        return sendMpdCommand(new MPDCommand(command, args));
    }

    private List<String> sendMpdCommand(MPDCommand command) throws MPDServerException {
        MPDConnection conn = mpd.getMpdConnection();
        if (conn == null) {
            handleError(new MPDServerException("No connection to MPD server."));
            return Collections.emptyList();
        } else {
            return conn.sendCommand(command.getCommand(), command.getArgs());
        }
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

    @Override
    public void notifyError(Exception e) {
        handleError(e);
    }
}
