package com.facetoe.bluetoothserver;

public class BluetoothServer {
    public static void main(String[] args) {
        new Thread(new WaitThread()).start();
    }
}
