package io.jbnu.test;

import com.badlogic.gdx.math.Rectangle;

public class MovingPlatform {
    public final Rectangle rect;
    public final boolean horizontal;
    public final float origin;
    public final float range;
    public final float speed;

    private int dir = 1;

    public MovingPlatform(Rectangle rect, boolean horizontal, float range, float speed) {
        this.rect = rect;
        this.horizontal = horizontal;
        this.range = range;
        this.speed = speed;
        this.origin = horizontal ? rect.x : rect.y;
    }

    public void update(float delta) {
        if (horizontal) {
            rect.x += speed * dir * delta;
            if (rect.x > origin + range) { rect.x = origin + range; dir = -1; }
            if (rect.x < origin - range) { rect.x = origin - range; dir = 1; }
        } else {
            rect.y += speed * dir * delta;
            if (rect.y > origin + range) { rect.y = origin + range; dir = -1; }
            if (rect.y < origin - range) { rect.y = origin - range; dir = 1; }
        }
    }
}
