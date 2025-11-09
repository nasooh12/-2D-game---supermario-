package io.jbnu.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LevelConfig {
    public final float gravityY;
    public final float friction;
    public final float windX;
    public final Color backgroundColor;
    public final int minCoinsToClear;

    public final Array<Rectangle> platforms = new Array<>();
    public final Array<MovingPlatform> movingPlatforms = new Array<>();
    public final Array<Vector2> coinPositions = new Array<>();
    public Rectangle flagRect;

    public final float worldWidth;
    public final float worldHeight;

    public LevelConfig(float gravityY, float friction, float windX,
                       Color bg, int minCoinsToClear,
                       float worldWidth, float worldHeight) {
        this.gravityY = gravityY;
        this.friction = friction;
        this.windX = windX;
        this.backgroundColor = bg;
        this.minCoinsToClear = minCoinsToClear;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
}
