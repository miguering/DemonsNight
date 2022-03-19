package demons.night.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import demons.night.DemonsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
            
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            
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
