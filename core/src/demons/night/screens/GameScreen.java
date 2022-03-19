package demons.night.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Input;
import demons.night.DemonsGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import demons.night.managers.AndroidControlsManager;
import demons.night.managers.ResourceManager;
import demons.night.managers.SpriteManager;

public class GameScreen implements Screen {

    final DemonsGame game;
    public SpriteManager spriteManager;
    AndroidControlsManager controladorAndroid;
    public Stage stage;
    public Touchpad joystick;
    public Touchpad saltar;
    public Touchpad golpear;
	
    public GameScreen(DemonsGame game) {
        this.game = game;
        stage = new Stage();
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            controladorAndroid = new AndroidControlsManager(stage, game.skin);
        }
        spriteManager = new SpriteManager(game, controladorAndroid);

        game.paused = false;
    }
	
    /*
     * Metodo que se invoca cuando esta pantalla es
     * la que se está mostrando
     * @see com.badlogic.gdx.Screen#show()
     */
    @Override
    public void show() {

        game.paused = false;

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float dt) {

        if (!game.paused) {
            // Actualizamos primero (es más eficiente)
            spriteManager.update(dt);
        }

        // En cada frame se limpia la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteManager.draw();
        stage.act();
        stage.draw();

        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            handleScreenPause();
        }else{
            handleKeyboard();
        }

    }

    private void handleKeyboard() {

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new InGameMenuScreen(game, this));
        }
    }

    private void handleScreenPause() {

        if (spriteManager.pause_clicked) {
            game.setScreen(new InGameMenuScreen(game, this));
            spriteManager.pause_clicked = false;

            if(ResourceManager.sonido_activado){
                spriteManager.music.pause();
                //spriteManager.music.dispose();
            }

        }
    }
	
    /*
     * Metodo que se invoca cuando esta pantalla
     * deja de ser la principal
     */
    @Override
    public void hide() {
        game.paused = true;
    }

    @Override
    public void dispose() {
        /*
        music.stop();
        music.dispose();
        */
        spriteManager.dispose();
    }

    @Override
    public void resize(int width, int height) {
        spriteManager.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        game.paused = false;
    }
}
