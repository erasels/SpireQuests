package spireQuests.quests.coda.monsters;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeDur;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeIntensity;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LaserBeamEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.actions.WaitMoreAction;
import spireQuests.quests.coda.powers.MonsterSpiritShieldPower;
import spireQuests.util.Wiz;
public class CharadeMonster extends AbstractSQMonster {

    public static final String ID = makeID(CharadeMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    private static final String NAME = monsterStrings.NAME;

    private static final int HP_MIN = 300;
    private static final int HP_MAX = 325;
    private static final float HB_W = 420.0F;
    private static final float HB_H = 250.0F; //534

    private static final Byte IRONCLAD = 0, SILENT = 1, DEFECT = 2, WATCHER = 3, WAKEUP = 4;
    private static final float DROP_DUR = 0.5F;

    public static enum OrbColor {
        NONE,
        RED,
        GREEN,
        BLUE,
        PURPLE
    }

    
    private OrbColor currColor;
    public ArrayList<OrbColor> moveOrder;
    private int greenAttackAmount;
    private int redDebuffAmount;
    private int purpleDefendBuff;
    private int greenDebuffAmount;
    private int turnsTaken;
    private int blueBuffAmount;
    private int purpleAttackBuff;
    private boolean isAwake;
    private int redBlockAmount;
    private int blueDamageAmount;
    private boolean redDebuffUpgraded;
    private boolean doneWaking;
    private float dropTime = 0.0F;
    private int greenDamageAmount;
    
    public CharadeMonster() {
        this(0, 200);
    }

    public CharadeMonster(float x, float y) {
        super(NAME, ID, 300, 0.0F, 125.0F, HB_W, HB_H, null, x, y);
        setHp(calcAscensionTankiness(HP_MIN), calcAscensionTankiness(HP_MAX));
        
        type = EnemyType.ELITE;

        this.isAwake = false;
        this.doneWaking = false;

        this.turnsTaken = 0;
        this.moveOrder = getMoveOrder();

        this.redBlockAmount = 30;
        this.redDebuffAmount = 3;
        this.redDebuffUpgraded = false;
        
        this.greenDamageAmount = 4;
        this.greenAttackAmount = 7;
        this.greenDebuffAmount = 2;
        
        this.blueDamageAmount = 40;
        this.blueBuffAmount = 1;
        
        this.purpleDefendBuff = 5;
        this.purpleAttackBuff = 2;
        
        if (AbstractDungeon.ascensionLevel >= 3) {
            this.redDebuffAmount = 4;

            this.greenAttackAmount = 8;

            this.blueDamageAmount = 45;
        }
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.redBlockAmount = 35;
            this.purpleDefendBuff = 8;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            this.redDebuffUpgraded = true;

            this.greenDamageAmount = 5;
            this.greenDebuffAmount = 3;

            this.blueBuffAmount = 2;
            this.purpleAttackBuff = 3;
        }

        addMove(IRONCLAD, Intent.DEFEND_DEBUFF);
        addMove(SILENT, Intent.ATTACK_DEBUFF, this.greenDamageAmount, this.greenAttackAmount, true);
        addMove(DEFECT, Intent.ATTACK_BUFF, this.blueDamageAmount);
        addMove(WATCHER, Intent.DEFEND_BUFF);
        addMove(WAKEUP, Intent.UNKNOWN);
        
        this.loadAnimation(makeContributionPath("coda", "charadeOrb/skeleton.atlas"), makeContributionPath("coda", "charadeOrb/skeleton.json"), 1.0F);
        
        /**  Track Info
        0 - Base anims
        1 - Fall Translation
        2 - Fall Rotation
        4 - Color loops
        */

        AnimationState.TrackEntry e1 = this.state.setAnimation(0, "asleep", true);
        AnimationState.TrackEntry e2 = this.state.setAnimation(4, "color_loop", true);

        e1.setTimeScale(1.0F);
        e2.setTimeScale(1.0F);

        stateData.setMix("asleep", "idle", 0.25F);
        stateData.setMix("idle", "attack", 0.4F);
        stateData.setMix("attack", "idle", 0.4F);
        stateData.setMix("idle", "hit", 0.2F);
        stateData.setMix("hit", "idle", 0.2F);
        stateData.setMix("hit", "attack", 0.4F);
        stateData.setMix("attack", "hit", 0.4F);
        stateData.setMix("idle", "attack_long", 0.4F);
        stateData.setMix("attack_long", "idle", 0.4F);
        stateData.setMix("attack_long", "hit", 0.4F);

        stateData.setMix("hit", "idle", 0.2F);

        stateData.setMix("red_loop", "green_loop", 0.4F);
        stateData.setMix("red_loop", "blue_loop", 0.4F);
        stateData.setMix("red_loop", "purple_loop", 0.4F);
        stateData.setMix("green_loop", "red_loop", 0.4F);
        stateData.setMix("green_loop", "blue_loop", 0.4F);
        stateData.setMix("green_loop", "purple_loop", 0.4F);
        stateData.setMix("blue_loop", "red_loop", 0.4F);
        stateData.setMix("blue_loop", "green_loop", 0.4F);
        stateData.setMix("blue_loop", "purple_loop", 0.4F);
        stateData.setMix("purple_loop", "red_loop", 0.4F);
        stateData.setMix("purple_loop", "green_loop", 0.4F);
        stateData.setMix("purple_loop", "blue_loop", 0.4F);
    }

    @Override
    public void usePreBattleAction() {
        if (!this.isAwake) {
            addToBot(new GainBlockAction(this, 100));
        }
    }

