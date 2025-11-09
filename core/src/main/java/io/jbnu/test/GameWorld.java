package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Music;

public class GameWorld {

    private final Texture playerTex;
    private final Texture platformTex;
    private final Texture coinTex;
    private final Texture flagTex;
    private final Texture pauseTex;
    private final Sound effectSound;
    private Music bgm1;
    private Music bgm2;
    private Music wind;

    private final float WORLD_W;
    private final float WORLD_H;

    private GameCharacter player;

    private final Array<LevelConfig> levels = new Array<>();
    private int currentLevelIndex = 0;
    private LevelConfig current;

    private final Array<Rectangle> platforms = new Array<>();
    private final Array<MovingPlatform> movingPlatforms = new Array<>();
    private final Array<CoinObject> coins = new Array<>();
    private FlagObject flag;

    private int collectedCoins = 0;
    private boolean levelCleared = false;
    private boolean allCleared = false;

    private final BitmapFont font = new BitmapFont();

    private static final float FLOOR_Y = 40f;
    private static final float EPS = 0.0001f;
    // ── Gust(돌풍) 설정 ─────────────────────
    private float gustInterval = 3f;   // 주기(3초)
    private float gustDuration = 0.8f;   // 돌풍 지속시간
    private float gustTimer = 0f;      // 경과 타이머
    private boolean inGust = false;    // 현재 돌풍 중인가
    private float gustStrength = 1000f; // 돌풍이 추가로 주는 바람 세기(px/s^2)
    private float currentWindXApplied = 0f; // 이번 프레임에 실제로 적용할 windX


    public GameWorld(Texture playerTex, Texture platformTex, Texture coinTex, Texture flagTex,
                     Texture pauseTex, Sound effectSound,
                     float worldWidth, float worldHeight) {
        this.playerTex = playerTex;
        this.platformTex = platformTex;
        this.coinTex = coinTex;
        this.flagTex = flagTex;
        this.pauseTex = pauseTex;
        this.effectSound = effectSound;
        this.WORLD_W = worldWidth;
        this.WORLD_H = worldHeight;

        bgm1 = Gdx.audio.newMusic(Gdx.files.internal("bgm1.mp3"));
        bgm2 = Gdx.audio.newMusic(Gdx.files.internal("bgm2.mp3"));
        bgm1.setLooping(true);
        bgm2.setLooping(true);
        wind = Gdx.audio.newMusic(Gdx.files.internal("wind.mp3")); // 또는 wind.ogg
        wind.setLooping(true);
        wind.setVolume(1f);

        initLevels();
        loadLevel(0);
    }

    private void initLevels() {
        LevelConfig L1 = new LevelConfig(-900f, 8f, 0f, new Color(0.55f, 0.80f, 1f, 1f), 3, WORLD_W*2f, WORLD_H);
        L1.platforms.add(new Rectangle(0, 0, L1.worldWidth, FLOOR_Y));
        L1.platforms.add(new Rectangle(300, 140, 220, 30));
        L1.platforms.add(new Rectangle(650, 240, 220, 30));
        L1.platforms.add(new Rectangle(980, 360, 240, 30));
        L1.coinPositions.add(new Vector2(350, 180));
        L1.coinPositions.add(new Vector2(700, 280));
        L1.coinPositions.add(new Vector2(1030, 400));
        L1.coinPositions.add(new Vector2(1150, 400));
        L1.flagRect = new Rectangle(1400, FLOOR_Y, 40, 160);

        LevelConfig L2 = new LevelConfig(-1200f, 4f, 0f, new Color(1f, 0.65f, 0.45f, 1f), 5, WORLD_W*2.2f, WORLD_H);
        L2.platforms.add(new Rectangle(0, 0, L2.worldWidth, FLOOR_Y));
        L2.platforms.add(new Rectangle(280, 110, 220, 30));
        L2.platforms.add(new Rectangle(680, 280, 200, 30));
        L2.platforms.add(new Rectangle(960, 420, 220, 30));

        L2.movingPlatforms.add(new MovingPlatform(new Rectangle(480, 220, 160, 28), true, 300, 120));
        L2.movingPlatforms.add(new MovingPlatform(new Rectangle(1080, 370, 160, 28), true, 300, 120));
        L2.coinPositions.add(new Vector2(300, 200));
        L2.coinPositions.add(new Vector2(520, 260));
        L2.coinPositions.add(new Vector2(760, 320));
        L2.coinPositions.add(new Vector2(980, 460));
        L2.coinPositions.add(new Vector2(1100, 460));
        L2.coinPositions.add(new Vector2(1300, 180));
        L2.flagRect = new Rectangle(1650, FLOOR_Y, 40, 160);

        LevelConfig L3 = new LevelConfig(-1000f, 6f, -500f, new Color(0.08f, 0.08f, 0.15f, 1f), 6, WORLD_W*2.4f, WORLD_H);
        L3.platforms.add(new Rectangle(0, 0, L3.worldWidth, FLOOR_Y));
        L3.platforms.add(new Rectangle(260, 140, 200, 28));
        L3.platforms.add(new Rectangle(560, 240, 160, 28));
        L3.platforms.add(new Rectangle(840, 340, 160, 28));
        L3.platforms.add(new Rectangle(1120, 440, 200, 28));
        L3.movingPlatforms.add(new MovingPlatform(new Rectangle(1420, 260, 160, 28), false, 120, 110));
        L3.coinPositions.add(new Vector2(280, 180));
        L3.coinPositions.add(new Vector2(600, 280));
        L3.coinPositions.add(new Vector2(880, 380));
        L3.coinPositions.add(new Vector2(1150, 480));
        L3.coinPositions.add(new Vector2(1450, 300));
        L3.coinPositions.add(new Vector2(1550, 420));
        L3.coinPositions.add(new Vector2(1700, 180));
        L3.flagRect = new Rectangle(1850, FLOOR_Y, 40, 160);

        levels.addAll(L1, L2, L3);
    }

