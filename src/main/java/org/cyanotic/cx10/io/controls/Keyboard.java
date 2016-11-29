package org.cyanotic.cx10.io.controls;

import org.cyanotic.cx10.model.Command;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

/**
 * Created by orfeo.ciano on 29/11/2016.
 */
public class Keyboard implements IController, KeyEventDispatcher {
    private final KeyboardFocusManager focusManager;
    private CommandListener commandListener;
    private Command command = new Command();

    public Keyboard(KeyboardFocusManager focusManager) {
        this.focusManager = focusManager;
    }

    public void start() {
        focusManager.addKeyEventDispatcher(this);
    }

    public void stop() {
        focusManager.removeKeyEventDispatcher(this);
    }

    public boolean isAvailable() {
        return false;
    }

    public void setListener(CommandListener controlListener) {
        this.commandListener = controlListener;
    }

    private void onKeyEvent(KeyEvent e, boolean isPressed) {
        int value = isPressed ? 127 : 0;
        boolean newInput = true;
        switch (e.getKeyCode()) {
            case VK_W:
                command.setThrottle(value);
                break;
            case VK_S:
                command.setThrottle(-value);
                break;
            case VK_D:
                command.setYaw(value);
                break;
            case VK_A:
                command.setYaw(-value);
                break;
            case VK_I:
                command.setPitch(value);
                break;
            case VK_K:
                command.setPitch(-value);
                break;
            case VK_L:
                command.setRoll(value);
                break;
            case VK_J:
                command.setRoll(-value);
                break;
            case VK_UP:
                command.setTakeOff(isPressed);
                break;
            case VK_DOWN:
                command.setLand(isPressed);
                break;
            default:
                newInput = false;
        }

        if (commandListener != null && newInput) {
            commandListener.onCommandReceived(command);
        }
        e.consume();
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KEY_PRESSED) {
            onKeyEvent(e, true);
        } else if (e.getID() == KEY_RELEASED) {
            onKeyEvent(e, false);
        }
        return false;
    }
}
