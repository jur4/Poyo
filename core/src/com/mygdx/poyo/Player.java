package com.mygdx.poyo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import static com.mygdx.poyo.GameScreen.PPM;


public class Player extends ScreenAdapter {

    private World world;
    private float ppm = 1.0f / 100.0f;
    private Sprite poyoSprite;
    private Texture poyoTexture;
    private TextureAtlas textureAtlas;
    private TextureRegion poyoRegion;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> leftwalkAnimation;
    private Animation<TextureRegion> rightwalkAnimation;
    private Animation<TextureRegion> leftwalkAfterRightAnimation;
    private Animation<TextureRegion> rightwalkAfterLeftAnimation;
    private PoyoState poyoState;
    private static final float MAXVELOXITY = 30;
    private float animationTimeIdle = 0.0f;
    private float animationTimeLeftWalk = 0.0f;
    private float animationTimeRightwalk = 0.0f;
    private float blinkTime = 0.0f;
    private Random random;
    private Body poyoBody;
    private BodyDef poyoBodyDef;
    private PoyoState lastPoyostate;


    public Player(World world) {
        this.random = new Random();
        this.world = world;
        this.textureAtlas = new TextureAtlas("poyoAtlas.atlas");
        this.poyoRegion = this.textureAtlas.findRegion("Idle", 0);
        this.idleAnimation = new Animation<TextureRegion>(0.2f, this.textureAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        this.leftwalkAnimation = new Animation<TextureRegion>(0.1f, this.textureAtlas.findRegions("LeftWalk"), Animation.PlayMode.NORMAL);
        this.rightwalkAnimation = new Animation<TextureRegion>(0.1f, this.textureAtlas.findRegions("RightWalk"), Animation.PlayMode.NORMAL);

        this.poyoSprite = new Sprite(poyoRegion);
        this.poyoSprite.setPosition(3, 10);
        createPoyoBody();

    }

    public void render(float delta, SpriteBatch batch, Viewport viewport) {
        this.calculateBlinkAction(delta);
        this.animationTimeIdle += delta;
        this.animationTimeLeftWalk += delta;
        this.animationTimeRightwalk += delta;

        TextureRegion idleFrame = this.idleAnimation.getKeyFrame(this.animationTimeIdle);
        TextureRegion leftwalkFrame = this.leftwalkAnimation.getKeyFrame(this.animationTimeLeftWalk);
        TextureRegion rightwalkFrame = this.rightwalkAnimation.getKeyFrame(this.animationTimeRightwalk);


        if (leftwalkAnimation.isAnimationFinished(animationTimeLeftWalk) && getPoyoState() != PoyoState.LEFTWALK) {
            animationTimeLeftWalk = 0;
        }
        if (rightwalkAnimation.isAnimationFinished(animationTimeRightwalk) && getPoyoState() != PoyoState.RIGHTWALK) {
            animationTimeRightwalk = 0;
        }





        switch (getPoyoState()) {
            case IDLE:


                if (!idleAnimation.isAnimationFinished(animationTimeIdle) && getPoyoState() == PoyoState.IDLE) {
                    batch.draw(idleFrame, this.poyoSprite.getX(), this.poyoSprite.getY(),
                            this.poyoSprite.getWidth() * PPM, this.poyoSprite.getHeight() * PPM);

                }  else {
                    batch.draw(poyoRegion, this.poyoSprite.getX(), this.poyoSprite.getY(),
                            this.poyoSprite.getWidth() * PPM, this.poyoSprite.getHeight() * PPM);

                }
                lastPoyostate = PoyoState.IDLE;
                break;
            case LEFTWALK:

                batch.draw(leftwalkFrame, this.poyoSprite.getX(), this.poyoSprite.getY(),
                        this.poyoSprite.getWidth() * PPM, this.poyoSprite.getHeight() * PPM);
                lastPoyostate = PoyoState.LEFTWALK;
                break;
            case RIGHTWALK:

                batch.draw(rightwalkFrame, this.poyoSprite.getX(), this.poyoSprite.getY(),
                        this.poyoSprite.getWidth() * PPM, this.poyoSprite.getHeight() * PPM);
                lastPoyostate = PoyoState.RIGHTWALK;
                break;
        }
    }

    private void calculateBlinkAction(float delta) {
        blinkTime = 100 * random.nextFloat();
        if (blinkTime < 90.1f && blinkTime > 89.0 && animationTimeIdle > 4.0f) {
            animationTimeIdle = 0;
        }
    }

    public PoyoState getPoyoState() {

        if (poyoBody.getLinearVelocity().x > 0.25f) {
            return PoyoState.RIGHTWALK;
        }
        if (poyoBody.getLinearVelocity().x < -0.25f) {
            return PoyoState.LEFTWALK;
        }
        return PoyoState.IDLE;

    }

    public void poyoWalk(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.D) && poyoBody.getLinearVelocity().x > -MAXVELOXITY) {
            poyoBody.applyLinearImpulse(0.025f, 0, poyoBody.getPosition().x, poyoBody.getPosition().y, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && poyoBody.getLinearVelocity().x < MAXVELOXITY) {
            poyoBody.applyLinearImpulse(-0.025f, 0, poyoBody.getPosition().x, poyoBody.getPosition().y, true);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && poyoBody.getLinearVelocity().y > -0.1 && poyoBody.getLinearVelocity().y < 0.5  ) {
            poyoBody.applyLinearImpulse(0, 1.2f, poyoBody.getPosition().x, poyoBody.getPosition().y, true);
        }
    }

    private void createPoyoBody() {

        poyoBodyDef = new BodyDef();
        poyoBodyDef.type = BodyDef.BodyType.DynamicBody;
        poyoBodyDef.position.set(this.getPoyoSprite().getX(), this.getPoyoSprite().getY());



        poyoBody = world.createBody(poyoBodyDef);
        poyoBody.setFixedRotation(true);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox((this.getPoyoSprite().getWidth() / 2 - 1) * PPM, (this.getPoyoSprite().getHeight() / 2 - 1) * PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.3f;
        fixtureDef.density = 1f;


        poyoBody.createFixture(fixtureDef).setUserData(this);


        shape.dispose();
    }

    public Texture getPoyoTexture() {
        return this.poyoTexture;
    }

    public void setPoyoTexture(Texture poyoTexture) {
        this.poyoTexture = poyoTexture;
    }


    public Sprite getPoyoSprite() {
        return this.poyoSprite;
    }

    public void setPoyoSprite(Sprite poyoSprite) {
        this.poyoSprite = poyoSprite;
    }

    public Body getPoyoBody() {
        return poyoBody;
    }

    public void setPoyoBody(Body poyoBody) {
        this.poyoBody = poyoBody;
    }
}