    public void loadLevel(int idx) {
        if (idx < 0 || idx >= levels.size) throw new IllegalArgumentException("Invalid level index");
        currentLevelIndex = idx;
        current = levels.get(idx);

        Sprite p = new Sprite(playerTex);
        p.setSize(64, 64);
        player = new GameCharacter(p, 40, FLOOR_Y + 20);

        platforms.clear();
        movingPlatforms.clear();
        coins.clear();
        collectedCoins = 0;
        levelCleared = false;
        allCleared = false;

        platforms.addAll(current.platforms);
        movingPlatforms.addAll(current.movingPlatforms);

        for (Vector2 pos : current.coinPositions) {
            float fallSpeed = 0f;
            CoinObject c = new CoinObject(coinTex, pos.x, pos.y, fallSpeed);
            coins.add(c);
        }

        flag = new FlagObject(new Rectangle(current.flagRect), flagTex);
        gustTimer = 0f;
        inGust = false;
        currentWindXApplied = current.windX;

        stopBGM();
        stopWind();
        if (idx == 0 || idx == 1) {
            bgm1.play();
        } else if (idx == 2) {
            bgm2.play();
            wind.play();
        }
    }

    public void nextLevel() {
        if (currentLevelIndex + 1 < levels.size) {
            loadLevel(currentLevelIndex + 1);
        } else {
            levelCleared = true;
            allCleared = true;
        }
    }

    public void resetAll() {
        loadLevel(0);
    }

