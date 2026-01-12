package spireQuests.quests.coda;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BackToBasicsQuest extends AbstractQuest {

    public BackToBasicsQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggeredUpdateTracker<Integer, Void>(QuestTriggers.VICTORY, 0, 1, () -> {
            if (AbstractDungeon.currMapNode == null) {
                return 0;
            }
            ArrayList<AbstractCard> cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisCombat;
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            if (!(room.eliteTrigger || room instanceof MonsterRoomBoss)) {
                return 0;
            }
            if (cardsPlayed == null || cardsPlayed.isEmpty()) {
                return 1;
            }
            for (AbstractCard c : cardsPlayed) {
                if (c.rarity == CardRarity.UNCOMMON || c.rarity == CardRarity.RARE) {
                    return 0;
                }
            }
            return 1;
        }) {
            @Override
            public String progressString() {
                return "";
            }

            @Override
            public boolean isDisabled() {
                AbstractRoom room = AbstractDungeon.getCurrRoom();
                if (!(room.eliteTrigger || room instanceof MonsterRoomBoss) || room.isBattleOver) {
                    return true;
                }
                ArrayList<AbstractCard> cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisCombat;
                for (AbstractCard c : cardsPlayed) {
                    if (c.rarity == CardRarity.UNCOMMON || c.rarity == CardRarity.RARE) {
                        return true;
                    }
                }
                return false;
            }
        }
                .add(this);

        useDefaultReward = false;
    }

    @Override
    public boolean canSpawn() {
        if (AbstractDungeon.actNum > 1) {
            return false;
        }
        
        int count = (int) AbstractDungeon.player.masterDeck.group.stream()
                    .filter(c -> c.rarity == CardRarity.BASIC || c.rarity == CardRarity.COMMON)
                    .count();

        return count >= 5;
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade() && (c.rarity == CardRarity.BASIC || c.rarity == CardRarity.COMMON)) {
                upgradableCards.add(c);
            }
        }
        Collections.shuffle(upgradableCards, new Random(AbstractQuest.rng.randomLong()));

        if (upgradableCards.size() == 1) {
            upgradeAndDisplay(upgradableCards.get(0), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F);
        } else if (upgradableCards.size() == 2) {
            upgradeAndDisplay(upgradableCards.get(0), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 20.0F, Settings.HEIGHT / 2.0F);
            upgradeAndDisplay(upgradableCards.get(1), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F, Settings.HEIGHT / 2.0F);
        } else {
            for (int i = 0; i < 3; i++) {
                upgradeAndDisplay(upgradableCards.get(i), Settings.WIDTH / 3.0F + (Settings.WIDTH / 6.0F * i), Settings.HEIGHT / 2.0F);
            }
        }
    }

    private void upgradeAndDisplay(AbstractCard card, float xPos, float yPos) {
        card.upgrade();
        AbstractDungeon.player.bottledCardUpgradeCheck(card);
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), xPos, yPos));
    }
}