package demons.night.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import demons.night.DemonsGame;
import demons.night.managers.ResourceManager;

public class GameOverScreen implements Screen {
    DemonsGame game;
    private Texture texturaBoton;
    private BitmapFont fuente;
    private ImageButton boton;
    private Texture background;
    Stage stage;
    Table table;
    int puntos;
    Label fin1;
    Label score;
    Label labelPuntos;
    Label.LabelStyle estilo;
    ArrayList<Score> puntuaciones;
    boolean mostrar_ranking;
    private boolean done;
    String cadena_puntos;
    TextField nameTextField;
    TextButton quitButton;


    public GameOverScreen(DemonsGame game, int puntos, boolean victoria) {
        this.game = game;
        this.puntos = puntos;
        this.mostrar_ranking = victoria;
        puntuaciones = new ArrayList<>();

        fuente = new BitmapFont(Gdx.files.internal("ui/fuente.fnt"));
        background = new Texture(Gdx.files.internal("ui/menuranking.png"));

        estilo = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("ui/fuente_big.fnt")), Color.WHITE);
        String coletilla = "";
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            coletilla = "_android";
        }

        texturaBoton = new Texture(Gdx.files.internal("ui/botonvolveralmenu" + coletilla + ".png"));

    }

    private void cargarPuntuaciones(){
        cadena_puntos = "";
        
        if(puntuaciones.size() > 0){
            
            boolean ordenado = false;
            Score temp;
            while(!ordenado) {
                ordenado = true;
                for (int i = 0; i < puntuaciones.size() - 1; i++) {
                    if (puntuaciones.get(i).puntuacion < puntuaciones.get(i+1).puntuacion) {
                        temp = puntuaciones.get(i);
                        puntuaciones.set(i, puntuaciones.get(i+1));
                        puntuaciones.set(i+1, temp);
                        ordenado = false;
                    }
                }
            }
            int tam = 3;
            if(puntuaciones.size() < 3){
                tam = puntuaciones.size();
            }
            for(int i = 0 ; i < tam; i++){
                cadena_puntos += i+1 + " - " + puntuaciones.get(i).nombre + " - " + puntuaciones.get(i).puntuacion + "\n";
            }
        }else{
            cadena_puntos = "Nadie ha registrado su record!";
        }

    }
    @Override
    public void show() {

        stage = new Stage();
        table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();
        cargarXML();
        cargarPuntuaciones();
        String cadena;


        if(this.mostrar_ranking){
            cadena = "Juego completado!" + "\nGemas conseguidas: " + this.puntos;
        }else{
            cadena = "Fin de la partida - Has perdido " + "\nGemas conseguidas: " + this.puntos;
        }


        fin1 = new Label(cadena  + "\n Ranking: \n" + cadena_puntos, estilo);

        TextureRegion regionBoton = new TextureRegion(texturaBoton);



        table.row().height(50);
        table.add(fin1).center().pad(35f).padLeft(160).padTop(80);
        table.row().height(180);


        boton = new ImageButton(new TextureRegionDrawable(regionBoton));
        boton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/sonidomenu.mp3").play();
                //gameScreen.dispose();
                game.setScreen(new MainMenuScreen(game));
                }
        });

        table.setBackground(new TextureRegionDrawable(new TextureRegion(background)));


        if (!done && puntos > 0 && entraEnRanking()) {
            nameTextField = new TextField("Introduce tu nombre", game.getSkin());
            
            nameTextField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                nameTextField.setText("");
            }
            });

            quitButton = new TextButton("OK", game.getSkin());
            quitButton.addListener(new ClickListener() {
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if(!nameTextField.getText().equals("") && !nameTextField.getText().equals("Introduce tu nombre")){
                        addScore(nameTextField.getText(), puntos);
                        guardarXML();
                    }
                    stage.clear();
                    done = true;
                    show();
                }
            });

            table.row().height(20);
            table.add(nameTextField).center().height(50f).width(650f).pad(55f).padBottom(-100);
            table.row().height(50);
            table.add(quitButton).center().height(50f).width(300f).pad(5f).padBottom(-100);

        }

        table.row();
        table.add(boton).center().align(Align.bottomLeft).pad(5f).padLeft(-120);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private boolean entraEnRanking(){
        boolean entra = false;

        if(puntuaciones.size() < 3){
            return true;
        }else {
            for (int i = 0; i < 2; i++) {
                if (puntuaciones.get(i).puntuacion < puntos) {
                    return true;

                }
            }
        }
        return entra;
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

    }

    @Override
    public void resume() {
    }


    public void addScore(String name, int score) {

        puntuaciones.add(new Score(name, score));
        //mostrarRankingEnTabla();
    }

    public void guardarXML() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document documento = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation dom = builder.getDOMImplementation();
            documento = dom.createDocument(null, "xml", null);

            Element raiz = documento.createElement("Puntuaciones");
            documento.getDocumentElement().appendChild(raiz);

            Element nodoJugador = null, nodoNombre = null, nodoPuntuacion = null;
            Text texto = null;

            for (Score e : puntuaciones) {
                nodoJugador = documento.createElement("Jugador");
                raiz.appendChild(nodoJugador);

                nodoPuntuacion = documento.createElement("puntuacion");
                nodoJugador.appendChild(nodoPuntuacion);

                texto = documento.createTextNode(String.valueOf(e.puntuacion));
                nodoPuntuacion.appendChild(texto);

                nodoNombre = documento.createElement("nombre");
                nodoJugador.appendChild(nodoNombre);

                texto = documento.createTextNode(e.nombre);
                nodoNombre.appendChild(texto);
            }

            Source source = new DOMSource(documento);
            Result resultado = new StreamResult(new File("scores.xml"));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, resultado);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
    }

    public void cargarXML(){

        puntuaciones.clear();
        String nombre;
        int puntuacion;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document documento = null;

        File fichero = new File("scores.xml");

        if(fichero.exists()){

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                documento = builder.parse(fichero);

                NodeList jugadores = documento.getElementsByTagName("Jugador");
                for (int i = 0; i < jugadores.getLength(); i++) {
                    Node jugador = jugadores.item(i);
                    Element elemento = (Element) jugador;
                    nombre = String.valueOf(elemento.getElementsByTagName("nombre").item(0).
                            getChildNodes().item(0).getNodeValue());
                    puntuacion = Integer.valueOf(elemento.getElementsByTagName("puntuacion").item(0).
                            getChildNodes().item(0).getNodeValue());

                    puntuaciones.add(new Score(nombre, puntuacion));
                }

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (SAXException saxe) {
                saxe.printStackTrace();
            }
        }
    }

    public class Score{
        public String nombre;
        public int puntuacion;

        public Score(String nombre, int puntuacion){
            this.nombre = nombre;
            this.puntuacion = puntuacion;
        }

    }
}
