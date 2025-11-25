package spireQuests.quests.modargo.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.RoomTintEffect;

public class BlackstaffCurseEffect extends AbstractGameEffect {
    private final float x;
    private final float y;
    private int count;
    private float stakeTimer = 0.0F;

    private static final int AMOUNT = 6;

    public BlackstaffCurseEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.count = 6;
    }

    public void update() {
        this.stakeTimer -= Gdx.graphics.getDeltaTime();
        if (this.stakeTimer < 0.0F) {
            if (this.count == 6) {
                CardCrawlGame.sound.playA("ATTACK_HEAVY", -0.5F);
                AbstractDungeon.effectsQueue.add(new RoomTintEffect(Color.RED.cpy(), 0.2F));
                AbstractDungeon.effectsQueue.add(new BorderLongFlashEffect(new Color(0.9F, 0.1F, 0.1F, 0.7F)));
            }

            AbstractDungeon.effectsQueue.add(new BlackstaffStakeEffect(this.x + MathUtils.random(-50.0F, 50.0F) * Settings.scale, this.y + MathUtils.random(-60.0F, 60.0F) * Settings.scale));
            this.stakeTimer = 0.04F;
            --this.count;
            if (this.count == 0) {
                this.isDone = true;
            }
        }
    }

    public void render(SpriteBatch sb) {}

    public void dispose() {}
}
