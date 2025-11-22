package spireQuests.quests.jackrenoson;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

import java.util.ArrayList;

public class RainbowQuest extends AbstractQuest {
    ArrayList<AbstractCard.CardColor> colorsAdded = new ArrayList<>();
    int req = 3;

    public RainbowQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        rewardScreenOnly = true;
        needHoverTip = true;

        Tracker tracker = new TriggerTracker<>(QuestTriggers.ADD_CARD, req)
                .triggerCondition((card) -> !colorsAdded.contains(card.color))
                .add(this);
        tracker.text = localization.EXTRA_TEXT[0] + req + localization.EXTRA_TEXT[1];

        new TriggerEvent<>(QuestTriggers.ADD_CARD, c -> {
            if(!colorsAdded.contains(c.color)){
                colorsAdded.add(c.color);
            }
        }).add(this);

        addReward(new QuestReward.RelicReward(new QuestionCard()));
        addReward(new QuestReward.RelicReward(new PrismaticShard()));
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.95F, Settings.HEIGHT / 2.0F, new PrismaticShard());
    }

    @Override
    public void onComplete() {
        AbstractDungeon.player.loseRelic(new PrismaticShard().relicId);
    }

    @Override
    protected void setText() {
        name = localization.TEXT[0];
        description = localization.TEXT[1] + req + localization.TEXT[2];
        author = localization.TEXT[3];
    }

    @Override
    public PowerTip getHoverTooltip() {
        ArrayList<String> colorNames = new ArrayList<>();
        for (AbstractCard.CardColor c : colorsAdded) {
            colorNames.add(c.name().toLowerCase());
        }
        return new PowerTip(localization.EXTRA_TEXT[2], String.join(" NL ", colorNames));
    }
}
