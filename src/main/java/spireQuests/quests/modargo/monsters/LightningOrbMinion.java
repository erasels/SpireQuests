package spireQuests.quests.modargo.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningOrbPassiveEffect;

import static spireQuests.Anniv8Mod.makeID;

public class LightningOrbMinion extends OrbMinion {
    public static final String ID = makeID(LightningOrbMinion.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte LIGHTNING = 0;

    private float vfxTimer = 1.0F;

    public LightningOrbMinion(float x, float y) {
        super(NAME, ID, ImageMaster.ORB_LIGHTNING, -4f, -16f, x, y);
        type = EnemyType.NORMAL;

        addMove(LIGHTNING, Intent.ATTACK, 3);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.NONE, false, true));
        if (!Settings.FAST_MODE) {
            this.addToTop(new WaitAction(0.1F));
        }
        addToBot(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY)));
        addToBot(new VFXAction(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.LIGHTNING, true)));
        addToBot(new SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        setMoveShortcut(LIGHTNING, MOVES[LIGHTNING]);
    }

    @Override
    public void update() {
        super.update();
        if (this.isDeadOrEscaped()) {
            return;
        }
        this.angle += Gdx.graphics.getDeltaTime() * 180.0F;
        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0F) {
            AbstractDungeon.effectList.add(new LightningOrbPassiveEffect(this.hb.cX, this.hb.cY));
            if (MathUtils.randomBoolean()) {
                AbstractDungeon.effectList.add(new LightningOrbPassiveEffect(this.hb.cX, this.hb.cY));
            }

            this.vfxTimer = MathUtils.random(0.15F, 0.8F);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.isDeadOrEscaped()) {
            super.render(sb);
            return;
        }
        this.shineColor.a = this.c.a / 2.0F;
        sb.setColor(this.shineColor);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale + MathUtils.sin(this.angle / 12.566371F) * 0.05F + 0.19634955F, this.scale * 1.2F, this.angle, 0, 0, 96, 96, false, false);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.2F, this.scale + MathUtils.sin(this.angle / 12.566371F) * 0.05F + 0.19634955F, -this.angle, 0, 0, 96, 96, false, false);
        sb.setBlendFunction(770, 771);
        sb.setColor(this.c);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, this.angle / 12.0F, 0, 0, 96, 96, false, false);
        super.render(sb);
    }
}
