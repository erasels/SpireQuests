package spireQuests.quests.gk;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.shop.ShopScreen;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.util.Wiz;

public class FavouriteCustomerQuest extends AbstractQuest {
    public FavouriteCustomerQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.MONEY_SPENT_AT_SHOP, 10)
                .add(this);

        addReward(new QuestReward.RelicReward(new SmilingMask()));
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.commonRelicPool.remove(SmilingMask.ID);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (AbstractDungeon.shopScreen != null) {
            ReflectionHacks.setPrivate(AbstractDungeon.shopScreen, ShopScreen.class, "actualPurgeCost", SmilingMask.COST);
        }
    }

    @Override
    public boolean canSpawn() {
        return !Wiz.p().hasRelic(SmilingMask.ID) && (Settings.isEndless || AbstractDungeon.actNum < 40);
    }
}
