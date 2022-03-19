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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import demons.night.DemonsGame;
import demons.night.managers.ResourceManager;
import demons.night.screens.GameScreen;
import demons.night.screens.MainMenuScreen;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alici
 */
public class OptionsScreen implements Screen {

    DemonsGame game;
    private Table table;
    Stage stage;
    Label title;
    Label about_label;
    private Texture boton_mute;
    private Texture boton_unmute;
    private Texture boton_atras;    
    private Texture background;

    
    private TextureRegionDrawable muteDrawable;
    private TextureRegionDrawable unmuteDrawable;
    private TextureRegionDrawable atrasDrawable;
    
    TextButton normal;
            
    TextButton facil;
    private ImageButton muteButton;
    //private ImageButton optionsButton;
    private ImageButton atrasButton;

    public OptionsScreen(DemonsGame game) {
        this.game = game;
        background = new Texture(Gdx.files.internal("ui/menuopciones.png"));

        String coletilla = "";
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            coletilla = "_android";
        }

        boton_mute = new Texture(Gdx.files.internal("ui/botonmute" + coletilla + ".png"));
        boton_unmute = new Texture(Gdx.files.internal("ui/botonunmute" + coletilla + ".png"));
        boton_atras = new Texture(Gdx.files.internal("ui/botonatras" + coletilla + ".png"));
    }

    @Override
    public void show() {
        
        TextureRegion mute = new TextureRegion(boton_mute);
        TextureRegion unmute = new TextureRegion(boton_unmute);
        TextureRegion atras = new TextureRegion(boton_atras);
        
        muteDrawable = new TextureRegionDrawable(mute);
        unmuteDrawable = new TextureRegionDrawable(unmute);
        atrasDrawable = new TextureRegionDrawable(atras);

        stage = new Stage();

        table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        /*
        Label title = new Label("JUMPER2DX", game.getSkin());
        title.setFontScale(2.5f);
        */

        muteButton = new ImageButton(muteDrawable, muteDrawable, unmuteDrawable);
        muteButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(ResourceManager.sonido_activado){
                    muteButton.setChecked(true);
                    ResourceManager.sonido_activado = false;
                    ResourceManager.opening.pause();
                    
                }else{
                    muteButton.setChecked(false);
                    ResourceManager.sonido_activado = true;
                    if(ResourceManager.sonido_activado)
                        ResourceManager.opening.play();
                }
                /*
                ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                dispose();
                game.setScreen(gameScreen);*/
            }
        });
        atrasButton = new ImageButton(atrasDrawable);
        atrasButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                atrasButton.setChecked(true);
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                //gameScreen.dispose();
                game.setScreen(new MainMenuScreen(game));

            }
        });
        facil = new TextButton("Facil", game.getSkin());
        facil.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                facil.setLayoutEnabled(false);
                normal.setLayoutEnabled(true);
                ResourceManager.dificultad = "facil";

            }
        });
        
        normal = new TextButton("Normal", game.getSkin());
        normal.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                facil.setLayoutEnabled(true);
                normal.setLayoutEnabled(false);
                ResourceManager.dificultad = "normal";

            }
        });
        

        createTable();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }
    
    public void createTable(){

        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            table.row().height(110);
            table.add(title).center().pad(80f);
            table.row();
            //table.add(title).center().padLeft(150f);
            table.add(muteButton).padRight(-1200f).padTop(120f);
            table.row();
            table.add(atrasButton).align(Align.bottomLeft).padLeft(-150f).padTop(-60f);
            table.add(facil).center().align(Align.bottom).width(320).height(50).padTop(60f);
            table.add(normal).center().align(Align.bottom).width(320).height(50).padTop(60f).padRight(-250);
        }else {

            table.row().height(110);
            table.add(title).center().pad(80f);
            table.row();
            //table.add(title).center().padLeft(150f);
            table.add(muteButton).padRight(-780f).padTop(30f);
            table.row();
            table.add(atrasButton).align(Align.bottomLeft).padLeft(50f).padTop(60f);
            table.add(facil).center().align(Align.bottom).width(170).padTop(60f);
            table.add(normal).center().align(Align.bottom).width(170).padTop(60f);
        }
        
    }

    @Override
    public void render(float dt) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Pinta el menú
        stage.act(dt);
        stage.draw();
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

