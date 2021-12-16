package com.mygdx.poyo;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.poyo.GameScreen.PPM;

public class Enemy {

    TextureAtlas textureAtlas;
    TextureRegion enemyRegion;
    Sprite enemySprite;
    World world;
    BodyDef enemyBodyDef;
    Body enemyBody;
    Animation<TextureRegion> enemyAnimation;
    float enemyIdleAnimationTime = 0.0f;


    public Enemy(World world) {
        this.world = world;
        this.textureAtlas = new TextureAtlas("EnemyTexture.atlas");
        this.enemyRegion = textureAtlas.findRegion("Untitled - 1_Enemy_Idle");
        this.enemySprite = new Sprite(enemyRegion);
        this.enemySprite.setPosition(16,5 );
        this.enemyAnimation = new Animation<TextureRegion>(0.2f,textureAtlas.findRegions("Untitled - 1_Enemy_Idle"), Animation.PlayMode.LOOP);
        this.createBody();

    }



    public void render(float delta,SpriteBatch batch) {
        enemyIdleAnimationTime += delta;
        TextureRegion enemIdleFrame = enemyAnimation.getKeyFrame(enemyIdleAnimationTime);
        batch.draw(enemIdleFrame, this.enemySprite.getX(), this.enemySprite.getY(),
                this.enemySprite.getWidth() * PPM, this.enemySprite.getHeight() * PPM);
        move();

    }

    private void move() {
     //   this.getEnemyBody().setLinearVelocity(1 ,0);


    }

    private void createBody() {

        enemyBodyDef = new BodyDef();
        enemyBodyDef.type = BodyDef.BodyType.DynamicBody;
        enemyBodyDef.position.set(this.enemySprite.getX(), this.enemySprite.getY());


        enemyBody = world.createBody(enemyBodyDef);
        enemyBody.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((this.enemySprite.getWidth() / 2 - 1) * PPM, (this.enemySprite.getHeight() / 2 - 1) * PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.3f;
        fixtureDef.density = 1f;


        enemyBody.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }



    public Sprite getEnemySprite() {
        return enemySprite;
    }

    public void setEnemySprite(Sprite enemySprite) {
        this.enemySprite = enemySprite;
    }

    public Body getEnemyBody() {
        return enemyBody;
    }

    public void setEnemyBody(Body enemyBody) {
        this.enemyBody = enemyBody;
    }
}
