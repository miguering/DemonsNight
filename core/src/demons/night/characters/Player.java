package demons.night.characters;
import com.badlogic.gdx.graphics.g2d.*;

import demons.night.screens.GameOverScreen;
import demons.night.screens.MainMenuScreen;
import demons.night.managers.SpriteManager;
import demons.night.managers.ResourceManager;
import demons.night.managers.TiledMapManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


public class Player {

	public Vector2 position;
	public State state;
	public Vector2 velocity;
	public boolean canJump;
	public boolean isRunning;
	public boolean isJumping;
	public AtaqueEspada ataque;

	
	// Anota que el personaje ha perdido una vida
	private boolean dead;
	private boolean levelCleared;
	
	private TextureRegion idleLeft;
	private TextureRegion idleRight;
	private TextureRegion currentFrame;
	public float stateTime;
	
	private Animation<TextureRegion> rightAnimation;
	private Animation<TextureRegion> leftAnimation;
        private Animation<TextureRegion> rightJump;
	private Animation<TextureRegion> leftJump;
        private Animation<TextureRegion> rightAttack;
	private Animation<TextureRegion> leftAttack;
	
	private SpriteManager spriteManager;
	
	public enum State {
		IDLE_LEFT, IDLE_RIGHT, RUNNING_LEFT, RUNNING_RIGHT, ATTACKING_LEFT, ATTACKING_RIGHT
	}
        public State lastState;
	
