package com.mygdx.poyo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

//todo enemies
//todo tilemap
//todo contact Listener
//todo camera fix


public class GameScreen extends ScreenAdapter {


    static final float STEP_TIME = 1f / 60f;
    public static final float PPM = 1f / 30f;
    private float accumulator = 0;
    private SpriteBatch batch;
    private Player poyoPlayer;
    private Enemy enemy;
    private World world;
    private Vector2 gravity;
    private OrthographicCamera orthographicCamera;
    private Box2DDebugRenderer debugRenderer;
    private Body groundBody;
    private BodyDef groundBodyDef;
    private FitViewport fitViewport;
    private static BD2ContactListener contactListener;




    public GameScreen() {

        debugRenderer = new Box2DDebugRenderer();
        orthographicCamera = new OrthographicCamera();
        fitViewport = new FitViewport(Gdx.graphics.getWidth() * PPM, Gdx.graphics.getHeight() * PPM, orthographicCamera);
        orthographicCamera.zoom = 0.5f;
        batch = new SpriteBatch();
        gravity = new Vector2(0f, -12);
        world = new World(gravity, true);
        contactListener = new BD2ContactListener();
        world.setContactListener(contactListener);
        enemy = new Enemy(world);
        poyoPlayer = new Player(world);
        createGroundBody();



    }


    @Override
    public void render(float delta) {

        ScreenUtils.clear(1, 1, 1, 1);
        orthographicCamera.update();
        orthographicCamera.position.set(poyoPlayer.getPoyoSprite().getX(),poyoPlayer.getPoyoSprite().getY(),0);
        batch.setProjectionMatrix(orthographicCamera.combined);
        batch.begin();
        poyoPlayer.render(delta, batch, fitViewport);
        enemy.render(delta,batch);
        batch.end();
        debugRenderer.render(world, orthographicCamera.combined);
        poyoPlayer.poyoWalk(delta);
        stepWorld(delta);
        poyoPlayer.getPoyoSprite().setPosition(poyoPlayer.getPoyoBody().getPosition().x - poyoPlayer.getPoyoSprite().getWidth() / 2 * PPM, poyoPlayer.getPoyoBody().getPosition().y - (poyoPlayer.getPoyoSprite().getHeight() - 2) / 2 * PPM);
        enemy.getEnemySprite().setPosition(enemy.getEnemyBody().getPosition().x - enemy.getEnemySprite().getWidth() / 2 * PPM, enemy.getEnemyBody().getPosition().y - (enemy.getEnemySprite().getHeight() - 2) / 2 * PPM);
        checkContacts();
    }



    private void stepWorld(float delta) {

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, 6, 2);
        }
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();


    }

    private void checkContacts() {

    }



    private void createGroundBody() {

        groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(new Vector2(0, -70 * PPM));

        groundBody = world.createBody(groundBodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(150f * PPM, 20f * PPM);

        groundBody.createFixture(shape, 0.0f);

        shape.dispose();

    }

}
