package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Racer mRacer;
    private final HealthComponent mHealthComponent;

    private GameInputHandler mInputHandler;

    public PlayerPilot(Assets assets, GameWorld gameWorld, Racer racer, GameInputHandler inputHandler) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mHealthComponent = mRacer.getHealthComponent();
        mInputHandler = inputHandler;
    }

    public void createHudActors(Group root) {
        mInputHandler.createHud(mAssets, root);
    }

    @Override
    public void act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        if (mHealthComponent.getHealth() == 0) {
            vehicle.setBraking(true);
            vehicle.setAccelerating(false);
            return;
        }

        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            BonusIndicator bonusIndicator = mInputHandler.getBonusIndicator();
            if (mRacer.getBonus() != bonusIndicator.getBonus()) {
                bonusIndicator.setBonus(mRacer.getBonus());
            }

            GameInput input = mInputHandler.getGameInput();
            vehicle.setDirection(input.direction);
            vehicle.setAccelerating(input.accelerating);
            vehicle.setBraking(input.braking);
            if (input.triggeringBonus) {
                mRacer.triggerBonus();
            }
        }
    }
}