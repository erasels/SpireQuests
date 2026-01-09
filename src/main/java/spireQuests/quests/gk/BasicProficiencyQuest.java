package spireQuests.quests.gk;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Dualcast;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.Survivor;
import com.megacrit.cardcrawl.cards.purple.Vigilance;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;

import basemod.BaseMod;
import spireQuests.patches.QuestTriggers;
import spireQuests.questStats.StatRewardBox;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.gk.cards.*;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spireQuests.util.CompatUtil.CARDISTRY_ID;
import static spireQuests.util.CompatUtil.SNAPSHOT_ID;

public class BasicProficiencyQuest extends AbstractQuest {
    public static final Color TITLE_PURPLE = new Color(183/255f, 95/255f, 245/255f, 1);
    private static final Map<String, List<String>> CHAR_MAP = new HashMap<>();

    static {
        CHAR_MAP.put("IRONCLAD", Arrays.asList(Bash.ID, Smash.ID));
        CHAR_MAP.put("THE_SILENT", Arrays.asList(Survivor.ID, Thriver.ID));
        CHAR_MAP.put("DEFECT", Arrays.asList(Dualcast.ID, TripleCast.ID));
        CHAR_MAP.put("WATCHER", Arrays.asList(Vigilance.ID, Balance.ID));
        CHAR_MAP.put("THE_PACKMASTER", Arrays.asList(CARDISTRY_ID, Cardmancy.ID));
        CHAR_MAP.put("HERMIT", Arrays.asList(SNAPSHOT_ID, Trapshot.ID));
    }

    private String cardToPlayId = Madness.ID;
    private String rewardCardId = Madness.ID;

    public BasicProficiencyQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        needHoverTip = true;

        if (Wiz.p() != null) {
            List<String> data = CHAR_MAP.get(Wiz.p().chosenClass.name());
            cardToPlayId = data.get(0);
            rewardCardId = data.get(1);
        }

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 3)
                .triggerCondition((card) -> card.cardID.equals(cardToPlayId))
                .setResetTrigger(QuestTriggers.COMBAT_END)
                .add(this);

        addReward(new QuestReward.CardReward(CardLibrary.getCopy(rewardCardId)));
        titleScale = 1.0f;
    }

    @Override
    public boolean canSpawn() {
        return CHAR_MAP.containsKey(Wiz.p().chosenClass.name()) && Wiz.deck().findCardById(cardToPlayId) != null;
    }

    @Override
    public String getDescription() {
        if (!CardCrawlGame.isInARun()) {
            return questStrings.EXTRA_TEXT[0];
        }
        AbstractCard card = CardLibrary.getCard(cardToPlayId);
        return String.format(description, FontHelper.colorString(card.name, "y"));
    }

    @Override
    public ArrayList<StatRewardBox> getStatRewardBoxes() {
        ArrayList<StatRewardBox> ret = new ArrayList<>();

        ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(Smash.ID))));
        ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(Thriver.ID))));
        ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(TripleCast.ID))));
        ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(Balance.ID))));

        List<AbstractPlayer> moddedChars = BaseMod.getModdedCharacters();

        if (moddedChars.stream().anyMatch(p -> p.chosenClass.name().equals("HERMIT"))) {
            ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(Trapshot.ID))));
        }
        if (moddedChars.stream().anyMatch(p -> p.chosenClass.name().equals("THE_PACKMASTER"))) {
            ret.add(new StatRewardBox(new QuestReward.CardReward(CardLibrary.getCopy(Cardmancy.ID))));
        }

        return ret;
    }
}
