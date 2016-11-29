package org.cyanotic.cx10.io.controls;

import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.XInputComponents;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.model.Command;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class XInput implements IController {

    public static final long XINPUT_DEFAULT_POLL_TIME = 20;
    private Thread pollingThread;
    private long pollTime;

    private CommandListener listener;
    private XInputDevice device;

    public XInput() {
        pollTime = XINPUT_DEFAULT_POLL_TIME;
    }

    public XInput(long pollTime) {
        if (pollTime > 0) {
            this.pollTime = pollTime;
        } else {
            this.pollTime = XINPUT_DEFAULT_POLL_TIME;
        }
    }

    public void start() {
        device = null;
        System.out.println("Initializing XInput...");
        try {
            XInputDevice[] allDevices = XInputDevice.getAllDevices();
            if (allDevices.length > 0) {
                device = allDevices[0];
            }
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }

        if (device == null) {
            System.err.println("Unable to start XInputDevice");
        }

        pollingThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    poll();
                    try {
                        Thread.sleep(pollTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pollingThread.start();
        System.out.println("XInput Started");
    }

    private void poll() {
        if (listener == null) {
            return;
        }

        if (!device.poll()) {
            System.err.println("Unable to poll the device");
            return;
        }

        XInputComponents components = device.getComponents();
        XInputAxes axes = components.getAxes();

        int lt = descretize(axes.lt);
        int rt = descretize(axes.rt);

        int lx = descretize(axes.lx);
        int ly = descretize(axes.ly);

        int rx = descretize(axes.rx);
        int ry = descretize(axes.ry);

        Command command = new Command();

        command.setTakeOff(rt > 64);
        command.setLand(lt > 64);
        command.setThrottle(ly);
        command.setYaw(lx);
        command.setPitch(ry);
        command.setRoll(rx);

        listener.onCommandReceived(command);
    }

    private int descretize(float f) {
        return 24 * (Math.round((f * 128) / 24));
    }

    public void stop() {
        System.out.println("Stopping XInput...");
        if (pollingThread.isAlive()) {
            pollingThread.interrupt();
            System.out.println("Stopping XInput Interrupted");
        } else {
            System.err.println("Unable to stop XInput. Was it interrupted before?");
        }
    }

    public boolean isAvailable() {
        if (!XInputDevice.isAvailable()) {
            return false;
        }

        try {
            if (XInputDevice.getAllDevices().length <= 0) {
                return false;
            }
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void setListener(CommandListener controlListener) {
        this.listener = controlListener;
    }
}
