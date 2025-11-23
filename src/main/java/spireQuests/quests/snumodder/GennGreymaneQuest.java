package spireQuests.quests.snumodder;


import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

public class GennGreymaneQuest extends AbstractQuest {
    public GennGreymaneQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);
        new TriggeredUpdateTracker<Integer, Void>(QuestTriggers.DECK_CHANGE, 0, 1, () -> {
            if (getOddCards() > 0 || getEvenCards() < 10) return 0;
            else return 1;
        }){
            @Override
            public String progressString() {
                return String.format(
                        " (%d/10) %s (%d/0)",
                        getEvenCards(), localization.EXTRA_TEXT[1], getOddCards()
                );
            }
        }.add(this);

        useDefaultReward = false;
        rewardsText = localization.EXTRA_TEXT[2];
    }

    private int getEvenCards() {
        int cards = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.cost % 2 != 1) {
                cards += 1;
            }
        }
        return cards;
    }

    private int getOddCards() {
        int cards = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.cost % 2 == 1) {
                cards += 1;
            }
        }
        return cards;
    }

    @Override
    public void onComplete() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!(c.rarity == AbstractCard.CardRarity.BASIC) || !c.canUpgrade()) continue;
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(c);
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1f, 0.9f) * (float) Settings.WIDTH, MathUtils.random(0.2f, 0.8f) * (float)Settings.HEIGHT));
        }
    }
}
