package spireQuests.quests.dayvig;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.dayvig.relics.Binder;

public class NeatQuest extends AbstractQuest {
    public NeatQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 1)
                .triggerCondition(this::hasPerfectDeck)
                .add(this);
        new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 1)
                .triggerCondition(this::hasPerfectDeck)
                .hide()
                .add(this);

        addReward(new QuestReward.RelicReward(new Binder()));
    }

    public boolean hasPerfectDeck(AbstractCard card) {
        int skills = 0;
        int powers = 0;
        int attacks = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type.equals(AbstractCard.CardType.SKILL)) {
                skills++;
            }
            if (c.type.equals(AbstractCard.CardType.ATTACK)) {
                attacks++;
            }
            if (c.type.equals(AbstractCard.CardType.POWER)) {
                powers++;
            }
        }
        return skills == 10 && attacks == 10 && powers == 5;
    }
}
