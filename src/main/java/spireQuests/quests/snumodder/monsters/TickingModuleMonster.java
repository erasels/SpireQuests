package spireQuests.quests.snumodder.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.snumodder.powers.TickingPower;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class TickingModuleMonster extends AbstractSQMonster {
    public static final String ID = makeID(TickingModuleMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte TICK = 0, ATTACK = 1;
    private boolean firstTurn = true;

    private static final int HEALTH = 10;
    private final int strengthUp;

    public TickingModuleMonster(float x, float y) {
        super(NAME, ID, HEALTH * actNum, 0f, -30.0f, 160f, 180f, null, x, y);
        setHp(calcAscensionTankiness(HEALTH * actNum));
        addMove(TICK, Intent.BUFF);
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(3 * actNum));
        strengthUp = actNum * AbstractDungeon.ascensionLevel < 17 ? 2 : 3;
        loadAnimation(makeImagePath("snumodder/zilliax/ticking/ticking.atlas"),
                makeImagePath("snumodder/zilliax/ticking/ticking.json"),
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new TickingPower(this)));
    }

    @Override
    public void takeTurn() {
        switch (nextMove) {
            case 0:
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, strengthUp), strengthUp));
                setMoveShortcut(TICK);
                break;
            case 1:
                DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
                info.applyPowers(this, AbstractDungeon.player);
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SMASH));
                setMoveShortcut(TICK);
                break;
        }
    }

    @Override
    protected void getMove(int i) {
        if (firstTurn) {
            firstTurn = false;
            setMoveShortcut(TICK);
        }
    }
}
