package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.quests.AbstractQuest;


@SpirePatch(
        clz = AbstractRelic.class,
        method = SpirePatch.CLASS
)
public class QuestboundRelicFields {
    public static SpireField<AbstractQuest> isQuestbound = new SpireField<>(()->null);
}
