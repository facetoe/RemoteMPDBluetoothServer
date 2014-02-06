package com.facetoe.bluetoothserver;


import org.a0z.mpdlocal.exception.MPDServerException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

public class WaitThread implements Runnable {

    @Override
    public void run() {
        waitForConnection();
    }

    /**
     * Waiting for connection from devices
     */
    private void waitForConnection() {
        StreamConnectionNotifier notifier = getStreamConnectionNotifier();
        StreamConnection connection;

        // waiting for connection
        while (true) {
            try {
                System.out.println("waiting for connection...");
                connection = notifier.acceptAndOpen();

                // Launch the ConnectedThread to communicate with remote device
                new ConnectedThread(connection).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StreamConnectionNotifier getStreamConnectionNotifier() {
        LocalDevice local ;
        StreamConnectionNotifier notifier = null;

        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            String url = getUrl();
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (BluetoothStateException e) {
            System.err.println("Bluetooth is not turned on.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notifier;
    }

    private String getUrl() {
        UUID uuid = new UUID("04c6093b00001000800000805f9b34fb", false);
        return "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
    }
}
