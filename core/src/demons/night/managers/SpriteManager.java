package demons.night.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import demons.night.DemonsGame;
import demons.night.characters.*;
import demons.night.characters.Duende;
import demons.night.characters.Player;
import demons.night.screens.GameOverScreen;
import demons.night.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import static java.lang.Math.abs;


public class SpriteManager implements ControllerListener{

    public DemonsGame game;

    private Batch batch;
    private BitmapFont font;
    public OrthographicCamera camera;
    public static float CAMERA_OFFSET = 0;
    OrthogonalTiledMapRenderer mapRenderer;
    public static float LIMITE_IZQ = 0;
    public static float LIMITE_DCHO = 0;
    Touchpad touchpad;
    ImageButton saltar;
    ImageButton golpear;
    ImageButton pause;
    public boolean pause_clicked = false;
    GameScreen gs;

    public Player player;
    // Música de fondo que suena actualmente
    public Music music;

    public LevelManager levelManager;

    enum PlayerState {
    	IDLE, LEFT, RIGHT, UP, DOWN
    }
    private PlayerState playerState;


    public SpriteManager(DemonsGame game, AndroidControlsManager controladorAndroid) {
        this.game = game;
        player = new Player(this);
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            this.touchpad = controladorAndroid.getJoystick();
            this.saltar = controladorAndroid.getSaltar();
            this.golpear = controladorAndroid.getGolpear();
            this.pause = controladorAndroid.getPause();
        }


        LevelManager.PRIMERA_Y = 0;
        LevelManager.PRIMERA_X = 0;

        levelManager = new LevelManager();

        font = new BitmapFont(Gdx.files.internal("ui/fuente.fnt"));

        // Crea una cámara y muestra 30x20 unidades del mundo
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1 / 2f;

        camera.setToOrtho(false, Gdx.graphics.getWidth()/16, Gdx.graphics.getHeight()/16);
        camera.update();
        CAMERA_OFFSET = 0;

        // Activa face culling
        Gdx.gl.glCullFace(GL20.GL_CULL_FACE);

