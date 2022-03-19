package demons.night.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import demons.night.DemonsGame;
import demons.night.managers.ResourceManager;


public class SplashScreen implements Screen {

    private Texture splashTexture;
    private Image splashImage;
    private Stage stage;
    private Table table;
    private float WIDTH, HEIGHT;

    private boolean splashDone = false;
    public Music cancion;
    private DemonsGame game;

    public SplashScreen(DemonsGame game) {
        this.game = game;

        splashTexture = new Texture(Gdx.files.internal("ui/splash.png"));
        splashImage = new Image(splashTexture);
        
        ResourceManager.playOpening();
        
    }

    @Override
    public void render(float delta) {
        /*
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_CLEAR_VALUE);

        stage.act();
        stage.draw();

        // Comprueba si se han cargado todos los recursos
        if (ResourceManager.update()) {
            // Si la animación ha terminado se muestra ya el menú principal
            if (splashDone) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
        */
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        
        if (ResourceManager.update()) {
            // Si la animación ha terminado se muestra ya el menú principal
            if (splashDone) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
        
        
    }

    @Override
    public void show() {
        
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        System.out.println("ancho: " + WIDTH + " alto: " + HEIGHT);
        
        splashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        splashImage.setSize(WIDTH,HEIGHT);
        stage = new Stage(new FitViewport(WIDTH,HEIGHT));
        stage.addActor(splashImage);

        splashImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.fadeIn(1.25F),Actions.delay(1F),Actions.fadeOut(0.75F), Actions.run(new Runnable() {
            @Override
            public void run() {
                splashDone = true;
            }
            })
        ));

        /*
        table = new Table();
        table.setFillParent(true);
        table.center();
        

        // Muestra la imagen de SplashScreen como una animación
        splashImage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f),
                Actions.delay(1.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        splashDone = true;
                    }
                })
        ));

        table.row().height(splashTexture.getHeight());
        table.add(splashImage).center();
        stage.addActor(table);

        // Lanza la carga de recursos
        
        */
        ResourceManager.loadAllResources();
    }

    @Override
    public void resize(int width, int height) {

       
        //stage.getCamera().viewportHeight = height;
        //stage.getCamera().viewportWidth = width;
        stage.getViewport().update(width, height, true);
        //stage.getCamera().update();

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        splashTexture.dispose();
        stage.dispose();
    }
}
