package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.soytheproton.relics.MagicBoot;

import java.util.ArrayList;
import java.util.List;

public class LuckyNumberQuest extends AbstractQuest {
    private AbstractRelic boot = new Boot();
    public LuckyNumberQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.BOOT_TRIGGER, 7)
                .setResetTrigger(QuestTriggers.COMBAT_END)
                .add(this);
        addReward(new QuestReward.RelicReward(new MagicBoot()));

        questboundRelics = new ArrayList<>();
        questboundRelics.add(boot);
        returnQuestboundRelics = false;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new PowerTip(boot.name, boot.description));
        tipList.add(new PowerTip(Anniv8Mod.keywords.get("Questbound").PROPER_NAME, Anniv8Mod.keywords.get("Questbound").DESCRIPTION));
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(Boot.ID);
    }
}
