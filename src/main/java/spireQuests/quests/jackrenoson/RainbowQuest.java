package spireQuests.quests.jackrenoson;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.ArrayList;

public class RainbowQuest extends AbstractQuest {
    ArrayList<AbstractCard.CardColor> colorsAdded = new ArrayList<>();
    int req;

    public RainbowQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        rewardScreenOnly = true;
        needHoverTip = true;
        req = determineReq();

        Tracker tracker = new TriggerTracker<>(QuestTriggers.ADD_CARD, req)
                .triggerCondition((card) -> !colorsAdded.contains(card.color))
                .add(this);
        tracker.text = questStrings.TRACKER_TEXT[0] + req + questStrings.TRACKER_TEXT[1];

        new TriggerEvent<>(QuestTriggers.ADD_CARD, c -> {
            if(!colorsAdded.contains(c.color)){
                colorsAdded.add(c.color);
            }
        }).add(this);

        addReward(new QuestReward.RelicReward(new QuestionCard()));
        addReward(new QuestReward.RelicReward(new PrismaticShard()));
        titleScale = 1.1f;
    }

    @Override
    public boolean canSpawn(){
        for(AbstractQuest q : QuestManager.getAllQuests()){
            if (q instanceof MulticlassQuest)
                return false;
        }
        return AbstractCard.CardColor.values().length<30 && !AbstractDungeon.player.hasRelic(PrismaticShard.ID) && !AbstractDungeon.player.hasRelic(QuestionCard.ID) &&!AbstractDungeon.player.hasRelic(MulticlassEmblem.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.95F, Settings.HEIGHT / 2.0F, new PrismaticShard());
        AbstractDungeon.shopRelicPool.remove(PrismaticShard.ID);
        AbstractDungeon.uncommonRelicPool.remove(QuestionCard.ID);
    }

    @Override
    public void onComplete() {
        AbstractDungeon.player.loseRelic(new PrismaticShard().relicId);
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
