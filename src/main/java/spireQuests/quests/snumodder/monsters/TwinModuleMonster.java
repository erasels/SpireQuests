package spireQuests.quests.snumodder.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class TwinModuleMonster extends AbstractSQMonster {
    public static final String ID = makeID(TwinModuleMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final byte ATTACK = 0, REST = 1;

    private static final int HEALTH = 6;
    private final boolean firstAttack;
    private boolean firstTurn = true;

    public TwinModuleMonster(float x, float y, boolean first) {
        super(NAME, ID, HEALTH * actNum, 0f, -30.0f, 160f, 180f, null, x, y);
        setHp(calcAscensionTankiness(HEALTH * actNum));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(5 * actNum));
        addMove(REST, Intent.UNKNOWN);
        firstAttack = first;
        loadAnimation(makeImagePath("snumodder/zilliax/twin/twin.atlas"),
                makeImagePath("snumodder/zilliax/twin/twin.json"),
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
                addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                setMoveShortcut(REST);
                break;
            case 1:
                setMoveShortcut(ATTACK);
                break;
        }
    }

    @Override
    protected void getMove(int i) {
        if (firstTurn) {
            if (firstAttack) {
                setMoveShortcut(ATTACK);
            } else {
                setMoveShortcut(REST);
            }
            firstTurn = false;
        }
    }
}
