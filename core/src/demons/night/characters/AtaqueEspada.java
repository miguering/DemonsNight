package demons.night.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import demons.night.characters.Enemy;
import demons.night.managers.LevelManager;
import demons.night.managers.ResourceManager;
import demons.night.managers.SpriteManager;


public class AtaqueEspada implements Disposable{
    
    public Vector2 position;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private String direccion;
    LevelManager levelManager;
    public Rectangle rectAtaque;
    
    private Array<Rectangle> tiles = new Array<Rectangle>();
    // Pool de rectángulos (mejora la eficiencia si se trabaja con muchos)
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
            @Override
            protected Rectangle newObject () {
                    return new Rectangle();
            }
    };
    
    public static int WIDTH = 53;
    public static int HEIGHT = 69;

    public AtaqueEspada(float x, float y, String direccion, SpriteManager sprite) {
            rectAtaque = new Rectangle();
            levelManager = sprite.levelManager;
            this.direccion = direccion;
            position = new Vector2(x, y);

            TextureAtlas atlas = ResourceManager.assets.get("characters/characters.pack", TextureAtlas.class);

            if(direccion.equals("left")){
                animation = new Animation<TextureRegion>(0.02f, atlas.findRegions("water_attack_left"));
            }else if(direccion.equals("right")){
                animation = new Animation<TextureRegion>(0.02f, atlas.findRegions("water_attack_right"));
            }
            
            
    }
    
    public void render(Batch spriteBatch) {
            
            stateTime += Gdx.graphics.getDeltaTime();

            currentFrame = animation.getKeyFrame(stateTime, false);

            
            // Pinta en pantalla el frame actual
            if(this.direccion == "left"){
                spriteBatch.draw(currentFrame, position.x - WIDTH * 0.5f, position.y, WIDTH, HEIGHT);
            }else{
                spriteBatch.draw(currentFrame, position.x + WIDTH * 0.5f, position.y, WIDTH, HEIGHT);
            }
            
            
            rectAtaque.set(this.position.x, this.position.y, this.WIDTH, this.HEIGHT);
            
            Rectangle enemyRect = new Rectangle();
            // Comprueba si el enemigo ha chocado contra algún enemigo
            for (EnemigoVolador murcielago : levelManager.murcielagos) {
                enemyRect.set(murcielago.position.x, murcielago.position.y, murcielago.WIDTH, murcielago.HEIGHT);

                if (enemyRect.overlaps(rectAtaque)) {

                    if(ResourceManager.sonido_activado)
                        ResourceManager.getSound("sounds/murcielago.mp3").play();
                    levelManager.murcielagos.removeValue(murcielago, true);
                }else{
                    //System.out.println("No coincide con un murcielago");
                }
                
            }
            
            // Comprueba si el enemigo ha chocado contra algún enemigo
            for (Enemy enemigo : levelManager.enemies) {
                
                enemyRect.set(enemigo.position.x, enemigo.position.y, enemigo.WIDTH, enemigo.HEIGHT);

                if (enemyRect.overlaps(rectAtaque)) {

                    if(ResourceManager.sonido_activado)
                        ResourceManager.getSound("sounds/golem.mp3").play();
                    levelManager.enemies.removeValue(enemigo, true);
                }
                else{
                    //System.out.println("No coincide con un zombi");
                }
            }
	}
        
    @Override
    public void dispose() {
    }
    
    public void accion(){
        //this.render();
        
        
    }
    
}
