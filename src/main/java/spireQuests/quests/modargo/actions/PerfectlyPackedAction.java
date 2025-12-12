package spireQuests.quests.modargo.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import spireQuests.quests.modargo.PackFanaticQuest;

import java.util.*;
import java.util.stream.Collectors;

public class PerfectlyPackedAction extends AbstractGameAction {
    private static final int CARD_OPTIONS = 3;
    private boolean retrieveCard = false;

    public PerfectlyPackedAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        ArrayList<AbstractCard> generatedCards = this.generateCardChoices();
        if (!AbstractDungeon.player.drawPile.isEmpty()) {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                AbstractDungeon.cardRewardScreen.customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], false);
            } else {
                if (!this.retrieveCard) {
                    if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                        AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard;
                        if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                            AbstractDungeon.player.drawPile.moveToHand(card, AbstractDungeon.player.drawPile);
                        } else {
                            AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                            AbstractDungeon.player.createHandIsFullDialog();
                        }

                        AbstractDungeon.cardRewardScreen.discoveryCard = null;
                    }

                    this.retrieveCard = true;
                }
            }
        }
        this.tickDuration();
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        List<AbstractCard> cards = AbstractDungeon.player.drawPile.group.stream()
                .filter(c -> PackFanaticQuest.cardParentMap.containsKey(c.cardID))
                .collect(Collectors.toList());
        List<AbstractCard> choices = new ArrayList<>();
        Set<String> seenPacks = new HashSet<>();
        Collections.shuffle(cards, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));
        int i = 0;
        for (AbstractCard c : cards) {
            String packID = PackFanaticQuest.cardParentMap.get(c.cardID);
            if (!seenPacks.contains(packID)) {
                choices.add(c);
                seenPacks.add(packID);
                i++;
                if (i >= 3) {
                    break;
                }
            }
        }

        return new ArrayList<>(choices);
    }
}
