package com.facetoe.bluetoothserver;

import org.a0z.mpdlocal.exception.MPDServerException;

import javax.microedition.io.StreamConnection;
import java.io.IOException;

public class ConnectedThread extends Thread {
    StreamConnection connection;
    public ConnectedThread(StreamConnection connection) {
        System.out.println("Entered ConnectedThread: " + Thread.currentThread().getName());
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            String host = "localhost";
            String passwd = "password";
            int port = 6600;
            MPDManager manager = new MPDManager(connection, passwd, port, host);
            manager.run();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MPDServerException e) {
            e.printStackTrace();
        }
    }
}

