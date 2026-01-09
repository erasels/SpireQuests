package spireQuests.quests.modargo.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;
import spireQuests.abstracts.AbstractSQMonster;

public abstract class OrbMinion extends AbstractSQMonster {
    public static int MIN_HEALTH = 28;
    public static int A8_MIN_HEALTH = 30;
    public static int MAX_HEALTH = 31;
    public static int A8_MAX_HEALTH = 33;

    protected Color c;
    protected Color shineColor;
    protected Texture img;
    protected BobEffect bobEffect;
    protected float angle;
    protected float scale;
    protected float channelAnimTimer;
    public float tX;
    public float tY;

    public OrbMinion(String name, String id, Texture image, float hb_x, float hb_y, float offsetX, float offsetY) {
        super(name, id, MIN_HEALTH, hb_x, hb_y, 96f, 96f, null, offsetX, offsetY);

        setHp(AbstractDungeon.ascensionLevel < 8 ? MIN_HEALTH : A8_MIN_HEALTH, AbstractDungeon.ascensionLevel < 8 ? MAX_HEALTH : A8_MAX_HEALTH);

        this.img = image;
        this.c = Settings.CREAM_COLOR.cpy();
        this.shineColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
        this.bobEffect = new BobEffect(3.0F * Settings.scale, 3.0F);
        this.channelAnimTimer = 0.5F;
        this.tX = this.hb.cX;
        this.tY = this.hb.cY;
    }

    @Override
    public void update() {
        super.update();
        if (this.isDeadOrEscaped()) {
            return;
        }
        this.bobEffect.update();
        this.hb.cX = MathHelper.orbLerpSnap(this.hb.cX, AbstractDungeon.player.animX + this.tX);
        this.hb.cY = MathHelper.orbLerpSnap(this.hb.cY, AbstractDungeon.player.animY + this.tY);
        if (this.channelAnimTimer != 0.0F) {
            this.channelAnimTimer -= Gdx.graphics.getDeltaTime();
            if (this.channelAnimTimer < 0.0F) {
                this.channelAnimTimer = 0.0F;
            }
        }

        this.c.a = Interpolation.pow2In.apply(1.0F, 0.01F, this.channelAnimTimer / 0.5F);
        this.scale = Interpolation.swingIn.apply(Settings.scale, 0.01F, this.channelAnimTimer / 0.5F);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic(RunicDome.ID) && this.intent != Intent.NONE && !Settings.hideCombatElements) {
                this.renderIntentVfxBehind(sb);
                this.renderIntent(sb);
                this.renderIntentVfxAfter(sb);
                this.renderDamageRange(sb);
            }

            this.hb.render(sb);
            this.intentHb.render(sb);
            this.healthHb.render(sb);

            if (!AbstractDungeon.player.isDead) {
                this.renderHealth(sb);
                this.renderName(sb);
            }
        }
    }
}
