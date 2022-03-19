package demons.night.managers;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import demons.night.characters.Enemy;
import demons.night.characters.EnemigoVolador;
import demons.night.characters.Gem;
import demons.night.characters.Duende;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;


public class LevelManager {

	// Info del LevelManager
	public static final String LEVEL_DIR = "levels";
	public static final String LEVEL_PREFIX = "level";
	public static final String LEVEL_EXTENSION = ".tmx";

	public static float PRIMERA_X = 0;
	public static float PRIMERA_Y = 0;

	// NPC del nivel actual
	public Array<Enemy> enemies;
	public Array<Gem> gems;
	public Array<EnemigoVolador> murcielagos;
	public Array<Duende> duendes;
	
	// Mapa del nivel actual
	public TiledMap map;
	
	// Parámetros de nivel
	public int currentLevel;
	public int currentLives;
	public int totalCoins;
	public int currentCoins;

    // Indica si la pantalla actual es más alta que la cámara
	public boolean highLevel;

    public LevelManager() {

        currentLevel = 1;
        currentLives = 3;
        currentCoins = 0;
        totalCoins = 0;
        highLevel = true;

        enemies = new Array<Enemy>();
        gems = new Array<Gem>();
        murcielagos = new Array<>();
        duendes = new Array<Duende>();
        
    }
	
	public void passCurrentLevel() {
		currentLevel++;
	}
	
	public String getCurrentLevelName() {
		return LEVEL_PREFIX + currentLevel;
	}
	
	public String getCurrentLevelPath() {
		return LEVEL_DIR + "/" + getCurrentLevelName() + LEVEL_EXTENSION;
	}
	
	/**
	 * Carga el mapa de la pantalla actual
	 */
	public boolean loadCurrentMap() {

            
            TiledMapManager.setLevelManager(this);
            //File f = new File(getCurrentLevelPath());
            //if(f.exists()){
            //System.out.println("mapa existe");
            try {
                    map = new TmxMapLoader().load(getCurrentLevelPath());
            }catch(Exception ex){
                    return false;
            }
            if(map.getLayers().size() > 4){
                TiledMapManager.collisionLayer = (TiledMapTileLayer) map.getLayers().get("rocks");
                TiledMapManager.objectLayer = map.getLayers().get("objects");

                loadEnemies();
                loadGems();
                //loadPlatforms();
                return true;
            }else{
                System.out.println("No hay mapa");
                return false;
            }
            
	}
	
	/**
	 * Carga los enemigos del nivel actual
	 */
	private void loadEnemies() {
		
		Enemy enemy = null;
		EnemigoVolador murcielago = null;
		Duende duende = null;
		// Carga los objetos móviles del nivel actual
		for (MapObject object : map.getLayers().get("objects").getObjects()) {
			TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
			if (object.getProperties().containsKey(TiledMapManager.ENEMY)) {
				if(object.getProperties().containsKey(TiledMapManager.VOLADOR)){
					murcielago = new EnemigoVolador();
					murcielago.position.set(tileObject.getX(), tileObject.getY());
					murcielagos.add(murcielago);
					System.out.println("Murcielago creado.");
				}else if(object.getProperties().containsKey("skeleto")){
					enemy = new Enemy();
					enemy.position.set(tileObject.getX(), tileObject.getY());
					enemies.add(enemy);
				}else if(object.getProperties().containsKey("duende")){
					duende = new Duende();
					duende.position.set(tileObject.getX(), tileObject.getY());
					duendes.add(duende);
				}

			}
		}
	}
	
	/**
	 * Sitúa un enemigo en la pantalla
	 * @param x Posición x
	 * @param y Posición y
	 */
	public void addEnemy(float x, float y) {
		
		Enemy enemy = new Enemy();
		enemy.position.set(x * map.getProperties().get("tilewidth", Integer.class), y * map.getProperties().get("tileheight",
            Integer.class));
		enemies.add(enemy);
	}
        
        /**
	 * Sitúa un enemigo en la pantalla
	 * @param x Posición x
	 * @param y Posición y
	 */
	public void addMurcielago(float x, float y) {
		
		EnemigoVolador m = new EnemigoVolador();
		m.position.set(x * map.getProperties().get("tilewidth", Integer.class), y * map.getProperties().get("tileheight",
            Integer.class));
		murcielagos.add(m);
	}
        
        public void addDuende(float x, float y) {
		
		Duende m = new Duende();
		m.position.set(x * map.getProperties().get("tilewidth", Integer.class), y * map.getProperties().get("tileheight",
            Integer.class));
		duendes.add(m);
	}
        
        public void addGem(float x, float y) {
		
		Gem m = new Gem();
		m.position.set(x * map.getProperties().get("tilewidth", Integer.class), y * map.getProperties().get("tileheight",
            Integer.class));
		gems.add(m);
	}
	
	/**
	 * Elimina una moneda de la pantalla
	 * @param x Posición x de la moneda
	 * @param y Posición y de la moneda
	 */
	public void removeCoin(int x, int y) {
		System.out.println("Borrando moneda...");
		for(MapObject object : TiledMapManager.objectLayer.getObjects()){
			TiledMapTileMapObject tile = (TiledMapTileMapObject) object;
			if(tile.getX() == x && tile.getY() == y){
				TiledMapManager.objectLayer.getObjects().remove(object);
			}
		}
		currentCoins++;
	}

	
	/**
	 * Elimina los personajes del el nivel actual
	 */
	public void clearCharactersCurrentLevel() {
            enemies.clear();
            gems.clear();
            murcielagos.clear();
            duendes.clear();
	}
	
	/**
	 * Finaliza y limpia el nivel actual
	 */
	public void finishCurrentLevel() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {}

        totalCoins += currentCoins;
        currentCoins = 0;

        clearCharactersCurrentLevel();

	}

    /**
     * Reinicia la pantalla actual
     * (Normalmente para jugarla otra vez)
     */
    public void restartCurrentLevel() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {}

        currentCoins = 0;
        currentLives--;
        clearCharactersCurrentLevel();
    }

    private void loadGems() {
        
        Gem gem = null;
        // Carga los objetos móviles del nivel actual
        for (MapObject object : map.getLayers().get("objects").getObjects()) {
            TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
            if (object.getProperties().containsKey(TiledMapManager.COIN)) {
                
                    gem = new Gem();
                    gem.position.set(tileObject.getX(), tileObject.getY());
                    gems.add(gem);
                    //System.out.println("Gema added.");
            }
        }
    }

	public int getTotalCoins() {
		return totalCoins;
	}

	public int getCurrentCoins() {
		return currentCoins;
	}
}
