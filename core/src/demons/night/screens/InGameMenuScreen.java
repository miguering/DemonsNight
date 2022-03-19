package demons.night.screens;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import demons.night.DemonsGame;
import demons.night.managers.ResourceManager;


public class InGameMenuScreen implements Screen {

    DemonsGame game;
    GameScreen gameScreen;
    private Table table;
    Stage stage;
    Label title;
    Label about_label;
    private Texture boton_jugar;
    private Texture boton_opciones;
    private Texture boton_salir;

    private Texture background;

    
    private TextureRegionDrawable jugarDrawable;
    private TextureRegionDrawable opcionesDrawable;
    private TextureRegionDrawable salirDrawable;
    
    private ImageButton playButton;
    private ImageButton optionsButton;
    private ImageButton exitButton;

    public InGameMenuScreen(DemonsGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        background = new Texture(Gdx.files.internal("ui/menu.png"));

        String coletilla = "";
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            coletilla = "_android";
        }

        boton_jugar = new Texture(Gdx.files.internal("ui/botonreanudar" + coletilla + ".png"));
        boton_opciones = new Texture(Gdx.files.internal("ui/botonvolveralmenu" + coletilla + ".png"));
        boton_salir = new Texture(Gdx.files.internal("ui/botonsalir" + coletilla + ".png"));

    }

    @Override
    public void show() {
        
        TextureRegion jugar = new TextureRegion(boton_jugar);
        TextureRegion opciones = new TextureRegion(boton_opciones);
        TextureRegion salir = new TextureRegion(boton_salir);

        
        jugarDrawable = new TextureRegionDrawable(jugar);
        opcionesDrawable = new TextureRegionDrawable(opciones);
        salirDrawable = new TextureRegionDrawable(salir);

        stage = new Stage();

        table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        /*
        Label title = new Label("JUMPER2DX", game.getSkin());
        title.setFontScale(2.5f);
        */

        playButton = new ImageButton(jugarDrawable);
        playButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                dispose();
                game.setScreen(gameScreen);
            }
        });
        optionsButton = new ImageButton(opcionesDrawable);
        optionsButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                optionsButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                gameScreen.dispose();
                game.setScreen(new MainMenuScreen(game));
                ResourceManager.playOpening();

            }
        });
        exitButton = new ImageButton(salirDrawable);
        exitButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                exitButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                dispose();
                System.exit(0);
            }
        });
        

        createTable();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }
    
    public void createTable(){

        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            playButton.setWidth(playButton.getWidth()*2);
            playButton.setHeight(playButton.getHeight()*2);
            optionsButton.setWidth(optionsButton.getWidth()*2);
            optionsButton.setHeight(optionsButton.getHeight()*2);
            exitButton.setWidth(exitButton.getWidth()*2);
            exitButton.setHeight(exitButton.getHeight()*2);
        }
        
        table.row().height(150);
        table.add(title).center().pad(35f);
        table.row();
        table.add(playButton).center().pad(5f);
        table.row();
        table.add(optionsButton).center().pad(5f);
        table.row();
        table.add(exitButton).center().pad(5f);
        table.row();
        table.add(about_label).center().pad(55f);
        
        
    }

    @Override
    public void render(float dt) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Pinta el menú
        stage.act(dt);
        stage.draw();
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                if(playButton.isChecked()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainMenuScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    playButton.setChecked(false);
                }
                if(optionsButton.isChecked()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainMenuScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    optionsButton.setChecked(false);
                }
                if(exitButton.isChecked()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainMenuScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    exitButton.setChecked(false);
                }
            }
        }).start();*/
    }

    @Override
    public void dispose() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        /*
        stage.getCamera().viewportHeight = height;
        stage.getCamera().viewportWidth = width;
        stage.getCamera().update();
        */
    }

    @Override
    public void resume() {
    }
}
