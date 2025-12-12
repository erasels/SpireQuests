package spireQuests.quests.modargo.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.vfx.combat.DarkOrbActivateEffect;
import com.megacrit.cardcrawl.vfx.combat.DarkOrbPassiveEffect;
import spireQuests.quests.modargo.vfx.OrbMinionFlareEffect;

import static spireQuests.Anniv8Mod.makeID;

public class DarkOrbMinion extends OrbMinion {
    public static final String ID = makeID(DarkOrbMinion.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final float NUM_X_OFFSET = 20.0F * Settings.scale;
    private static final float NUM_Y_OFFSET = -12.0F * Settings.scale;

    private static final byte GATHER = 0, EVOKE = 1;

    private static final int PASSIVE_GROWTH = 6;

    private float vfxTimer = 1.0F;

    private int evokeDamage = PASSIVE_GROWTH;

    public DarkOrbMinion(float x, float y) {
        super(NAME, ID, ImageMaster.ORB_DARK, -4f, -16f, x, y);
        type = EnemyType.NORMAL;

        addMove(GATHER, Intent.BUFF);
        addMove(EVOKE, Intent.ATTACK, 0);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        switch (nextMove) {
            case GATHER:
                addToBot(new VFXAction(new OrbMinionFlareEffect(this, OrbMinionFlareEffect.OrbFlareColor.DARK), Settings.FAST_MODE ? 0.0F : 0.1F));
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        DarkOrbMinion.this.evokeDamage += getGrowthAmount();
                        this.isDone = true;
                    }
                });
                break;
            case EVOKE:
                addToBot(new SFXAction("ORB_DARK_EVOKE"));
                addToBot(new VFXAction(new DarkOrbActivateEffect(this.hb.cX, this.hb.cY), 0.3f));
                addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.FIRE));
                addToBot(new SuicideAction(this));
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    private int getGrowthAmount() {
        return PASSIVE_GROWTH + DefectEliteMonster.getFocus();
    }

    @Override
    protected void getMove(int i) {
        if (this.lastMove(GATHER) && this.lastMoveBefore(GATHER)) {
            this.moves.get(EVOKE).baseDamage = this.evokeDamage;
            setMoveShortcut(EVOKE, MOVES[EVOKE]);
        }
        else {
            setMoveShortcut(GATHER, MOVES[GATHER]);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.isDeadOrEscaped()) {
            return;
        }
        this.angle += Gdx.graphics.getDeltaTime() * 120.0F;
        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0F) {
            AbstractDungeon.effectList.add(new DarkOrbPassiveEffect(this.hb.cX, this.hb.cY));
            this.vfxTimer = 0.25F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.isDeadOrEscaped()) {
            super.render(sb);
            return;
        }
        sb.setColor(this.c);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, this.angle, 0, 0, 96, 96, false, false);
        this.shineColor.a = this.c.a / 3.0F;
        sb.setColor(this.shineColor);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.2F, this.scale * 1.2F, this.angle / 1.2F, 0, 0, 96, 96, false, false);
        sb.draw(this.img, this.hb.cX - 48.0F, this.hb.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.5F, this.scale * 1.5F, this.angle / 1.4F, 0, 0, 96, 96, false, false);
        sb.setBlendFunction(770, 771);
        this.renderText(sb);
        super.render(sb);
    }

    protected void renderText(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.getGrowthAmount()), this.hb.cX + NUM_X_OFFSET, this.hb.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET - 4.0F * Settings.scale, new Color(0.2F, 1.0F, 1.0F, this.c.a), 0.7f);
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.evokeDamage), this.hb.cX + NUM_X_OFFSET, this.hb.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET + 20.0F * Settings.scale, this.c, 0.7f);
    }
}
