package spireQuests.quests.modargo.monsters;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.gk.vfx.FakePlayCardEffect;
import spireQuests.quests.modargo.powers.FakeFocusPower;
import spireQuests.quests.modargo.powers.OrbMinionDefectFocusDamageIncreasePower;
import spireQuests.util.Wiz;

import java.util.Arrays;
import java.util.List;

import static spireQuests.Anniv8Mod.makeID;

public class DefectEliteMonster extends AbstractSQMonster {
    public static final String ID = makeID(DefectEliteMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CORE_SURGE = 0, CAPACITOR = 1, THUNDER_STRIKE = 2, BALL_LIGHTNING = 3, ZAP = 4;

    private static final int HEALTH = 275;

    private final int biasedCognitionAmount;
    private final int defragmentAmount;
    private final int leapBlock;
    private final int forceFieldBlock;

    private final float[] x = new float[]{-300f, -150f, 0f, 150f, 300f};
    private final float[] y = new float[]{200f, 300f, 375f, 300f, 200f};
    private final AbstractMonster[] orbs = new AbstractMonster[5];

    private int lightningSummons = 0;

    public DefectEliteMonster() {
        this(0, 0);
    }

    public DefectEliteMonster(float x, float y) {
        super(NAME, ID, HEALTH, -4f, -16f, 240f, 244f, null, x, y);
        type = EnemyType.ELITE;

        setHp(calcAscensionTankiness(HEALTH));
        addMove(CORE_SURGE, Intent.ATTACK_BUFF, calcAscensionDamage(11));
        addMove(CAPACITOR, Intent.UNKNOWN);
        addMove(THUNDER_STRIKE, Intent.ATTACK_DEFEND, calcAscensionDamage(7), 0, true);
        addMove(BALL_LIGHTNING, Intent.ATTACK_BUFF, calcAscensionDamage(7), 2, true);
        addMove(ZAP, Intent.UNKNOWN);

        this.biasedCognitionAmount = AbstractDungeon.ascensionLevel < 18 ? 4 : 5;
        this.defragmentAmount = AbstractDungeon.ascensionLevel < 18 ? 1 : 2;
        this.leapBlock = AbstractDungeon.ascensionLevel < 8 ? 9 : 12;
        this.forceFieldBlock = AbstractDungeon.ascensionLevel < 8 ? 12 : 16;

        loadAnimation("images/characters/defect/idle/skeleton.atlas",
                "images/characters/defect/idle/skeleton.json",
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "Idle", true);
        stateData.setMix("Hit", "Idle", 0.1f);
        e.setTimeScale(0.6f);
        flipHorizontal = true;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        switch (nextMove) {
            case 0: // Core Surge / Biased Cognition / Rainbow
                doFakePlay(new CoreSurge(), 3, 0);
                addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                doFakePlay(new BiasedCognition(), 18, 1);
                addToBot(new ApplyPowerAction(this, this, new FakeFocusPower(this, this.biasedCognitionAmount)));
                doFakePlay(new Rainbow(), 2);
                summonOrbMinion(OrbMinionType.Lightning, 1);
                summonOrbMinion(OrbMinionType.Frost, 2);
                summonOrbMinion(OrbMinionType.Dark, 3);
                break;
            case 1: // Capacitor / Electrodynamics / Defragment
                doFakePlay(new Capacitor(), 0);
                doFakePlay(new Electrodynamics(), 1);
                summonOrbMinion(OrbMinionType.Lightning);
                summonOrbMinion(OrbMinionType.Lightning);
                doFakePlay(new Defragment(), 18, 2);
                addToBot(new ApplyPowerAction(this, this, new FakeFocusPower(this, this.defragmentAmount)));
                break;
            case 2: // Thunder Strike / Leap / Force Field
                doFakePlay(new ThunderStrike(), 3, 0);
                useSlowAttackAnimation();
                for (int i = 0; i < this.lightningSummons; i++) {
                    addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.NONE));
                    if (!Settings.FAST_MODE) {
                        this.addToTop(new WaitAction(0.1F));
                    }
                    addToBot(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY)));
                    addToBot(new VFXAction(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.LIGHTNING)));
                    addToBot(new SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));
                }
                doFakePlay(new Leap(), 8, 1);
                addToBot(new GainBlockAction(this, this.leapBlock));
                doFakePlay(new ForceField(), 8, 2);
                addToBot(new GainBlockAction(this, this.forceFieldBlock));
                break;
            case 3: // Ball Lightning / Compile Driver / Defragment
                doFakePlay(new BallLightning(), 3, 0);
                Wiz.atb(new AbstractGameAction() {
                    public void update() {
                        useFastAttackAnimation();
                        isDone = true;
                    }
                });
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                summonOrbMinion(OrbMinionType.Lightning);
                doFakePlay(new CompileDriver(), 3, 1);
                Wiz.atb(new AbstractGameAction() {
                    public void update() {
                        useFastAttackAnimation();
                        isDone = true;
                    }
                });
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                doFakePlay(new Defragment(), 18, 2);
                addToBot(new ApplyPowerAction(this, this, new FakeFocusPower(this, this.defragmentAmount)));
                break;
            case 4: // Zap / Coolheaded / Darkness
                doFakePlay(new Zap(), 0);
                summonOrbMinion(OrbMinionType.Lightning);
                doFakePlay(new Coolheaded(), 1);
                summonOrbMinion(OrbMinionType.Frost);
                doFakePlay(new Darkness(), 2);
                summonOrbMinion(OrbMinionType.Dark);
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        if (this.firstMove) {
            setMoveShortcut(CORE_SURGE, MOVES[CORE_SURGE]);
            this.firstMove = false;
        } else if (this.lastMove(CORE_SURGE)) {
            setMoveShortcut(CAPACITOR, MOVES[CAPACITOR]);
        } else {
            List<Byte> summonMoves = Arrays.asList(CORE_SURGE, CAPACITOR, ZAP);
            int moves = this.moveHistory.size();
            boolean canSummon = moves > 1 && !summonMoves.contains(this.moveHistory.get(moves - 1)) && !summonMoves.contains(this.moveHistory.get(moves - 2));
            int minionCount = (int)AbstractDungeon.getMonsters().monsters.stream().filter(m -> m instanceof OrbMinion && !m.isDeadOrEscaped()).count();
            Anniv8Mod.logger.info("minionCount: " + minionCount);
            int chance = minionCount == 0 ? 100 : minionCount == 1 ? 65 : minionCount == 2 ? 30 : 0;
            if (canSummon && i < chance) {
                setMoveShortcut(ZAP, MOVES[ZAP]);
            }
            else {
                boolean canBallLightning = minionCount < this.orbs.length && (!this.lastMove(BALL_LIGHTNING) || !this.lastMoveBefore(BALL_LIGHTNING));
                boolean canThunderStrike = !this.lastMove(THUNDER_STRIKE);
                if (!canBallLightning || (canThunderStrike && AbstractDungeon.aiRng.randomBoolean())) {
                    this.moves.get(THUNDER_STRIKE).multiplier = this.lightningSummons;
                    setMoveShortcut(THUNDER_STRIKE, MOVES[THUNDER_STRIKE]);
                }
                else {
                    setMoveShortcut(BALL_LIGHTNING, MOVES[BALL_LIGHTNING]);
                }
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output - currentBlock > 0) {
            AnimationState.TrackEntry e = state.setAnimation(0, "Hit", false);
            state.addAnimation(0, "Idle", true, 0f);
            e.setTimeScale(0.6f);
        }

        super.damage(info);
    }

    @Override
    public void die() {
        super.die();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m instanceof OrbMinion && !m.isDead && !m.isDying) {
                AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
            }
        }
    }

    public static int getFocus() {
        AbstractMonster defect = AbstractDungeon.getMonsters().monsters.stream().filter(m -> m instanceof DefectEliteMonster && !m.isDeadOrEscaped()).findFirst().orElse(null);
        if (defect == null) {
            return 0;
        }
        AbstractPower focus = defect.getPower(FakeFocusPower.POWER_ID);
        return focus == null ? 0 : focus.amount;
    }

    private void summonOrbMinion(OrbMinionType type) {
        Integer slot = null;
        for (int i = 0; i < this.orbs.length; i++) {
            AbstractMonster m = this.orbs[i];
            if (m == null || m.isDeadOrEscaped()) {
                slot = i;
                break;
            }
        }
        if (slot == null) {
            throw new RuntimeException("Tried to summon when all slots were full");
        }
        summonOrbMinion(type, slot);
    }

    private void summonOrbMinion(OrbMinionType type, int slot) {
        AbstractMonster orb;
        switch (type) {
            case Lightning: orb = new LightningOrbMinion(x[slot], y[slot]); break;
            case Frost: orb = new FrostOrbMinion(x[slot], y[slot]); break;
            case Dark: orb = new DarkOrbMinion(x[slot], y[slot]); break;
            default: throw new RuntimeException("Unrecognized orb type: " + type);
        }
        this.orbs[slot] = orb;
        addToBot(new SpawnMonsterAction(orb, true));
        if (type == OrbMinionType.Lightning) {
            this.lightningSummons++;
            addToBot(new ApplyPowerAction(orb, orb, new OrbMinionDefectFocusDamageIncreasePower(this)));
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // We adjust the monster list so that the Defect acts after all orbs
                List<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
                for (int i = 0; i < monsters.size(); i++) {
                    AbstractMonster m = monsters.get(i);
                    if (m instanceof DefectEliteMonster && monsters.size() > i + 1) {
                        monsters.set(i, monsters.get(i + 1));
                        monsters.set(i + 1, m);
                    }
                }
                this.isDone = true;
            }
        });
    }

    private void doFakePlay(AbstractCard c, int index) {
        doFakePlay(c, Integer.MAX_VALUE, index);
    }

    private void doFakePlay(AbstractCard c, int ascLevelToUpgrade, int index) {
        if(AbstractDungeon.ascensionLevel >= ascLevelToUpgrade) c.upgrade();
        Wiz.vfx(new FakePlayCardEffect(this, c, index * -225f));
    }

    private enum OrbMinionType {
        Lightning,
        Frost,
        Dark
    }
}
