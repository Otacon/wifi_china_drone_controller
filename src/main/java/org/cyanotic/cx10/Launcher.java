package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        CX10 cx10 = new CX10();
        cx10.connect();
        cx10.startControls();
        cx10.startVideoRecorder();
        cx10.startVideoStream();
        System.in.read();
        cx10.disconnect();
    }
}
