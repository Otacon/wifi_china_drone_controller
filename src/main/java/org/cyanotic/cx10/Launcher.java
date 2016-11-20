package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.io.XInput;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Connection;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        Connection c1 = new Connection("172.16.10.1", 8888);
        Connection c2 = new Connection("172.16.10.1", 8888);
        Controller controller = new Controller(new XInput(), new CommandConnection("172.16.10.1", 8895), c1, c2);
        controller.start();
        Thread.sleep(60000);

    }


}
