package spireQuests.quests.luaviper.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static spireQuests.patches.QuestTriggers.ENEMY_DIES_DELAYED_CHECK;

public class EnemyDiesDelayedCheckAction extends AbstractGameAction {
    AbstractMonster monster;

    public EnemyDiesDelayedCheckAction(AbstractMonster monster) {
        this.actionType = ActionType.DAMAGE;    //do not clear event after combat
        this.monster = monster;
    }

    public void update() {
        ENEMY_DIES_DELAYED_CHECK.trigger(monster);
        isDone = true;
    }
}