	private Array<Rectangle> tiles = new Array<Rectangle>();
	// Pool de rectángulos (mejora la eficiencia si se trabaja con muchos)
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};
	
	// Parámetros de movimiento del personaje
	public static float WALKING_SPEED = 2.0f;
	public static float JUMPING_SPEED = 5.0f;
	public static float MAX_JUMPING = 60f;
	public static float GRAVITY = 9f;
	public static float WIDTH = 18;
	public static float HEIGHT = 28;
	
	public Player(SpriteManager spriteManager) {
		
            position = new Vector2();
            velocity = new Vector2();
            state = State.IDLE_RIGHT;
            this.spriteManager = spriteManager;
            dead = false;
            ataque = null;

            // Posiciones estáticas del personaje para izquierda y derecha en parado y salto
            TextureAtlas atlas = ResourceManager.assets.get("characters/characters.pack", TextureAtlas.class);
            idleLeft = atlas.findRegion("tanji_idle_left");
            idleRight = atlas.findRegion("tanji_idle_right");

            // Crea la animación para correr hacia la derecha
            rightAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_run_right"));

            // Crea la animación para correr hacia la izquierda
            leftAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_run_left"));

            // Crea la animación para correr hacia la derecha
            rightJump = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_jump_right"));

            // Crea la animación para correr hacia la izquierda
            leftJump = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_jump_left"));

            leftAttack = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_attack_left"));
            rightAttack = new Animation<TextureRegion>(0.15f, atlas.findRegions("tanji_attack_right"));
	}
	
	/**
	 * Pinta la animación en pantalla
	 */
	public void render(Batch spriteBatch) {
		
            if(lastState != state){
                stateTime = 0;
            }else{
                stateTime += Gdx.graphics.getDeltaTime();
            }
		
		
		// Calcula el frame del personaje que se debe pintar (o qué animación)
		switch (state) {
			case IDLE_LEFT:
                            if (isJumping){
                                currentFrame = leftJump.getKeyFrame(stateTime, false);
                            }else{
                                currentFrame = idleLeft;             
                            }
				break;
			case IDLE_RIGHT:
                            if (isJumping){
                                currentFrame = rightJump.getKeyFrame(stateTime, false);
                            }else{
                                currentFrame = idleRight;             
                            }
				break;
			case RUNNING_LEFT:
				if (isJumping){
					currentFrame = leftJump.getKeyFrame(stateTime, false);
                                }else{
					currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                                }
				break;
			case RUNNING_RIGHT:
				if (isJumping){
					currentFrame = rightJump.getKeyFrame(stateTime, false);
                                }else{
					currentFrame = rightAnimation.getKeyFrame(stateTime, true);
                                }
				break;
                        case ATTACKING_RIGHT:
                            currentFrame = rightAttack.getKeyFrame(stateTime, false);
                            break;
                        case ATTACKING_LEFT:
                            currentFrame = leftAttack.getKeyFrame(stateTime, false);
                            break;
			default:
				currentFrame = idleLeft;
		}
                lastState = state;
                WIDTH = currentFrame.getRegionWidth();
                HEIGHT = currentFrame.getRegionHeight();
		
		// Pinta en pantalla el frame actual
		spriteBatch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);

		if((state == Player.State.ATTACKING_LEFT) && stateTime > 0.4){
			state = Player.State.IDLE_LEFT;
		}else if((state == Player.State.ATTACKING_RIGHT) && stateTime > 0.4){
			state = Player.State.IDLE_RIGHT;
		}
	}
	
	/**
	 * Actualiza el estado del jugador en función de la tecla pulsada
	 * @param dt
	 */
	public void update(float dt) {

            //System.out.println("Posicion personaje actual: " + position.x + ", " + position.y);
		if (position.x <= 0)
			die();
		if (position.y <= 0)
			die();

		/*
		 *  Aplica la fuerza de la gravedad (en el eje y) sólo si no está en una plataforma
		 *  En otro caso cae de golpe al bajar de ella (se le acumula gravedad mientras está
		 *  en ella
		 */
		velocity.y -= GRAVITY * dt;

		// Controla que el personaje nunca supere una velocidad límite en el eje y
		if (velocity.y > JUMPING_SPEED)
			velocity.y = JUMPING_SPEED;
		else if (velocity.y < -JUMPING_SPEED)
			velocity.y = -JUMPING_SPEED;

		
		// Escala la velocidad para calcular cuanto se avanza en este frame (para mayor precisión)
		velocity.scl(dt);
		
		// Comprueba las colisiones con tiles en el eje X (he quitado + velocity.x en startX, endX)
		Rectangle playerRect = rectPool.obtain();
		playerRect.set(position.x, position.y, WIDTH, HEIGHT);
		int startX, endX, startY, endY;
		// El jugador se desplaza hacia la derecha
		if (velocity.x > 0)
			startX = endX = (int) (position.x + WIDTH + velocity.x); 
		// El jugador se desplaza hacia la izquierda (no se tiene en cuenta la anchura del personaje)
		else
			startX = endX = (int) (position.x + velocity.x);
		
		startY = (int) position.y;
		endY = (int) (position.y + HEIGHT);
		// Obtiene la lista de tiles que ocupan la posición del personaje
		getTilesPosition(startX, startY, endX, endY, tiles);
		playerRect.x += velocity.x;
		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				velocity.x = 0;
				break;
			}
		}
		playerRect.x = position.x;
		
		// Comprueba las colisiones con tiles en el eje Y (he quitado + velocity.y en startY, endY)
		// El jugador está saltando
		if (velocity.y > 0){
			startY = endY = (int) (position.y + HEIGHT + velocity.y);
                }
		// El jugador cae o está parado (no se tiene en cuenta su altura)
		else
			startY = endY = (int) (position.y + velocity.y);
		
		startX = (int) position.x;
		endX = (int) (position.x + WIDTH);
		// Obtiene la lista de tiles que ocupan la posición del personaje
		getTilesPosition(startX, startY, endX, endY, tiles);
		playerRect.y += velocity.y;
		for (Rectangle tile : tiles) {
			if (tile.overlaps(playerRect)) {
				if (velocity.y > 0) {
					position.y = tile.y - HEIGHT;
				}
				else {
					position.y = tile.y + tile.height;
					canJump = true;
					isJumping = false;
				}
				velocity.y = 0;
				break;
			}
		}
		rectPool.free(playerRect);
		
		velocity.scl(1 / dt);
		position.add(velocity);
	}
	
	/**
	 * El jugador salta
	 * @param sound Indica si debe sonar o no
	 */
	public void jump(boolean sound) {
		
		if (sound && ResourceManager.sonido_activado)
			ResourceManager.getSound("sounds/jump.wav").play();
		
		velocity.y = Player.JUMPING_SPEED;
		canJump = false;
		isJumping = true;
	}
        
        public void attack(){            
            isJumping = false;
            canJump = false;
            if(state == State.ATTACKING_RIGHT){
                ataque = new AtaqueEspada(position.x, position.y, "right", spriteManager);
            }else if(state == State.ATTACKING_LEFT){
                ataque = new AtaqueEspada(position.x, position.y, "left", spriteManager);
            }
            if(ResourceManager.sonido_activado)
                ResourceManager.getSound("sounds/sword.mp3").play();
            
        }

    /**
     * Check if the player can jump. Then, jump. Otherwise player does nothing
     */
	public void tryJump() {
		if (canJump) {
			jump(true);
		}
	}
	
	/**
	 * El jugador muere
	 */
	public void die() {
		
		dead = true;
		velocity.x = velocity.y = 0;
		spriteManager.CAMERA_OFFSET = 0;
                if(ResourceManager.sonido_activado)
                    spriteManager.pararMusica();
                ResourceManager.getSound("sounds/player_down.wav").play(0.5f);

		if (spriteManager.levelManager.currentLives == 1) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {}
			if(ResourceManager.sonido_activado)
				ResourceManager.getSound("sounds/game_over.wav").play();
			spriteManager.game.getScreen().dispose();

			int coins = spriteManager.levelManager.getCurrentCoins();
			spriteManager.game.setScreen(new GameOverScreen(spriteManager.game, coins, false));
		}
		else {
			spriteManager.levelManager.restartCurrentLevel();
			spriteManager.loadCurrentLevel();
			spriteManager.resetearCamara();
			spriteManager.iniciarCamara();
		}
	}
	
	/**
	 * Obtiene una lista de celdas con las que colisiona el personaje
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	private void getTilesPosition(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
		
            tiles.clear();
            float tileWidth = 0;
            float tileHeight = 0;
            if(TiledMapManager.collisionLayer != null){
                tileWidth = TiledMapManager.collisionLayer.getTileWidth();
                tileHeight = TiledMapManager.collisionLayer.getTileHeight();

                for (int y = startY; y <= endY && !levelCleared; y++) {
                    for (int x = startX; x <= endX && !levelCleared; x++) {
                        int xCell = (int) (x / tileWidth);
                        int yCell = (int) (y / tileHeight);
                        Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);

                        // Si es un bloque se añade para comprobar colisiones
                        if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED))) {
                            Rectangle rect = rectPool.obtain();
                            // El jugador está saltando (se choca con la cabeza en una celda)
                            if (velocity.y > 0)
                                    rect.set(x, y, 1, 1);
                            // El jugador está cayendo (se posa en la celda que tenga debajo)
                            else
                                    rect.set((int) (Math.ceil(x / tileWidth) * tileWidth), (int) (Math.ceil(y / tileHeight) * tileHeight), 0, 0);

                            tiles.add(rect);

                        }
                        // Si es una moneda, desaparece
                        else if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.COIN))) {
                            if(ResourceManager.sonido_activado)
                                ResourceManager.getSound("sounds/coin.wav").play();
                            spriteManager.levelManager.removeCoin(xCell, yCell);
                            System.out.println("Moneda eliminada.");
                        }
                        // Si es un enemigo pierde una vida
                        else if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.ENEMY))) {
                            if (!dead) {
                                    die();
                            }
                        }
                        // Si es un cofre se abre y se termina la pantalla
                        else if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.GOAL))) {

                            if (!levelCleared) {
                                    levelCleared = true;
                                    if(ResourceManager.sonido_activado){
                                            spriteManager.music.stop();
                                            ResourceManager.getSound("sounds/level_clear.wav").play();
                                    }
                                    System.out.println("Nivel Terminado desde player");
                                    spriteManager.levelManager.finishCurrentLevel();
                                    spriteManager.passCurrentLevel();
                            }
                        }
                    }
                }
            }
        }
        
        public float getX(){
            return position.x;
        }
        public float getY(){
            return position.y;
        }
        
}
