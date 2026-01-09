package spireQuests.quests.darkglade;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;

import java.util.List;

public class DoomsdayCalendarQuest extends AbstractQuest {
    public static final Trigger<Void> IMPENDING_DAY_KILL = new Trigger<>();

    public DoomsdayCalendarQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);
        new TriggerTracker<>(IMPENDING_DAY_KILL, 12).add(this);

        addReward(new QuestReward.CardReward(new StarOfExtinction()));
        titleScale = 0.9f;
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new ImpendingDay(), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new ImpendingDay()));
    }
}
