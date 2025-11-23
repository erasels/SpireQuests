package spireQuests.quests.gk.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.cards.red.TwinStrike;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.FossilizedHelix;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.gk.powers.FakeDemonFormPower;
import spireQuests.quests.gk.vfx.FakePlayCardEffect;
import spireQuests.quests.gk.vfx.MonsterIronWaveEffect;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class ICEliteMonster extends AbstractSQMonster {
    public static final String ID = makeID(ICEliteMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final Byte DEMON_FORM = 0, IRON_WAVE = 1, BASH = 2, TWIN_STRIKE = 3;

    private AbstractRelic relic;
    private int demonFormAmt = 2;

    public ICEliteMonster() {
        this(0, 0);
    }

    public ICEliteMonster(float x, float y) {
        super(NAME, ID, 80, -4f, -16f, 220f, 290f, null, x, y);
        type = EnemyType.ELITE;

        setHp(calcAscensionTankiness(80), calcAscensionTankiness(87));
        addMove(DEMON_FORM, Intent.BUFF);
        addMove(IRON_WAVE, Intent.ATTACK_DEFEND, calcAscensionDamage(5));
        addMove(BASH, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(TWIN_STRIKE, Intent.ATTACK, calcAscensionDamage(5), 2, true);

        demonFormAmt = calcAscensionSpecial(demonFormAmt);

        loadAnimation("images/characters/ironclad/idle/skeleton.atlas",
                "images/characters/ironclad/idle/skeleton.json",
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "Idle", true);
        stateData.setMix("Hit", "Idle", 0.1f);
        e.setTimeScale(0.6f);
        flipHorizontal = true;

        relic = new FossilizedHelix();
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        switch (nextMove) {
            case 0: // Demon form
                doFakePlay(new DemonForm(), 18);
                Wiz.atb(new AbstractGameAction() {
                    public void update() {
                        useFastShakeAnimation(0.25f);
                        isDone = true;
                    }
                });
                addToBot(new ApplyPowerAction(this, this, new FakeDemonFormPower(this, demonFormAmt)));
                break;
            case 1: //Ironwave
                doFakePlay(new IronWave(), 3);
                useHopAnimation();
                Wiz.vfx(new MonsterIronWaveEffect(this.hb.cX, this.hb.cY, Wiz.p().hb.cX), 0.5f);
                addToBot(new GainBlockAction(this, calcAscensionDamage(5)));
                addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.NONE));
                break;
            case 2: // Bash
                doFakePlay(new Bash(), Integer.MAX_VALUE); // Don't visually upgrade because vuln amount would be weird
                useSlowAttackAnimation();
                addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot(new ApplyPowerAction(Wiz.p(), this, new VulnerablePower(Wiz.p(), 2, true)));
                break;
            case 3: // Twin Strike
                doFakePlay(new TwinStrike(), 3);
                Wiz.atb(new AbstractGameAction() {
                    public void update() {
                        useFastAttackAnimation();
                        isDone = true;
                    }
                });
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                Wiz.atb(new AbstractGameAction() {
                    public void update() {
                        useFastAttackAnimation();
                        isDone = true;
                    }
                });
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL));
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        // Start with Demon form and then cycle through the moves in order
        if(firstMove) {
            setMoveShortcut(DEMON_FORM, MOVES[DEMON_FORM]);
            firstMove = false;
        } else if(lastMove(DEMON_FORM) || lastMove(TWIN_STRIKE)) {
            setMoveShortcut(IRON_WAVE, MOVES[IRON_WAVE]);
        } else if(lastMove(IRON_WAVE)) {
            setMoveShortcut(BASH, MOVES[BASH]);
        } else if(lastMove(BASH)) {
            setMoveShortcut(TWIN_STRIKE, MOVES[TWIN_STRIKE]);
        }
    }

    @Override
    public void init() {
        super.init();
        relic.currentX = relic.targetX = hb.x + hb.width + (100f* Settings.xScale);
        relic.currentY = relic.targetY = hb.y;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new BufferPower(this, 1), 1));
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        Color transparent = Color.WHITE.cpy();
        transparent.a = 0;
        relic.renderWithoutAmount(sb, transparent);
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

    private void doFakePlay(AbstractCard c, int ascLevelToUpgrade) {
        if(AbstractDungeon.ascensionLevel >= ascLevelToUpgrade) c.upgrade();
        Wiz.vfx(new FakePlayCardEffect(this, c));
    }

    @SpirePatch2(clz = MonsterHelper.class, method = "getEncounterName")
    public static class FixRunHistory {
        @SpirePrefixPatch
        public static SpireReturn<String> patch(String key) {
            if(ID.equals(key)) {
                return SpireReturn.Return(NAME);
            }

            return SpireReturn.Continue();
        }
    }
}