    public void update(float delta, boolean left, boolean right, boolean jump) {
        if (levelCleared) return;

        // 입력
        if (left)  player.moveLeft(delta);
        if (right) player.moveRight(delta);
        if (jump)  player.jump();

        // 이동 플랫폼
        for (MovingPlatform mp : movingPlatforms) mp.update(delta);

        // 입력 없고 지면이면 미끄럼 방지
        if (!left && !right && player.isGrounded) {
            player.velocity.x = 0f;
        }

        // 이전 위치 저장
        float prevX = player.position.x;
        float prevY = player.position.y;

        // ── 돌풍 상태 업데이트 ─────────────────────
        currentWindXApplied = current.windX;

        if (currentLevelIndex == 2) { // Level 3만 돌풍 On
            gustTimer += delta;
            // 3초 주기 중 마지막 1초에 돌풍(= 2.0s ~ 3.0s 구간)
            float t = gustTimer % gustInterval;
            inGust = (t >= (gustInterval - gustDuration)); // true면 돌풍 구간

            if (inGust) {
                // 돌풍은 기존 바람 방향을 따르며, 세기를 추가
                if (wind != null) wind.setVolume(2.0f);
                float dir = (current.windX >= 0f) ? 1f : -1f;
                currentWindXApplied = current.windX + dir * gustStrength;
            }
            else{
                if (wind != null) wind.setVolume(1f);
            }
        }


        // 물리 적용
        player.applyPhysics(delta, current.gravityY, current.friction, currentWindXApplied);

        // 상단/좌우 월드 경계 1차 클램프
        if (player.position.y + player.height > current.worldHeight) {
            player.position.y = current.worldHeight - player.height;
            player.velocity.y = 0;
        }
        if (player.position.x < 0) { player.position.x = 0; }
        if (player.position.x + player.width > current.worldWidth) {
            player.position.x = current.worldWidth - player.width;
        }
        // 축 분리 충돌 (이전 좌표 기반)

        // Y축 보정: X는 prevX(이전 X)로 고정한 '가상 히트박스'로 판정
        float dy = player.position.y - prevY;
        player.isGrounded = false;

        // 기본 바닥
        if (player.position.y < FLOOR_Y) {
            player.landOn(FLOOR_Y);
        }

        Rectangle pbY = new Rectangle(prevX, player.position.y, player.width, player.height);

        // 고정 플랫폼 (Y축)
        for (Rectangle r : platforms) {
            if (Intersector.overlaps(pbY, r)) {
                if (dy < 0) { // 아래로 이동 중: 착지
                    float top = r.y + r.height;
                    player.landOn(top + EPS);
                    pbY.setPosition(prevX, player.position.y);
                } else if (dy > 0) { // 위로 이동 중: 머리 부딪힘
                    float bottom = r.y;
                    player.position.y = bottom - player.height - EPS;
                    player.velocity.y = 0;
                    pbY.setPosition(prevX, player.position.y);
                }
            }
        }
        // 이동 플랫폼 (Y축)
        for (MovingPlatform mp : movingPlatforms) {
            Rectangle r = mp.rect;
            if (Intersector.overlaps(pbY, r)) {
                if (dy < 0) {
                    float top = r.y + r.height;
                    player.landOn(top + EPS);
                    pbY.setPosition(prevX, player.position.y);
                } else if (dy > 0) {
                    float bottom = r.y;
                    player.position.y = bottom - player.height - EPS;
                    player.velocity.y = 0;
                    pbY.setPosition(prevX, player.position.y);
                }
            }
        }

        // X축 보정: Y는 보정된 현재 Y로, X만 이동한 '가상 히트박스'로 판정
        float dx = player.position.x - prevX;
        Rectangle pbX = new Rectangle(player.position.x, player.position.y, player.width, player.height);

        boolean resolvedX = false;
        for (Rectangle r : platforms) {
            if (Intersector.overlaps(pbX, r)) {
                if (dx > 0) { // 오른쪽으로 이동 중
                    player.position.x = r.x - player.width - EPS;
                } else if (dx < 0) { // 왼쪽으로 이동 중
                    player.position.x = r.x + r.width + EPS;
                }
                player.velocity.x = 0;
                resolvedX = true;
                break; // ★ 한 번만 보정
            }
        }
        if (!resolvedX) {
            for (MovingPlatform mp : movingPlatforms) {
                Rectangle r = mp.rect;
                if (Intersector.overlaps(pbX, r)) {
                    if (dx > 0) {
                        player.position.x = r.x - player.width - EPS;
                    } else if (dx < 0) {
                        player.position.x = r.x + r.width + EPS;
                    }
                    player.velocity.x = 0;
                    break; // ★ 한 번만 보정
                }
            }
        }

        // 최종 월드 경계 재확인
        if (player.position.x < 0) { player.position.x = 0; player.velocity.x = 0; }
        if (player.position.x + player.width > current.worldWidth) {
            player.position.x = current.worldWidth - player.width; player.velocity.x = 0;
        }
        if (player.position.y < FLOOR_Y) {
            player.landOn(FLOOR_Y);
        }
        if (player.position.y + player.height > current.worldHeight) {
            player.position.y = current.worldHeight - player.height; player.velocity.y = 0;
        }

        // 코인 획득
        for (int i = coins.size - 1; i >= 0; i--) {
            CoinObject c = coins.get(i);
            if (Intersector.overlaps(new Rectangle(player.position.x, player.position.y, player.width, player.height),
                c.getBounds())) {
                coins.removeIndex(i);
                collectedCoins++;
                if (effectSound != null) effectSound.play();
            }
        }

        // 깃발
        Rectangle pb = new Rectangle(player.position.x, player.position.y, player.width, player.height);
        if (Intersector.overlaps(pb, flag.bounds)) {
            if (collectedCoins >= current.minCoinsToClear) {
                levelCleared = true;
            }
        }

    }

    public void draw(SpriteBatch batch) {
        for (Rectangle r : platforms)
            batch.draw(platformTex, r.x, r.y, r.width, r.height);

        for (MovingPlatform mp : movingPlatforms)
            batch.draw(platformTex, mp.rect.x, mp.rect.y, mp.rect.width, mp.rect.height);

        for (CoinObject c : coins) c.draw(batch);

        flag.draw(batch);
        player.draw(batch);
    }

    public void drawHUD(SpriteBatch batch, float screenW, float screenH) {
        String ltxt = "LEVEL " + (currentLevelIndex + 1) + " / " + levels.size;
        String ctxt = "COIN " + collectedCoins + " / NEED " + current.minCoinsToClear;
        font.draw(batch, ltxt, 20, screenH - 20);
        font.draw(batch, ctxt, 20, screenH - 50);

        if (allCleared)
            font.draw(batch, "ALL LEVELS CLEAR! Press R to restart", 320, screenH/2f + 40);
        else if (levelCleared)
            font.draw(batch, "LEVEL CLEAR! Press ENTER to continue", 320, screenH/2f + 40);
    }

    public Color getBackgroundColor() { return current.backgroundColor; }
    public boolean isLevelCleared() { return levelCleared; }
    public boolean isAllCleared() { return allCleared; }

    public Vector2 getPlayerPosition() { return player.position; }
    public float getWorldWidth() { return current.worldWidth; }
    public float getWorldHeight() { return current.worldHeight; }

    public void onEnter() {
        if (levelCleared && !allCleared) nextLevel();
    }
    public void dispose() {
        if (bgm1 != null) bgm1.dispose();
        if (bgm2 != null) bgm2.dispose();
        if (wind != null) wind.dispose();
    }
    private void stopBGM() {
        if (bgm1.isPlaying()) bgm1.stop();
        if (bgm2.isPlaying()) bgm2.stop();
    }
    private void stopWind() {
        if (wind != null && wind.isPlaying()) wind.stop();
    }

}
