package org.cyanotic.cx10;

import org.cyanotic.cx10.io.IController;
import org.cyanotic.cx10.model.Command;
import org.cyanotic.cx10.net.*;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Controller extends Thread implements IController.CommandListener {

    private final IController controller;
    private final CommandConnection dataConnection;
    private final Connection transportConnection;
    private final Connection transportConnection2;
    private Command lastCommand;

    public Controller(IController controller, CommandConnection dataConnection, Connection transportConnection, Connection transportConnection2) {
        this.controller = controller;
        this.dataConnection = dataConnection;
        this.transportConnection = transportConnection;
        this.transportConnection2 = transportConnection2;
        lastCommand = new Command();
    }

    @Override
    public void interrupt() {
        controller.setListener(null);
        controller.stop();
        super.interrupt();
    }

    @Override
    public void run() {
        try {
            handshake();
        } catch (IOException e) {
            System.err.println("Unable to start the handshake");
            e.printStackTrace();
            return;
        }

        controller.setListener(this);
        controller.start();

        while (!isInterrupted()) {
            dataConnection.sendCommand(lastCommand);
            hold();
        }
    }

    private void handshake() throws IOException {
        transportConnection.connect();
        transportConnection.setName("org.cyanotic.cx10.net.Connection 1");
        transportConnection.sendMessage(new HelloMessage1());
        transportConnection.sendMessage(new HelloMessage3());
        transportConnection.sendMessage(new HelloMessage4());
        transportConnection.sendMessage(new HelloMessage5());
        transportConnection.sendMessage(new HelloMessage6());
    }

    private void hold() {
        try {
            sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onCommandReceived(Command command) {
        if (command == null) {
            lastCommand = new Command();
        } else {
            lastCommand = command;
        }
    }
}
