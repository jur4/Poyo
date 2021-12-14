package com.mygdx.poyo;

import com.badlogic.gdx.physics.box2d.*;

public class BD2ContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();



        System.out.println(a.getUserData() + " " + b.getUserData());
        if(isContactBetweenPlayerAndEnemy(a,b)) {
            Player player = (Player) b.getUserData();
            Enemy enemy = (Enemy) a.getUserData();

            System.out.println("player get a hit");
        }

    }



    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public boolean isContactBetweenPlayerAndEnemy(Fixture a, Fixture b) {
        return (a.getUserData() instanceof Player && b.getUserData() instanceof Enemy || a.getUserData() instanceof Enemy && b.getUserData() instanceof Player);
    }
}
