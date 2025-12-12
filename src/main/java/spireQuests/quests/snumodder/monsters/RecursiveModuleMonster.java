package spireQuests.quests.snumodder.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class RecursiveModuleMonster extends AbstractSQMonster {
    public static final String ID = makeID(RecursiveModuleMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ATTACK = 0, REPEAT = 1;
    private boolean firstTurn = true;

    private static final int HEALTH = 10;

    public RecursiveModuleMonster(float x, float y) {
        super(NAME, ID, HEALTH * actNum, 0f, -30.0f, 160f, 180f, null, x, y);
        setHp(calcAscensionTankiness(HEALTH * actNum));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(5 * actNum));
        addMove(REPEAT, Intent.DEBUFF);
        loadAnimation(makeImagePath("snumodder/zilliax/recursive/recursive.atlas"),
                makeImagePath("snumodder/zilliax/recursive/recursive.json"),
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void takeTurn() {
        switch (nextMove) {
            case 0:
                DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
                info.applyPowers(this, AbstractDungeon.player);
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.LIGHTNING));
                setMoveShortcut(REPEAT);
                break;
            case 1:
                addToBot(new MakeTempCardInDiscardAction(new Dazed(), 1));
                setMoveShortcut(ATTACK);
                break;
        }
    }

    @Override
    protected void getMove(int i) {
        if (firstTurn) {
            firstTurn = false;
            setMoveShortcut(ATTACK);
        }
    }
}
