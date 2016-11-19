package org.cyanotic.cx10;

import org.cyanotic.cx10.io.IController;
import org.cyanotic.cx10.model.Command;
import org.cyanotic.cx10.net.CommandConnection;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Controller extends Thread implements IController.CommandListener {

    private final IController controller;
    private final CommandConnection connection;
    private Command lastCommand;
    private boolean tookOff = false;

    public Controller(IController controller, CommandConnection connection){
        this.controller = controller;
        this.connection = connection;
        lastCommand = new Command();
    }

    @Override
    public void start(){
        super.start();
    }

    @Override
    public void interrupt(){
        controller.setListener(null);
        controller.stop();
    }

    @Override
    public void run(){
        controller.setListener(this);
        controller.start();
        while(!isInterrupted()) {
            if(lastCommand.isTakeOff() && !tookOff){
                takeOff();
                continue;
            }
            if(!tookOff){
                connection.sendCommand(new Command());
            } else {
                connection.sendCommand(lastCommand);
            }
            hold();
        }
    }

    private void takeOff() {
        System.out.println("Take off procedure started");
        Command command = new Command();
        command.setTakeOff(true);
        command.setPitch(126);
        for(int i = 0; i < 20; i++){
            connection.sendCommand(command);
            hold();
        }
        System.out.println("Take off procedure finished");
        tookOff = true;
    }

    private void land() {
        System.out.println("Landing procedure started");
        Command command = new Command();
        command.setLand(true);
        command.setPitch(126);
        for(int i = 0; i < 20; i++){
            connection.sendCommand(command);
            hold();
        }
        System.out.println("Landing procedure finished");
        tookOff = false;
    }

    private void hold(){
        try {
            sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onCommandReceived(Command command) {
        if(command == null) {
            lastCommand = new Command();
        } else {
            lastCommand = command;
        }
    }
}
