package io.jbnu.test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class FlagObject {
    public final Rectangle bounds;
    private final Texture tex;

    public FlagObject(Rectangle r, Texture tex) {
        this.bounds = r;
        this.tex = tex;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tex, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
