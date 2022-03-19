package demons.night;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import demons.night.DemonsGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new DemonsGame(), config);
		FileHandle file = Gdx.files.local("scores.db");
		getDatabasePath("scores.db");

	}
}
