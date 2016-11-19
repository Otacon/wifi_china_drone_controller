package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.io.XInput;
import org.cyanotic.cx10.net.*;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        Connection c = new Connection("172.16.10.1", 8888);
        c.connect();
        c.setName("org.cyanotic.cx10.net.Connection 1");
        c.sendMessage(new HelloMessage1());

        Connection c2 = new Connection("172.16.10.1", 8888);
        c2.connect();
        c2.setName("org.cyanotic.cx10.net.Connection 2");
        c2.sendMessage(new HelloMessage2());
        c2.disconnect();

        c.sendMessage(new HelloMessage3());
        c.sendMessage(new HelloMessage4());
        c.sendMessage(new HelloMessage5());
        c.sendMessage(new HelloMessage6());

        Controller controller = new Controller(new XInput(), new CommandConnection("172.16.10.1", 8895));
        controller.start();
        Thread.sleep(60000);
    }
}
