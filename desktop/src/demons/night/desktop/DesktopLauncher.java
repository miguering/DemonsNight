package demons.night.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import demons.night.DemonsGame;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class DesktopLauncher {
	public static void main (String[] arg) {
            
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            
            config.addIcon("ui/logoandroid.png", Files.FileType.Internal);
            config.addIcon("ui/logo_128.png", Files.FileType.Internal);
            config.addIcon("ui/logo_32.png", Files.FileType.Internal);
            config.addIcon("ui/logo_16.png", Files.FileType.Internal);
            
            
            config.title = "Demon's Night";
            config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
            config.fullscreen = true;

            boolean fullscreen = true;
            if(!fullscreen ){
                config.fullscreen = false;
                config.width /= 1.3f;
                config.height /= 1.3f;
            }
            config.vSyncEnabled = true;


            new LwjglApplication(new DemonsGame(), config);
	}
}
