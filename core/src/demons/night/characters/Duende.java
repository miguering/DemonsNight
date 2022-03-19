package demons.night.characters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import demons.night.managers.ResourceManager;
import demons.night.managers.TiledMapManager;


public class Duende implements Disposable{
    public Vector2 position;
    public State state;
    public Vector2 velocity;
    public boolean isAlive;
    private float stateTime;

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
    public static float WALKING_SPEED = 3.0f;
    public static float JUMPING_SPEED = 8.0f;
    public static float MAX_JUMPING = 60f;
    public static float GRAVITY = 9f;
    public static int WIDTH = 12;
    public static int HEIGHT = 17;

    public Duende() {

            position = new Vector2();
            velocity = new Vector2();
            faceLeft = true;
            state = State.RUNNING_LEFT;

            TextureAtlas atlas = ResourceManager.assets.get("characters/characters.pack", TextureAtlas.class);

            rightAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("duende_right"));

            // Crea la animación para correr hacia la izquierda
            leftAnimation = new Animation<TextureRegion>(0.15f, atlas.findRegions("duende_left"));

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

            if (faceLeft)
                    velocity.x = -WALKING_SPEED;
            else
                    velocity.x = WALKING_SPEED;

            // Si sale de la pantalla se marca para eliminar
            
            if (position.x <= 0) 
                    dispose();
            if (position.y <= 32)
                    dispose();

            // Aplica la fuerza de la gravedad (en el eje y)
            velocity.y -= GRAVITY * dt;

            // Controla que el enemigo nunca supere una velocidad límite en el eje y
            if (velocity.y > JUMPING_SPEED)
                    velocity.y = JUMPING_SPEED;
            else if (velocity.y < -JUMPING_SPEED)
                    velocity.y = -JUMPING_SPEED;

            // Escala la velocidad para calcular cuanto se avanza en este frame (para mayor precisión)
            velocity.scl(dt);

            // Para el chequeo de colisiones
            int startX, endX, startY, endY;
            Rectangle rect = rectPool.obtain();
            rect.set(position.x, position.y, 18, 28);

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
            
            /*
            getTilesCercanas(startX, startY, endX, endY, tiles);
            
            if(tiles.size > 0){
                jump();
            }
            */
            if(comprobarCaida()){
                    faceLeft = !faceLeft;
                    velocity.x = 0;
            }
            
            if(comprobarPresencia(position.x, position.y)){
                velocity.y = JUMPING_SPEED;
                if(ResourceManager.sonido_activado)
                    ResourceManager.getSound("sounds/duende.mp3").play();
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
                            TiledMapTileLayer.Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);

                            // Si es un bloque se aniade para comprobar colisiones
                            if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED))) {
                                    Rectangle rect = rectPool.obtain();

                                    rect.set((int) (Math.ceil(x / 16f) * 16), (int) (Math.ceil(y / 16f) * 16), 0, 0);
                                    tiles.add(rect);
                            }
                    }
            }
    }
    
    private boolean comprobarCaida(){
            boolean caida = false;
            
            //System.out.println("Posicion del esqueleto " + this.toString() + " x " + position.x + " y " + position.y);
            //System.out.println("Comprobando la caida para x " + x + " y " + y);
            float x_destino=0;
            if(faceLeft){
                x_destino = position.x - 7f;
            }else{
                x_destino = position.x + 7f;
            }
            
                int xCell = (int) (x_destino / TiledMapManager.collisionLayer.getTileWidth());
                int yCell = (int) ((position.y - 16) / TiledMapManager.collisionLayer.getTileHeight());
                TiledMapTileLayer.Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);
                if(cell == null){
                    //System.out.println("Hay caida");
                    return true;
                }
            
            return caida;
        }
    
    private boolean comprobarPresencia(float x, float y){
            boolean presencia = false;
            
            //System.out.println("Posicion del esqueleto " + this.toString() + " x " + position.x + " y " + position.y);
            //System.out.println("Comprobando la caida para x " + x + " y " + y);
            float x_destino=0;
            float recorrido = 0;
            if(faceLeft){
                x_destino = x - 48f;
            }else{
                x_destino = x + 48f;
            }
            Rectangle duendeRectangle = new Rectangle(x_destino, position.y+16, WIDTH, HEIGHT);
            
            int xCell = (int) (x_destino / TiledMapManager.collisionLayer.getTileWidth());
            int yCell = (int) ((position.y + 16) / TiledMapManager.collisionLayer.getTileHeight());
            for (MapObject object : TiledMapManager.objectLayer.getObjects()) {
                    TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                    if (object.getProperties().containsKey(TiledMapManager.PERSONAJE)) {
                        Rectangle rect = new Rectangle(tileObject.getX(), tileObject.getY(), Player.WIDTH + 48, Player.HEIGHT);

                        if(rect.overlaps(duendeRectangle)){
                            return true;
                        }                          
                    }
            }
                
            return presencia;
        }
    
    private void getTilesCercanas(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {

            tiles.clear();

            for (int y = startY; y <= endY; y++) {
                    for (int x = startX - 400; x <= endX + 400; x++) {
                            int xCell = (int) (x / TiledMapManager.collisionLayer.getTileWidth());
                            int yCell = (int) (y / TiledMapManager.collisionLayer.getTileHeight());
                            TiledMapTileLayer.Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);

                            // Si es un bloque se aniade para comprobar colisiones
                            if ((cell != null) && (cell.getTile().getProperties().containsKey("personaje"))) {
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
            if(ResourceManager.sonido_activado)
                ResourceManager.getSound("sounds/duende.mp3").play();
    }
    
    public void jump(){
        velocity.y += JUMPING_SPEED;
    }
}
