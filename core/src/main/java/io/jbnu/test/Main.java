package io.jbnu.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;

    private SpriteBatch batch;
    private Texture playerTexture;
    private Texture platformTexture;
    private Texture coinTexture;
    private Texture flagTexture;
    private Texture pauseTexture;
    private Sound effectSound;

    private GameWorld world;
    private GameState currentState = GameState.RUNNING;

    private OrthographicCamera worldCam;
    private FitViewport worldViewport;

    private OrthographicCamera uiCam;
    private FitViewport uiViewport;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // ▶️ 각각 다른 이미지 로드
        playerTexture   = new Texture(Gdx.files.internal("player.png"));
        platformTexture = new Texture(Gdx.files.internal("platform.png"));
        coinTexture     = new Texture(Gdx.files.internal("coin.png"));
        flagTexture     = new Texture(Gdx.files.internal("flag.png"));
        pauseTexture    = new Texture(Gdx.files.internal("pause.png"));
        effectSound     = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));

        worldCam = new OrthographicCamera();
        worldViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCam);
        worldViewport.apply(true);

        uiCam = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCam);
        uiViewport.apply(true);
        uiCam.position.set(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0);
        uiCam.update();

        // ▶️ GameWorld 생성자 시그니처 변경됨
        world = new GameWorld(
            playerTexture, platformTexture, coinTexture, flagTexture,
            pauseTexture, effectSound,
            WORLD_WIDTH, WORLD_HEIGHT
        );
    }

    @Override
    public void render() {
        handleGlobalInput();

        Color bg = world.getBackgroundColor();
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();
        updateCamera(delta);

        if (currentState == GameState.RUNNING) {
            boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
            boolean jump = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
            world.update(delta, left, right, jump);
        }

        batch.setProjectionMatrix(worldCam.combined);
        batch.begin();
        world.draw(batch);
        batch.end();

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        world.drawHUD(batch, WORLD_WIDTH, WORLD_HEIGHT);
        if (currentState == GameState.PAUSED) {
            batch.draw(pauseTexture, WORLD_WIDTH/2f - 64f, WORLD_HEIGHT/2f - 64f, 128f, 128f);
        }
        batch.end();
    }

    private void handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = (currentState == GameState.RUNNING) ? GameState.PAUSED : GameState.RUNNING;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            world.onEnter();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && world.isAllCleared()) {
            world.resetAll();
            currentState = GameState.RUNNING;
        }
    }

    private void updateCamera(float delta) {
        float lerp = 5f * delta;
        float halfW = WORLD_WIDTH / 2f;
        float halfH = WORLD_HEIGHT / 2f;

        float targetX = world.getPlayerPosition().x + 32f;
        float targetY = world.getPlayerPosition().y + 32f;

        targetX = Math.max(halfW, Math.min(targetX, world.getWorldWidth() - halfW));
        targetY = Math.max(halfH, Math.min(targetY, world.getWorldHeight() - halfH));

        worldCam.position.x += (targetX - worldCam.position.x) * lerp;
        worldCam.position.y += (targetY - worldCam.position.y) * lerp;
        worldCam.update();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        uiCam.position.set(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0);
        uiCam.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
        platformTexture.dispose();
        coinTexture.dispose();
        flagTexture.dispose();
        pauseTexture.dispose();
        effectSound.dispose();
        world.dispose();

    }
}
