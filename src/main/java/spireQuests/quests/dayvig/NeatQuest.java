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

        // This would be better set up with three separate tracks, one for each card type, so you could see your progress
        // without needing to count everything in your deck yourself. But in the interest of getting this working for release,
        // that will be left for later.
        new TriggeredUpdateTracker<>(QuestTriggers.DECK_CHANGE, 0, 1, () -> this.hasPerfectDeck() ? 1 : 0)
                .add(this);

        addReward(new QuestReward.RelicReward(new Binder()));
    }

    public boolean hasPerfectDeck() {
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
