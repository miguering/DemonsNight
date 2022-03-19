package demons.night;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.Game;
import demons.night.screens.SplashScreen;

public class DemonsGame extends Game {

    public Skin skin;
    public Batch batch;
    public BitmapFont font;

    public boolean paused;

    public enum GameState {
            START, RESUME;
    }
    public GameState gameState; 

    /*
     * Método invocado en el momento de crearse la aplicación
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create() {

    
    batch = new SpriteBatch();
    font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
    setScreen(new SplashScreen(this));
    }

    public Skin getSkin() {
        if (skin == null)
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        return skin;
    }

    /*
     * Método que se invoca cada vez que hay que renderizar
     * Es el método donde se actualiza también la lógica del juego
     * @see com.badlogic.gdx.ApplicationListener#pause()
     */
    @Override
    public void render() {
            super.render();
    }

    @Override
    public void resize(int width, int height) {
            getScreen().resize(width, height);
    }

    /*
     * Método invocado cuando se destruye la aplicación
     * Siempre va precedido de una llamada a 'pause()'
     * @see com.badlogic.gdx.ApplicationListener#dispose()
     */
    @Override
    public void dispose() {

            getScreen().dispose();
    }

    /*
     * Proporciona compatibilidad con Android
     * El juego puede ser pasado a segundo plano y debería ser pausado
     * 
     */
    @Override
    public void pause() {
            paused = true;
    }

    /*
     * Proporciona compatibilidad con Android
     * El juego puede ser pasado a primer plano después de haber sido pausado
     * 
     */
    @Override
    public void resume()  {
            paused = false;
    }
}