package com.mygdx.poyo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;


// todo restart game
// todo dying
// todo player should have a circle as shape




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
    private TiledMap tiledMap;
    private TmxMapLoader tmxMapLoader;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private ArrayList<Body> bodyArrayList;


    public GameScreen() {


        bodyArrayList = new ArrayList<>();
        debugRenderer = new Box2DDebugRenderer();
        orthographicCamera = new OrthographicCamera();
        fitViewport = new FitViewport(Gdx.graphics.getWidth() * PPM, Gdx.graphics.getHeight() * PPM, orthographicCamera);
        orthographicCamera.zoom = 0.6f;
        batch = new SpriteBatch();
        gravity = new Vector2(0f, -12);
        world = new World(gravity, true);
        contactListener = new BD2ContactListener();
        world.setContactListener(contactListener);
        enemy = new Enemy(world);
        poyoPlayer = new Player(world);
        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("Poyo/poyomap.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, PPM);
        //  orthographicCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
        orthographicCamera.position.set(Gdx.graphics.getWidth() / 2f * PPM - 5.3f, Gdx.graphics.getHeight() / 2f * PPM - 2f, 0);
        // orthographicCamera.position.set(poyoPlayer.getPoyoBody().getPosition().x, Gdx.graphics.getHeight() / 2f * PPM -2f,0);
        createGroundBody();


    }


    @Override
    public void render(float delta) {

        ScreenUtils.clear(1, 1, 1, 1);
        orthographicCamera.update();
        batch.setProjectionMatrix(orthographicCamera.combined);
        orthogonalTiledMapRenderer.render();
        calculateCameraPosition(delta);
        orthogonalTiledMapRenderer.setView(orthographicCamera);
        batch.begin();
        poyoPlayer.render(delta, batch, fitViewport);
        enemy.render(delta, batch);
        batch.end();
        //    debugRenderer.render(world, orthographicCamera.combined);
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

    private void calculateCameraPosition(float delta) {
        float cameraPositionFront = orthographicCamera.position.x + 2;
        float cameraPositionBack = orthographicCamera.position.x - 2;

        if (poyoPlayer.getPoyoBody().getLinearVelocity().x > 0 && poyoPlayer.getPoyoBody().getPosition().x >= cameraPositionFront) {
            orthographicCamera.translate(delta * poyoPlayer.getPoyoBody().getLinearVelocity().x, 0);
        }

        if (poyoPlayer.getPoyoBody().getLinearVelocity().x < 0 && poyoPlayer.getPoyoBody().getPosition().x <= cameraPositionBack && orthographicCamera.position.x >= 8.1) {
            orthographicCamera.translate(delta * poyoPlayer.getPoyoBody().getLinearVelocity().x, 0);
        }


      /*  System.out.println("camera x position " + orthographicCamera.position.x);
        System.out.println("camera y position " + orthographicCamera.position.y);
        System.out.println("poyoPosition.X " + poyoPlayer.getPoyoBody().getPosition().x);
        System.out.println(Gdx.graphics.getWidth() * PPM);*/


    }


    private void createGroundBody() {


        BodyDef groundBodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();

        for (MapObject object : tiledMap.getLayers().get(1).getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();


                groundBodyDef.position.x = (rect.x + rect.width / 2) * PPM;
                groundBodyDef.position.y = (rect.y + rect.height / 2) * PPM;
                groundBodyDef.type = BodyDef.BodyType.StaticBody;

                shape.setAsBox(rect.width / 2 * PPM, rect.height / 2 * PPM);


                bodyArrayList.add(world.createBody(groundBodyDef));
                bodyArrayList.get(bodyArrayList.size() - 1).createFixture(shape, 0);
            }
        }

    }

}
