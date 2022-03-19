package demons.night.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import demons.night.managers.ResourceManager;
import demons.night.managers.TiledMapManager;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import static java.lang.Math.abs;


public class EnemigoVolador implements Disposable {

	public Vector2 position;
	public State state;
	public Vector2 velocity;
	public boolean isAlive;
        private float stateTime;
        private Vector2 posicion_inicial;
	private TextureRegion currentFrame;
	private boolean faceLeft;
	private Animation<TextureRegion> rightAnimation;
        private Animation<TextureRegion> leftAnimation;
        
        public enum State {
		RUNNING_LEFT, RUNNING_RIGHT
	}
        
	private Array<Rectangle> tiles = new Array<Rectangle>();
	// Pool de rectángulos (mejora la eficiencia si se trabaja con muchos)
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};
	
	// Parámetros de movimiento del enemigo
	public static float WALKING_SPEED = 1.0f;
	public static float JUMPING_SPEED = 5.0f;
	public static float MAX_JUMPING = 60f;
	public static float GRAVITY = 9f;
	public static int WIDTH = 14;
	public static int HEIGHT = 13;
	
	public EnemigoVolador() {
		
		position = new Vector2();
                posicion_inicial = new Vector2();
		velocity = new Vector2();
		faceLeft = true;
                state = State.RUNNING_LEFT;
                
                TextureAtlas atlas = ResourceManager.assets.get("characters/characters.pack", TextureAtlas.class);

                rightAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("volador_right"));
		
		// Crea la animación para correr hacia la izquierda
		leftAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("volador_left"));
                
		// Textura del enemigo
		//currentFrame = new TextureRegion(ResourceManager.getAtlas("characters/characters.pack").findRegion("enemy"));
		
		velocity.x = -1.0f;
		isAlive = true;
	}
	
	/**
	 * Pinta la animación en pantalla
	 */
	public void render(Batch spriteBatch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		// Calcula el frame del personaje que se debe pintar (o qué animación)
		switch (state) {
			case RUNNING_LEFT:

                            currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                            break;
			case RUNNING_RIGHT:
                                currentFrame = rightAnimation.getKeyFrame(stateTime, true);
				break;
			default:
				currentFrame = leftAnimation.getKeyFrame(stateTime, false);
		}
                
                WIDTH = currentFrame.getRegionWidth();
                HEIGHT = currentFrame.getRegionHeight();
		
		// Pinta en pantalla el frame actual
		spriteBatch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
	}
	
	/**
	 * Actualiza el estado del jugador en función de la tecla pulsada
	 * @param dt
	 */
	public void update(float dt) {
            
                if(posicion_inicial.x == 0){
                    posicion_inicial.x = position.x;
                }
		if (position.x <= 0){
                        faceLeft = !faceLeft;
                    }
                
		if (faceLeft){
                    
                    velocity.x = -WALKING_SPEED;
                }	
                else{
                    velocity.x = WALKING_SPEED;
                }
		
		velocity.scl(dt);
		
		// Para el chequeo de colisiones
		int startX, endX, startY, endY;
		Rectangle rect = rectPool.obtain();
		rect.set(position.x, position.y, 18, 18);
		
		// Comprueba las colisiones con tiles en el eje Y (he quitado + velocity.y en startY, endY)
		// El enemigo está saltando
		if (velocity.y > 0)
                    startY = endY = (int) (position.y + HEIGHT + velocity.y);
		// El enemigo cae o está parado (no se tiene en cuenta su altura)
		else
                    startY = endY = (int) (position.y + velocity.y);
		
		startX = (int) position.x;
		endX = (int) (position.x + WIDTH);
		// Obtiene la lista de tiles que ocupan la posición del enemigo
                
		getTilesPosition(startX, startY, endX, endY, tiles);
		rect.y += velocity.y;
		for (Rectangle tile : tiles) {
                    if (tile.overlaps(rect)) {
                            if (velocity.y > 0) {
                                    position.y = tile.y - HEIGHT;
                            }
                            else {
                                    position.y = tile.y + tile.height;
                            }
                            velocity.y = 0;
                            break;
                    }
		}
		
		// Comprueba las colisiones con tiles en el eje X (he quitado + velocity.x en startX, endX)
		// El enemigo se desplaza hacia la derecha
		if (velocity.x > 0){
			startX = endX = (int) (position.x + WIDTH + velocity.x); 
                        state = State.RUNNING_RIGHT;
                        
		// El enemigo se desplaza hacia la izquierda (no se tiene en cuenta la anchura del enemigo)
                }else{
			startX = endX = (int) (position.x + velocity.x);
                        state = State.RUNNING_LEFT;
                        
                }
                
		
		startY = (int) position.y;
		endY = (int) (position.y + HEIGHT);
		// Obtiene la lista de tiles que ocupan la posición del enemigo
		getTilesPosition(startX, startY, endX, endY, tiles);
                
               
		rect.x += velocity.x;
		for (Rectangle tile : tiles) {
                    if (rect.overlaps(tile)) {
                        faceLeft = !faceLeft;
                        velocity.x = 0;
                        System.out.println("COLISION");
                        break;
                    }
		}
                                
                if(abs(endX  - posicion_inicial.x) > 30){
                    faceLeft = !faceLeft;
                }
                
		rect.x = position.x;
		
		rectPool.free(rect);
		
		velocity.scl(1 / dt);
		position.add(velocity);
	}
	
	/**
	 * Obtiene una lista de celdas con las que colisiona el enemigo
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 */
	private void getTilesPosition(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
		
            tiles.clear();

            for (int y = startY; y <= endY; y++) {
                    for (int x = startX; x <= endX; x++) {
                            int xCell = (int) (x / TiledMapManager.collisionLayer.getTileWidth());
                            int yCell = (int) (y / TiledMapManager.collisionLayer.getTileHeight());
                            Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);

                            // Si es un bloque se aniade para comprobar colisiones
                            if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED))) {
                                    Rectangle rect = rectPool.obtain();

                                    rect.set((int) (Math.ceil(x / 16f) * 16), (int) (Math.ceil(y / 16f) * 16), 0, 0);
                                    tiles.add(rect);
                            }
                    }
            }
	}
	
	@Override
	public void dispose() {
		isAlive = false;
	}
}