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
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.snumodder.powers.HaywirePower;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class HaywireModuleMonster extends AbstractSQMonster {
    public static final String ID = makeID(HaywireModuleMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ATTACK = 0;

    private static final int HEALTH = 14;
    private int hpLoss;

    public HaywireModuleMonster(float x, float y) {
        super(NAME, ID, HEALTH * actNum, 0f, -30.0f, 160f, 180f, null, x, y);
        setHp(calcAscensionTankiness(HEALTH * actNum));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(7 * actNum));
        hpLoss = AbstractDungeon.ascensionLevel < 17 ? 8 : 7;
        if (actNum == 1) hpLoss -= 3;
        loadAnimation(makeImagePath("snumodder/zilliax/haywire/haywire.atlas"),
                makeImagePath("snumodder/zilliax/haywire/haywire.json"),
                1f);
        AnimationState.TrackEntry e = state.setAnimation(0, "animtion0", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new HaywirePower(this, hpLoss)));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        addToBot(new DamageAction(Wiz.p(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    protected void getMove(int i) {
        setMoveShortcut(ATTACK);
    }
}
