package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "touch";
        }

        @Override
        public String getName() {
            return "Touch";
        }

        @Override
        public String getDescription() {
            return "Use virtual buttons to control your vehicle.";
        }

        @Override
        public GameInputHandler create() {
            return new TouchInputHandler();
        }
    }

    private GameInput mInput = new GameInput();
    private HudButton mLeftButton, mRightButton, mBrakeButton, mBonusButton;

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.triggeringBonus = false;
        mInput.braking = false;
        mInput.accelerating = true;

        mLeftButton.setPressed(false);
        mRightButton.setPressed(false);
        mBrakeButton.setPressed(false);
        mBonusButton.setPressed(false);

        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i);
            float y = Gdx.graphics.getHeight() - Gdx.input.getY(i);
            if (isActorHit(mBonusButton, x, y)) {
                mBonusButton.setPressed(true);
                mInput.triggeringBonus = true;
            } else {
                if (isActorHit(mLeftButton, x, 0)) {
                    mLeftButton.setPressed(true);
                    mInput.direction = 1;
                } else if (isActorHit(mRightButton, x, 0)) {
                    mRightButton.setPressed(true);
                    mInput.direction = -1;
                } else if (isActorHit(mBrakeButton, x, 0)) {
                    mBrakeButton.setPressed(true);
                    mInput.accelerating = false;
                    mInput.braking = true;
                }
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, Group root) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        root.addActor(group);

        mLeftButton = new HudButton(assets, "left");
        mRightButton = new HudButton(assets, "right");
        mBrakeButton = new HudButton(assets, "back");
        mBonusButton = new HudButton(assets, "square");

        group.addPositionRule(mLeftButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
        group.addPositionRule(mRightButton, Anchor.BOTTOM_LEFT, mLeftButton, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBrakeButton, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBonusButton, Anchor.BOTTOM_RIGHT, mBrakeButton, Anchor.TOP_RIGHT);
    }

    @Override
    public void setBonus(Bonus bonus) {
        if (bonus == null) {
            mBonusButton.setVisible(false);
        } else {
            mBonusButton.setVisible(true);
            mBonusButton.setIcon(bonus.getIconRegion());
        }
    }

    private static boolean isActorHit(Actor actor, float screenX, float screenY) {
        Stage stage = actor.getStage();
        float x = screenX * stage.getWidth() / Gdx.graphics.getWidth();
        float y = screenY * stage.getHeight() / Gdx.graphics.getHeight();
        return actor.hit(x - actor.getX(), y - actor.getY(), false) != null;
    }
}
