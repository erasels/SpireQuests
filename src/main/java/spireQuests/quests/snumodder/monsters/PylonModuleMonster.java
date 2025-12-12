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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class PylonModuleMonster extends AbstractSQMonster {
    public static final String ID = makeID(PylonModuleMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ATTACK = 0;

    private static final int HEALTH = 10;

    public PylonModuleMonster(float x, float y) {
        super(NAME, ID, HEALTH * actNum, 0f, -30.0f, 160f, 180f, null, x, y);
        setHp(calcAscensionTankiness(HEALTH * actNum));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(5 * actNum));
        loadAnimation(makeImagePath("snumodder/zilliax/pylon/pylon.atlas"),
                makeImagePath("snumodder/zilliax/pylon/pylon.json"),
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            addToBot(new ApplyPowerAction(m, m, new StrengthPower(m, 1), 1));
        }
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    protected void getMove(int i) {
        setMoveShortcut(ATTACK);
    }
}
