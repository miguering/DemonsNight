package demons.night.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AndroidControlsManager {

    private Touchpad joystick;
    private Texture knobTexture;
    private Texture contenedorJoystick;
    private ImageButton saltar;
    private ImageButton golpear;
    private ImageButton pause;
    private Stage stage;
    private float ancho;
    private float alto;

    public AndroidControlsManager(Stage stage, Skin skin) {
        if(Gdx.app.getType().equals(Application.ApplicationType.Android)){
            ancho = stage.getCamera().viewportWidth;
            alto = stage.getCamera().viewportHeight;
            contenedorJoystick = new Texture(Gdx.files.internal("ui/touchBackground.png"));

            this.knobTexture = new Texture(Gdx.files.internal("ui/touchKnob.png"));

            this.stage = stage;

            joystick = new Touchpad(80, skin);
            joystick.setBounds(ancho * 0.05f, alto * 0.05f, contenedorJoystick.getWidth() * 2, contenedorJoystick.getHeight() * 2);
            joystick.getStyle().background = new TextureRegionDrawable(new TextureRegion(contenedorJoystick));
            joystick.getStyle().knob = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/touchKnob.png"))));
            joystick.getStyle().knob.setMinWidth(knobTexture.getWidth());
            joystick.getStyle().knob.setMinWidth(knobTexture.getHeight());

            saltar = new ImageButton( new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/botonsaltar.png"))))); //Set the button up
            golpear = new ImageButton( new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/botongolpear.png"))))); //Set the button up
            pause = new ImageButton( new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/botonpause.png"))))); //Set the button up
            pause.setBounds(ancho - pause.getWidth() * 1.7f, alto * 0.90f, pause.getWidth(), pause.getHeight());
            saltar.setBounds(ancho * 0.85f + golpear.getWidth(), alto * 0.10f, saltar.getWidth(), saltar.getHeight());
            golpear.setBounds(ancho * 0.8f , alto * 0.10f, golpear.getWidth(), saltar.getHeight());

            stage.addActor(joystick);
            stage.addActor(saltar);
            stage.addActor(golpear);
            stage.addActor(pause);
        }
    }

    public Touchpad getJoystick() {
        return joystick;
    }

    public void setJoystick(Touchpad joystick) {
        this.joystick = joystick;
    }

    public ImageButton getSaltar() {
        return saltar;
    }

    public void setSaltar(ImageButton saltar) {
        this.saltar = saltar;
    }

    public ImageButton getGolpear() {
        return golpear;
    }

    public void setGolpear(ImageButton golpear) {
        this.golpear = golpear;
    }

    public ImageButton getPause() {
        return pause;
    }
}
