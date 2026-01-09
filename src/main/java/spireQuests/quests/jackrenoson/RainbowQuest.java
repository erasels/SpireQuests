package spireQuests.quests.jackrenoson;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.ArrayList;
import java.util.Objects;

public class RainbowQuest extends AbstractQuest {
    ArrayList<AbstractCard.CardColor> colorsAdded = new ArrayList<>();
    int req;

    public RainbowQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        needHoverTip = true;
        req = determineReq();
        isAutoComplete = true;

        Tracker tracker = new TriggerTracker<>(QuestTriggers.ADD_CARD, req)
                .triggerCondition((card) -> !colorsAdded.contains(card.color))
                .add(this);
        tracker.text = questStrings.TRACKER_TEXT[0] + req + questStrings.TRACKER_TEXT[1];

        new TriggerEvent<>(QuestTriggers.ADD_CARD, c -> {
            if(!colorsAdded.contains(c.color)){
                colorsAdded.add(c.color);
            }
        }).add(this);

        questboundRelics = new ArrayList<>();
        questboundRelics.add(new PrismaticShard());
        returnQuestboundRelics = false;

        addReward(new QuestReward.RelicReward(new QuestionCard()));
        titleScale = 1.1f;
    }

    @Override
    public boolean canSpawn(){
        if(AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) AbstractDungeon.getCurrRoom();
            for(AbstractRelic r : shop.relics) {
                if(Objects.equals(r.relicId, PrismaticShard.ID) || Objects.equals(r.relicId, QuestionCard.ID)) return false;
            }
        }
        for(AbstractQuest q : QuestManager.getAllQuests()){
            if (q instanceof MulticlassQuest)
                return false;
        }
        return AbstractCard.CardColor.values().length<30 && !AbstractDungeon.player.hasRelic(PrismaticShard.ID) && !AbstractDungeon.player.hasRelic(QuestionCard.ID) &&!AbstractDungeon.player.hasRelic(MulticlassEmblem.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.shopRelicPool.remove(PrismaticShard.ID);
        AbstractDungeon.uncommonRelicPool.remove(QuestionCard.ID);
    }

    @Override
    protected void setText() {
        name = questStrings.TITLE;
        description = questStrings.EXTRA_TEXT[0] + determineReq() + questStrings.EXTRA_TEXT[1];
        author = questStrings.AUTHOR;
    }

    @Override
    public PowerTip getHoverTooltip() {
        ArrayList<String> colorNames = new ArrayList<>();
        for (AbstractCard.CardColor c : colorsAdded) {
            colorNames.add(c.name().toLowerCase());
        }
        return new PowerTip(questStrings.EXTRA_TEXT[2], String.join(" NL ", colorNames));
    }

    private int determineReq(){
        int totalColors = AbstractCard.CardColor.values().length-1; //-1 to not count Curses
        int r = 4;
        if (totalColors>=9) r = 5;
        if (totalColors>=15) r = 6;
        return r;
    }
}
