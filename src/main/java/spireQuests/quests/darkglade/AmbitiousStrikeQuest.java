package spireQuests.quests.darkglade;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AmbitiousStrikeQuest extends AbstractQuest {
    public AmbitiousStrikeQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        new TriggeredUpdateTracker<>(QuestTriggers.COMBAT_END, 0, 1, () -> {
            ArrayList<AbstractCard> cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisCombat;
            if (cardsPlayed == null || cardsPlayed.isEmpty()) return 0;

            AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
            if (lastCard.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && AbstractDungeon.getCurrRoom().eliteTrigger) {
                return 1;
            }
            return 0;
        }).add(this);

        useDefaultReward = false;
        titleScale = 1.0f;
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade() && c.type == AbstractCard.CardType.ATTACK) {
                upgradableCards.add(c);
            }
        }

        Collections.shuffle(upgradableCards, new Random(AbstractQuest.rng.randomLong()));
        if (!upgradableCards.isEmpty()) {
            upgradableCards.get(0).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(upgradableCards.get(0).makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        }
    }

    @Override
    public boolean canSpawn() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && (AbstractDungeon.actNum == 1 || AbstractDungeon.actNum == 2)) {
                return true;
            }
        }
        return false;
    }
}