    @Override
    protected void getMove(int i) {
        if (this.firstMove) {
            setMoveShortcut(WAKEUP);
            this.firstMove = false;
            return;
        }
        currColor = moveOrder.get(this.turnsTaken % moveOrder.size());
        
        if (AbstractDungeon.player.hasRelic(RunicDome.ID)) {
            changeColor(OrbColor.NONE);
        } else {
            changeColor(currColor);
        }

        switch (currColor) {
            case RED:
                setMoveShortcut(IRONCLAD);
                break;
            case GREEN:
                setMoveShortcut(SILENT);
                break;
            case BLUE:
                setMoveShortcut(DEFECT);
                break;
            case PURPLE:
                addToBot(new ApplyPowerAction(this, this, new MonsterSpiritShieldPower(this, this.purpleDefendBuff)));
                setMoveShortcut(WATCHER);
                break;
            default:
                setMoveShortcut(WAKEUP);
                break;
        }
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "AWAKE":
                this.isAwake = true;
                state.setAnimation(0, "idle", true);
                state.setAnimation(1, "wake2", false);
                state.setAnimation(2, "fall_swing", false);
                break;
            case "ATTACK":
                state.setAnimation(0, "attack", false);
                state.addAnimation(0, "idle", true, 0.0F);
                break;
            case "ATTACK_LONG":
                state.setAnimation(0, "attack_long", false);
                state.addAnimation(0, "idle", true, 0.0F);
                break;
        }
    }
        
        @Override
        public void update() {
            super.update();
            if (this.isAwake && !this.doneWaking) {
                this.dropTime += Gdx.graphics.getDeltaTime();
                float dp = Interpolation.bounceOut.apply(this.dropTime / DROP_DUR);
                float y = MathUtils.lerp(125, -75, dp);
                updateHitbox(hb_x, y, HB_W, HB_H);

                if (dp >= 1.0F) {
                    this.doneWaking = true;
                }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageType.THORNS && info.output - currentBlock > 0) {
            if (!this.isAwake) {
                addToBot(new ChangeStateAction(this, "AWAKE"));
            } else {
                state.setAnimation(0, "hit", false);
                state.addAnimation(0, "idle", true, 0.0F);
            }
        }

        super.damage(info);
    }

    private void changeColor(OrbColor color) {
        AnimationState.TrackEntry e;
        switch (color) {
            case RED:
                e = state.addAnimation(4, "red_loop", true, 2.0F);
                break;
            case GREEN:
                e = state.addAnimation(4, "green_loop", true, 2.0F);
                break;
            case BLUE:
                e = state.addAnimation(4, "blue_loop", true, 2.0F);
                break;
            case PURPLE:
                e = state.addAnimation(4, "purple_loop", true, 2.0F);
                break;
            default:
                e = state.addAnimation(4, "color_loop", true, 2.0F);
                break;
        }
        e.setTimeScale(1.0F);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        this.turnsTaken++;

        switch (nextMove) {
            case 0: // IRONCLAD
                addToBot(new ChangeStateAction(this, "ATTACK"));
                AbstractGameEffect burnVFX = new ScreenOnFireEffect();
                burnVFX.duration = burnVFX.startingDuration = 1.0F;
                addToBot(new VFXAction(burnVFX));
                addToBot(new GainBlockAction(this, this.redBlockAmount));
                AbstractCard burn = new Burn();
                if (this.redDebuffUpgraded) {
                    burn.upgrade();
                }
                addToBot(new MakeTempCardInDrawPileAction(burn, redDebuffAmount, true, true));
                break;
            case 1: // SILENT
                addToBot(new ChangeStateAction(this, "ATTACK"));
                addToBot(new ShakeScreenAction(0.0F, ShakeDur.SHORT, ShakeIntensity.MED));
                for (int i = 0; i < this.greenAttackAmount; i++ ) {
                    addToBot(new DamageAction(Wiz.p(), info, Wiz.getRandomSlash()));
                }
                addToBot(new ApplyPowerAction(Wiz.p(), this, new WeakPower(Wiz.p(), this.greenDebuffAmount, true)));
                break;
            case 2: // DEFECT
                addToBot(new ChangeStateAction(this, "ATTACK_LONG"));
                addToBot(new VFXAction(this, new LaserBeamEffect(this.hb.cX - 262.0F * Settings.scale, this.hb.cY + 269.0F * Settings.scale), 0.5F));
                addToBot(new DamageAction(Wiz.p(), info));
                addToBot(new ApplyPowerAction(this, this, new ArtifactPower(this, blueBuffAmount)));
                break;
            case 3: // WATCHER
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, this.purpleAttackBuff)));
                break;
            case 4: // WAKE UP
                if (!this.isAwake) {
                    addToBot(new WaitAction(1.0f));
                    addToBot(new ChangeStateAction(this, "AWAKE"));
                }
            }

        addToBot(new RollMoveAction(this));
    }

    private ArrayList<OrbColor> getMoveOrder() {
        ArrayList<OrbColor> ret = new ArrayList();
        if (CardCrawlGame.isInARun()) {
            ArrayList<OrbColor> attackColors = new ArrayList();
            ArrayList<OrbColor> defendColors = new ArrayList();
            attackColors.add(OrbColor.GREEN);
            attackColors.add(OrbColor.BLUE);
            Collections.shuffle(attackColors, new Random(AbstractDungeon.aiRng.randomLong()));
            defendColors.add(OrbColor.RED);
            defendColors.add(OrbColor.PURPLE);
            Collections.shuffle(defendColors, new Random(AbstractDungeon.aiRng.randomLong()));
            ret.add(attackColors.get(0));
            ret.add(defendColors.get(0));
            ret.add(attackColors.get(1));
            ret.add(defendColors.get(1));
            return ret;
        }
        ret.add(OrbColor.RED);
        ret.add(OrbColor.GREEN);
        ret.add(OrbColor.PURPLE);
        ret.add(OrbColor.BLUE);
        return ret;

    }

}
