package demons.night.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import demons.night.managers.ResourceManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;


public class Gem implements Disposable {

    public Vector2 position;
    public boolean isAlive;

    private TextureRegion currentFrame;
    private Animation<TextureRegion> animation;
    private float stateTime;
    public static int WIDTH = 10;
    public static int HEIGHT = 15;


    private Array<Rectangle> tiles = new Array<Rectangle>();
    // Pool de rectángulos (mejora la eficiencia si se trabaja con muchos)
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
            @Override
            protected Rectangle newObject () {
                    return new Rectangle();
            }
    };

    

    public Gem() {

            position = new Vector2();

            TextureAtlas atlas = ResourceManager.assets.get("items/items.pack", TextureAtlas.class);

            animation = new Animation<TextureRegion>(0.15f, atlas.findRegions("gem"));
            isAlive = true;
    }

    /**
     * Pinta la animación en pantalla
     */
    public void render(Batch spriteBatch) {

        stateTime += Gdx.graphics.getDeltaTime();

        if(isAlive){
            currentFrame = animation.getKeyFrame(stateTime, true);
        }else{
            currentFrame = null;
        }

        // Pinta en pantalla el frame actual
        spriteBatch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
    }

    @Override
    public void dispose() {
            isAlive = false;
    }
}