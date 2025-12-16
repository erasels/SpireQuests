package spireQuests.quests.ramchops;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.effects.EatMarshmallowEffect;
import spireQuests.quests.ramchops.monsters.EvilSentry;
import spireQuests.quests.ramchops.patch.MarshmallowOption;
import spireQuests.quests.ramchops.relics.SmoresRelic;

import java.util.ArrayList;

import static spireQuests.Anniv8Mod.makeID;

public class ThatsSmoresQuest extends AbstractQuest {

    private static final String ID = makeID(ThatsSmoresQuest.class.getSimpleName());

    public ThatsSmoresQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);
        new TriggerTracker<>(EatMarshmallowEffect.EAT_MARSHMALLOW, 2).add(this);
        addReward(new QuestReward.RelicReward(new SmoresRelic()));

        this.isAutoComplete = true;

    }

    @SpirePatch2(
            clz = CampfireUI.class,
            method = "initializeButtons"

    )
    public static class SmoreOptionPatch{

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void MarshmallowOptionPatch(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons){
            // if this quest exists
            ThatsSmoresQuest q = (ThatsSmoresQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                ___buttons.add(new MarshmallowOption());
            }

        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "hasRubyKey");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
