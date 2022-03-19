package demons.night.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import demons.night.screens.*;


public class MainMenuScreen implements Screen {
	
    final DemonsGame game;
    private Stage stage;
    private Table table;
    private Label title;
    private Texture background;

    private Texture boton_jugar;
    private Texture boton_opciones;
    private Texture boton_salir;
    
    private Sound sonido;
    
    private TextureRegionDrawable jugarDrawable;
    private TextureRegionDrawable opcionesDrawable;
    private TextureRegionDrawable salirDrawable;
    
    private ImageButton playButton;
    private ImageButton optionsButton;
    private ImageButton exitButton;
    private Label aboutLabel;
	
    public MainMenuScreen(DemonsGame game) {
        this.game = game;
        background = new Texture(Gdx.files.internal("ui/menu.png"));

        String coletilla = "";
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            coletilla = "_android";
        }

        boton_jugar = new Texture(Gdx.files.internal("ui/botonjugar" + coletilla + ".png"));
        boton_opciones = new Texture(Gdx.files.internal("ui/botonopciones" + coletilla + ".png"));
        boton_salir = new Texture(Gdx.files.internal("ui/botonsalir" + coletilla + ".png"));
        sonido = ResourceManager.getSound("sounds/sonidomenu.mp3");

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


        playButton = new ImageButton(jugarDrawable);

        playButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    sonido.play();
                dispose();
                ResourceManager.stopOpening();
                game.setScreen(new GameScreen(game));

                
            }
        });
        optionsButton = new ImageButton(opcionesDrawable);
        optionsButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                optionsButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    sonido.play();
                
                optionsButton.setChecked(false);
                game.setScreen(new OptionsScreen(game));

            }
        });
        exitButton = new ImageButton(salirDrawable);
        exitButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                exitButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    sonido.play();
                ResourceManager.stopOpening();
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

            table.row().height(150);
            table.add(title).center().pad(35f);
            table.row();
            table.add(playButton).center().pad(5f);
            table.row();
            table.add(optionsButton).center().pad(5f);
            table.row();
            table.add(exitButton).center().pad(5f);
            table.row();
            table.add(aboutLabel).center().pad(55f);
    }

	@Override
	public void render(float delta) {
		
            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            stage.act();
            stage.draw();

	}

	@Override
	public void resize(int width, int height) {

            stage.getViewport().update(width, height);
	}

	@Override
	public void hide() {
	}

//	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}