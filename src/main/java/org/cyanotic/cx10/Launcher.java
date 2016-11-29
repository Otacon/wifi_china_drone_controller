package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.ui.MainWindow;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        new MainWindow();
    }
}
