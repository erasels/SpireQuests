package spireQuests.quests.modargo;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.cards.PerfectlyPacked;

import java.util.*;
import java.util.stream.Collectors;

import static spireQuests.util.CompatUtil.pmLoaded;

public class PackFanaticQuest extends AbstractQuest {
    public static Class<?> anniv5;
    public static Class<?> abstractCardPack;
    public static HashMap<String, String> cardParentMap;

    public PackFanaticQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);
        if (pmLoaded()) {
            try {
                anniv5 = Class.forName("thePackmaster.SpireAnniversary5Mod");
                abstractCardPack = Class.forName("thePackmaster.packs.AbstractCardPack");
                cardParentMap = ReflectionHacks.getPrivateStatic(anniv5, "cardParentMap");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Error retrieving classes from Packmaster", e);
            }
        }

        new TriggeredUpdateTracker<>(QuestTriggers.DECK_CHANGE, 0, 7, () -> {
            ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
            List<String> deckPackIDs = deck.stream()
                    .filter(c -> cardParentMap.containsKey(c.cardID))
                    .map(c -> cardParentMap.get(c.cardID))
                    .distinct()
                    .collect(Collectors.toList());
            Set<String> poolPackIDs = getCurrentPoolPackIDs();
            return deckPackIDs.stream().filter(poolPackIDs::contains).count();
        }).add(this);

        addReward(new QuestReward.CardReward(new PerfectlyPacked()));
        addReward(new QuestReward.CardReward(new PerfectlyPacked()));
    }

    @Override
    public boolean canSpawn() {
        return pmLoaded()
                && anniv5 != null
                && abstractCardPack != null
                && cardParentMap != null
                && AbstractDungeon.player.chosenClass.toString().equals("THE_PACKMASTER")
                && getCurrentPoolPackIDs().size() == 7;
    }

    private Set<String> getCurrentPoolPackIDs() {
        List<Object> currentPoolPacks = ReflectionHacks.getPrivateStatic(anniv5, "currentPoolPacks");
        if (currentPoolPacks == null) {
            return new HashSet<>();
        }
        return currentPoolPacks.stream().map(p -> (String) ReflectionHacks.getPrivate(p, abstractCardPack, "packID")).collect(Collectors.toSet());
    }
}
