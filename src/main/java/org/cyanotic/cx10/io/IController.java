package org.cyanotic.cx10.io;

import org.cyanotic.cx10.model.Command;

/**
 * Created by cyanotic on 19/11/2016.
 */
public interface IController {

    void start();

    void stop();

    boolean isAvailable();

    void setListener(CommandListener controlListener);

    interface CommandListener{
        void onCommandReceived(Command command);
    }

}
