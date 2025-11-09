package io.jbnu.test;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameCharacter {
    public final Sprite sprite;
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public boolean isGrounded = false;

    // 움직임 파라미터(간단/안정)
    public float moveAccel = 900f;
    public float maxSpeedX = 350f;
    public float jumpVelocity = 550f;
    public float width;
    public float height;

    private final Rectangle bounds = new Rectangle();

    public GameCharacter(Sprite sprite, float startX, float startY) {
        this.sprite = sprite;
        this.position.set(startX, startY);
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        syncSpriteToPosition();
    }

    public void moveLeft(float delta) {
        velocity.x -= moveAccel * delta;
        if (velocity.x < -maxSpeedX) velocity.x = -maxSpeedX;
    }

    public void moveRight(float delta) {
        velocity.x += moveAccel * delta;
        if (velocity.x > maxSpeedX) velocity.x = maxSpeedX;
    }

    public void jump() {
        if (isGrounded) {
            velocity.y = jumpVelocity;
            isGrounded = false;
        }
    }

    /**
     * 간단 물리: 중력/마찰/바람 상호작용
     */
    public void applyPhysics(float delta, float gravityY, float friction, float windX) {
        // 중력 & 바람
        velocity.y += gravityY * delta;
        velocity.x += windX * delta;

        // 지면 마찰(수평 속도에만 감쇠)
        if (isGrounded) {
            float sign = Math.signum(velocity.x);
            float mag = Math.abs(velocity.x);
            mag -= friction * delta;
            if (mag < 0) mag = 0;
            velocity.x = mag * sign;
        }

        // 위치 갱신
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        syncSpriteToPosition();
    }

    public Rectangle getBounds() {
        bounds.set(position.x, position.y, width, height);
        return bounds;
    }

    public void landOn(float y) {
        position.y = y;
        velocity.y = 0;
        isGrounded = true;
        syncSpriteToPosition();
    }

    public void stopAtLeft(float xRight) {
        position.x = xRight - width;
        velocity.x = 0;
        syncSpriteToPosition();
    }

    public void stopAtRight(float xLeft) {
        position.x = xLeft;
        velocity.x = 0;
        syncSpriteToPosition();
    }

    private void syncSpriteToPosition() {
        sprite.setPosition(position.x, position.y);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