        loadCurrentLevel();
        //AndroidControllers.addListener(this);
        Controllers.addListener(this);
        playerState = PlayerState.IDLE;
    }

    /**
     * Carga el nivel actual
     */
    public void loadCurrentLevel() {

        // Crea y carga el mapa
        if(levelManager.loadCurrentMap()) {
            mapRenderer = new OrthogonalTiledMapRenderer(levelManager.map);
            batch = mapRenderer.getBatch();

            camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                camera.zoom = 0.36f;
            } else {
                camera.zoom = 1 / 2f;
            }

            camera.position.x = 0;
            camera.position.y = 0;

            // Crea el jugador y lo posiciona al inicio de la pantalla
            player = new Player(this);
            // posición inicial del jugador
            for (MapObject object : levelManager.map.getLayers().get("objects").getObjects()) {
                TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                if (object.getName() != null && object.getName().equals("personaje")) {
                    player.position.set(tileObject.getX(), tileObject.getY());
                    System.out.println("Coloco al jugador en la posicion: " + tileObject.getX() + " , " + tileObject.getY());
                }
            }


            if (ResourceManager.sonido_activado) {
                // Música durante la partida
                ResourceManager.assets.load("sounds/" + levelManager.getCurrentLevelName() + ".mp3", Music.class);
                music = ResourceManager.getMusic("sounds/" + levelManager.getCurrentLevelName() + ".mp3");
                music.setVolume(0.5f);
                music.setLooping(true);
                music.play();
            }
        }else{
            int coins = levelManager.getTotalCoins() + levelManager.getCurrentCoins();

            game.setScreen(new GameOverScreen(game, coins, true));


        }
    }

    public void passCurrentLevel() {

        levelManager.passCurrentLevel();
        mapRenderer.dispose();
        LevelManager.PRIMERA_X = 0;
        LevelManager.PRIMERA_Y = 0;
        loadCurrentLevel();
    }
	
    public void update(float dt) {

        if(player != null) {
            if (!((player.state == Player.State.ATTACKING_LEFT || player.state == Player.State.ATTACKING_RIGHT)
                    && player.stateTime < 0.4)) {
                if (Gdx.app.getType() == Application.ApplicationType.Android) {
                    handleInputAndroid();
                } else {
                    handleInput();
                }

            }
            // Comprobar entrada de usuario (teclado, pantalla, ratón, . . .)


            if (game.paused)
                return;

            // Actualizar jugador
            player.update(dt);

            // Comprueba colisiones del jugador con elementos móviles del juego
            checkCollisions();

            // Comprueba el estado de los enemigos
            for (Enemy enemy : levelManager.enemies) {
                // Si la cámara no los enfoca no se actualizan
                if (!camera.frustum.pointInFrustum(new Vector3(enemy.position.x, enemy.position.y, 0)))
                    continue;

                if (enemy.isAlive)
                    enemy.update(dt);
                else
                    levelManager.enemies.removeValue(enemy, true);
            }

            for (EnemigoVolador murcielago : levelManager.murcielagos) {
                // Si la cámara no los enfoca no se actualizan
                if (!camera.frustum.pointInFrustum(new Vector3(murcielago.position.x, murcielago.position.y, 0)))
                    continue;

                if (murcielago.isAlive)
                    murcielago.update(dt);
                else
                    levelManager.murcielagos.removeValue(murcielago, true);
            }

            for (Duende duende : levelManager.duendes) {
                // Si la cámara no los enfoca no se actualizan
                if (!camera.frustum.pointInFrustum(new Vector3(duende.position.x, duende.position.y, 0)))
                    continue;

                if (duende.isAlive)
                    duende.update(dt);
                else
                    levelManager.duendes.removeValue(duende, true);
            }

        }
    }

    public void draw() {

        iniciarCamara();

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0, 1, 2, 3, 4, 5});

        // Inicia renderizado del juego
        batch.begin();
        // Pinta al jugador
        player.render(batch);
        //float delay = 1; // seconds
        
        if(player.state == Player.State.ATTACKING_RIGHT || player.state == Player.State.ATTACKING_LEFT){
            player.ataque.render(batch);
        }
        
        for (Enemy enemy : levelManager.enemies)
            enemy.render(batch);
        for (EnemigoVolador murcielago : levelManager.murcielagos)
            murcielago.render(batch);
        for (Gem gem : levelManager.gems)
            gem.render(batch);
        for (Duende duende : levelManager.duendes)
            duende.render(batch);
        
        float altura_hud = camera.viewportHeight * 0.05f;
        
        //System.out.println("Coodenada x de gema: " + (camera.position.x - 10) +  " coordenada y: " + (camera.position.y - 135 - 12));
        // Pinta la información en partida relativa al jugador
        font.getData().setScale(0.7f);
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            altura_hud -= 40;
        }
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("coin"), camera.position.x - 190, altura_hud - 12);
        font.draw(batch, " X" + levelManager.currentCoins, camera.position.x - 186, altura_hud);
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("life"), camera.position.x + - 150, altura_hud - 10);
        font.draw(batch, " X" + levelManager.currentLives, camera.position.x - 146, altura_hud);
        font.draw(batch, "Nivel " + levelManager.currentLevel, camera.position.x - 255, altura_hud);

        batch.end();
    }
	
    public void iniciarCamara(){
        
        if(LevelManager.PRIMERA_X == 0 && LevelManager.PRIMERA_Y == 0){
/*
            if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
                camera.viewportWidth = Gdx.graphics.getWidth()/2;
                camera.viewportHeight = Gdx.graphics.getHeight()/2;
            }else{*/
                camera.viewportWidth = Gdx.graphics.getWidth();
                camera.viewportHeight = Gdx.graphics.getHeight();
            //}

            camera.position.x = 0;
            camera.position.y = 0;
            
            System.out.println("Recoloco la camara");
            float scaledViewportWidthHalfExtent = camera.viewportWidth * 0.52f * 0.5f;
            float scaledViewportHeightHalfExtent = camera.viewportHeight  * 0.52f * 0.5f;
            System.out.println("camera viewport = " + camera.viewportWidth + " , " + camera.viewportHeight);
            // Horizontal
            if (camera.position.x < scaledViewportWidthHalfExtent){
                camera.position.x = scaledViewportWidthHalfExtent;
            }else if (camera.position.x > Gdx.graphics.getWidth() - scaledViewportWidthHalfExtent)
                camera.position.x = Gdx.graphics.getWidth() - scaledViewportWidthHalfExtent;

            // Vertical
            if (camera.position.y < scaledViewportHeightHalfExtent) {
                camera.position.y = scaledViewportHeightHalfExtent;
                if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                    camera.position.y *=0.65f;
                    camera.position.x *= 0.65f;
                }

            }else if (camera.position.y > Gdx.graphics.getHeight() - scaledViewportHeightHalfExtent)
                camera.position.y = Gdx.graphics.getHeight() - scaledViewportHeightHalfExtent;
        

            LevelManager.PRIMERA_X = camera.position.x;
            LevelManager.PRIMERA_Y = camera.position.y;
            System.out.println("Primera x = " + LevelManager.PRIMERA_X + " Primera y = " + LevelManager.PRIMERA_Y);
        }else{
            
        TiledMapTileLayer layer = (TiledMapTileLayer) mapRenderer.getMap().getLayers().get(0);
        float ancho_mapa = layer.getWidth() * 16;
        
        if(player.position.x > ancho_mapa / 6.9 && player.position.x < ancho_mapa * 0.85){
            //System.out.println("Entro");
            if(abs(player.position.x - camera.position.x) > 100){
               // System.out.println("Jugador alejado de la camara...");
                Vector3 v3 = null;
                if(player.position.x < camera.position.x &&(player.state == player.state.RUNNING_LEFT || player.state == player.state.IDLE_LEFT)){
                    //System.out.println("Acercando camara...");
                    if((camera.position.x - (player.position.x * 0.05f) ) < LevelManager.PRIMERA_X){
                        v3 = new Vector3(LevelManager.PRIMERA_X, camera.position.y, 0);
                    }else{
                        v3 = new Vector3(camera.position.x - (player.position.x * 0.05f), camera.position.y, 0);
                    }
                    
                }else if (player.position.x > camera.position.x && (player.state == player.state.RUNNING_RIGHT || player.state == player.state.IDLE_RIGHT)){
                    //System.out.println("Alejando camara...");
                    
                    v3 = new Vector3(camera.position.x + (player.position.x *0.04f), camera.position.y, 0);

                }
                if(v3 != null)
                    camera.position.lerp(v3, 0.07f);

                camera.update();
                //System.out.println("Camera position x = " + camera.position.x);
            }else if(player.position.x <= ancho_mapa / 5 && (player.state == Player.State.RUNNING_LEFT || player.state == Player.State.IDLE_LEFT)){
                Vector3 v3 = new Vector3(LevelManager.PRIMERA_X, camera.position.y, 0);
                camera.position.lerp(v3, 0.05f);
                //System.out.println("Volviendo al principio...");
            }
        }
        }

    }
	/**
	 * Comprueba las colisiones del jugador con los elementos móviles del juego
	 * Enemigos e items
	 */
	private void checkCollisions() {
		Rectangle playerRect = new Rectangle();
		playerRect.set(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);
		
		// Comprueba si el enemigo ha chocado contra algún enemigo
		for (EnemigoVolador murcielago : levelManager.murcielagos) {
                    Rectangle enemyRect = new Rectangle();
                    enemyRect.set(murcielago.position.x, murcielago.position.y, EnemigoVolador.WIDTH, EnemigoVolador.HEIGHT);

                    if (enemyRect.overlaps(playerRect)) {

                        // Si el jugador está por encima elimina el enemigo
                        if (player.position.y > (murcielago.position.y + 5)) {
                            if(ResourceManager.sonido_activado)
                                ResourceManager.getSound("sounds/murcielago.mp3").play();
                                levelManager.murcielagos.removeValue(murcielago, true);

                                // El jugador rebota
                                player.jump(false);
                        }
                        // Si está al mismo nivel o por debajo se pierde una vida
                        else {
                            player.velocity.x = player.velocity.y = 0;
                            player.die();
                        }
                    }
		}
                
                for (Enemy enemy : levelManager.enemies) {
                    Rectangle enemyRect = new Rectangle();
                    enemyRect.set(enemy.position.x, enemy.position.y, Enemy.WIDTH, Enemy.HEIGHT);
                    

                    if (enemyRect.overlaps(playerRect)) {
                        
                        if (ResourceManager.dificultad.equals("facil") && player.position.y > (enemy.position.y + 5)) {
                            if(ResourceManager.sonido_activado)
                                ResourceManager.getSound("sounds/golem.mp3").play();
                                levelManager.enemies.removeValue(enemy, true);

                                // El jugador rebota
                                player.jump(false);
                        }else{

                            player.velocity.x = player.velocity.y = 0;
                            player.die();
                        }

                    }
		}
                
                for (Duende duende : levelManager.duendes) {
                    Rectangle enemyRect = new Rectangle();
                    enemyRect.set(duende.position.x, duende.position.y, duende.WIDTH, duende.HEIGHT);

                    if (enemyRect.overlaps(playerRect)) {

                        if (player.position.y > (duende.position.y + 5)) {
                            if(ResourceManager.sonido_activado)
                                ResourceManager.getSound("sounds/duende.mp3").play();
                                levelManager.duendes.removeValue(duende, true);

                                // El jugador rebota
                                player.jump(false);
                        }else{
                            player.velocity.x = player.velocity.y = 0;
                            player.die();
                        }
                        

                    }
		}
		
		// Comprueba si el jugador recoge algún item de la pantalla

                
                for (Gem gem : levelManager.gems) {
                    Rectangle itemRect = new Rectangle();
                    itemRect.set(gem.position.x, gem.position.y, Gem.WIDTH, Gem.HEIGHT);

                    if (itemRect.overlaps(playerRect)) {
                        if(ResourceManager.sonido_activado)
                            ResourceManager.getSound("sounds/coin.wav").play();
                            levelManager.gems.removeValue(gem, true);
                            levelManager.currentCoins++;
                    }
		}

	}
	
	/**
	 * Controla la entrada de teclado del usuario
	 */
	private void handleInput() {
            
		
        // Se pulsa la teclad derecha
        if ((Gdx.input.isKeyPressed(Keys.RIGHT)) || (playerState == PlayerState.RIGHT)) {
                player.isRunning = true;
                player.velocity.x = Player.WALKING_SPEED;
                player.state = Player.State.RUNNING_RIGHT;

                if ((!player.isJumping))
                        player.isRunning = true;
        }
        // Se pulsa la tecla izquierda
        else if ((Gdx.input.isKeyPressed(Keys.LEFT)) || (playerState == PlayerState.LEFT)) {
                player.isRunning = true;
                player.velocity.x = -Player.WALKING_SPEED;
                player.state = Player.State.RUNNING_LEFT;

                if ((!player.isJumping))
                        player.isRunning = true;
        }
        else if ((Gdx.input.isKeyPressed(Keys.X)) || (Gdx.input.isKeyPressed(Keys.NUMPAD_0))) {
			player.velocity.x = 0;
            if(player.state == Player.State.IDLE_LEFT || player.state == Player.State.RUNNING_LEFT)
                player.state = Player.State.ATTACKING_LEFT;
            else if(player.state == Player.State.IDLE_RIGHT || player.state == Player.State.RUNNING_RIGHT)
                player.state = Player.State.ATTACKING_RIGHT;

			player.attack();
                        
		}
        // No se pulsa ninguna tecla
        else {

                if (player.isRunning)
                        if (player.state == Player.State.RUNNING_LEFT)
                                player.state = Player.State.IDLE_LEFT;
                        else
                                player.state = Player.State.IDLE_RIGHT;

                player.isRunning = false;
                player.velocity.x = 0;
        }

        // Se pulsa la tecla CONTROL IZQ (salto)
        if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.UP)) {

                player.tryJump();
        }

        // Controla los límites (por debajo) de la pantalla, cuando cae el personaje
        if (player.position.y < 0) {
            player.die();

        }

        // Controla el límite izquierdo de la pantalla
        if (player.position.x <= 0)
                player.position.x = 0;
	}

    private void handleInputAndroid() {

        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){

                if(!pause_clicked) {
                    pause_clicked = true;
                }
            }
        });


	    saltar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                player.tryJump();
            }
        });

	    golpear.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){

                if(player.state == Player.State.IDLE_LEFT || player.state == Player.State.RUNNING_LEFT) {
                    player.state = Player.State.ATTACKING_LEFT;
                    player.velocity.x = 0;
                    player.attack();
                }else if(player.state == Player.State.IDLE_RIGHT || player.state == Player.State.RUNNING_RIGHT) {
                    player.state = Player.State.ATTACKING_RIGHT;
                    player.velocity.x = 0;
                    player.attack();
                }

            }
        });

        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // This is run when anything is changed on this actor.
                float deltaX = ((Touchpad) actor).getKnobPercentX();
                float deltaY = ((Touchpad) actor).getKnobPercentY();
                if(deltaX != 0 || deltaY != 0){
                   //System.out.println("Detecto toque en el joystick: " + deltaX + "," + deltaY);
                    if(deltaX > 0.65){
                        player.isRunning = true;
                        player.velocity.x = Player.WALKING_SPEED;
                        player.state = Player.State.RUNNING_RIGHT;

                        if ((!player.isJumping))
                            player.isRunning = true;
                    }else if(deltaX < -0.65){
                        player.isRunning = true;
                        player.velocity.x = -Player.WALKING_SPEED;
                        player.state = Player.State.RUNNING_LEFT;

                        if ((!player.isJumping))
                            player.isRunning = true;
                    }
                    /*
                    if(deltaY > 0.85){
                        player.tryJump();
                    }
                     */
                }else{
                    if (player.isRunning)
                        if (player.state == Player.State.RUNNING_LEFT)
                            player.state = Player.State.IDLE_LEFT;
                        else
                            player.state = Player.State.IDLE_RIGHT;

                    player.isRunning = false;
                    player.velocity.x = 0;
                }


            }
        });
        // Controla los límites (por debajo) de la pantalla, cuando cae el personaje
        if (player.position.y < 0) {
            player.die();

        }

        // Controla el límite izquierdo de la pantalla
        if (player.position.x <= 0)
            player.position.x = 0;
    }

    public void resize(int width, int height) {

        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    public void resetearCamara(){
            
        camera.position.x = LevelManager.PRIMERA_X;
        camera.position.y = LevelManager.PRIMERA_Y;
        camera.update();
    }

    public void pararMusica(){
	    if(music != null){
	        music.stop();
        }
    }
    
    /**
     * Libera los recursos utilizados por el controlador
     * Se invoca cuando se termina una partida y volvemos al
     * menú de juego
     */
    public void dispose() {

        if(ResourceManager.sonido_activado){
            music.stop();
            music.dispose();
        }
        font.dispose();
        batch.dispose();

        levelManager.clearCharactersCurrentLevel();
    }

	@Override
	public void connected(Controller controller) {

	}

	@Override
	public void disconnected(Controller controller) {

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {

		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {

		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}
        
        
}