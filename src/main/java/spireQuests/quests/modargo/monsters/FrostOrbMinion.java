package spireQuests.quests.modargo.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FrostOrbPassiveEffect;
import spireQuests.quests.modargo.vfx.OrbMinionFlareEffect;

import static spireQuests.Anniv8Mod.makeID;

public class FrostOrbMinion extends OrbMinion {
    public static final String ID = makeID(FrostOrbMinion.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte FROST = 0;

    private final boolean hFlip1 = MathUtils.randomBoolean();
    private final boolean hFlip2 = MathUtils.randomBoolean();
    private float vfxTimer = 1.0F;
    private final float vfxIntervalMin = 0.15F;
    private final float vfxIntervalMax = 0.8F;

    public FrostOrbMinion(float x, float y) {
        super(NAME, ID, null, -4f, -16f, x, y);
        type = EnemyType.NORMAL;

        addMove(FROST, Intent.DEFEND);
    }

    @Override
    public void takeTurn() {
        addToBot(new VFXAction(new OrbMinionFlareEffect(this, OrbMinionFlareEffect.OrbFlareColor.FROST), Settings.FAST_MODE ? 0.0F : 0.1F));
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                addToBot(new GainBlockAction(m, 2 + DefectEliteMonster.getFocus()));
            }
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        setMoveShortcut(FROST, MOVES[FROST]);
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
            AbstractDungeon.effectList.add(new FrostOrbPassiveEffect(this.hb.cX, this.hb.cY));
            if (MathUtils.randomBoolean()) {
                AbstractDungeon.effectList.add(new FrostOrbPassiveEffect(this.hb.cX, this.hb.cY));
            }

            this.vfxTimer = MathUtils.random(this.vfxIntervalMin, this.vfxIntervalMax);
        }

    }

    public void render(SpriteBatch sb) {
        if (this.isDeadOrEscaped()) {
            super.render(sb);
            return;
        }
        sb.setColor(this.c);
        sb.draw(ImageMaster.FROST_ORB_RIGHT, this.hb.cX - 48.0F + this.bobEffect.y / 4.0F, this.hb.cY - 48.0F + this.bobEffect.y / 4.0F, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, 0.0F, 0, 0, 96, 96, this.hFlip1, false);
        sb.draw(ImageMaster.FROST_ORB_LEFT, this.hb.cX - 48.0F + this.bobEffect.y / 4.0F, this.hb.cY - 48.0F - this.bobEffect.y / 4.0F, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, 0.0F, 0, 0, 96, 96, this.hFlip1, false);
        sb.draw(ImageMaster.FROST_ORB_MIDDLE, this.hb.cX - 48.0F - this.bobEffect.y / 4.0F, this.hb.cY - 48.0F + this.bobEffect.y / 2.0F, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, 0.0F, 0, 0, 96, 96, this.hFlip2, false);
        super.render(sb);
    }
}
