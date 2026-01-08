package spireQuests.quests.coda;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward.PotionReward;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.quests.coda.potions.NuclearJuicePotion;
import spireQuests.quests.coda.relics.RadiationDispenserRelic;
import spireQuests.quests.modargo.PackFanaticQuest;
import spireQuests.util.CompatUtil;

import java.util.*;

public class OrbSpinnerQuest extends AbstractQuest {

    private ArrayList<AbstractOrb> evokedOrbs = new ArrayList<>();

    public OrbSpinnerQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new PassiveTracker<Integer>(() -> evokedOrbs.size(), 4)
            .add(this);

        new TriggerEvent<AbstractOrb>(QuestTriggers.EVOKE_ORB, (orb) -> 
            {
                // IDK how you evoked an empty orb or null orb, but you did it chief.
                if (orb.ID == null || orb.ID.equals(EmptyOrbSlot.ORB_ID)) {
                    return;
                }

                for (AbstractOrb o : evokedOrbs) {
                    if (Objects.equals(o.ID, orb.ID)) {
                        return;
                    }
                }

                // If somehow the copied orb is null, we prefer ignoring buggy orbs to crashing.
                AbstractOrb orbCopy = orb.makeCopy();
                if (orbCopy != null) {
                    evokedOrbs.add(orbCopy);
                }
            })
        .add(this);
 
        new TriggerEvent<>(QuestTriggers.TURN_START, (v) -> evokedOrbs.clear()).add(this);
        new TriggerEvent<>(QuestTriggers.LEAVE_ROOM, (node) -> evokedOrbs.clear()).add(this);

        needHoverTip = true;
        addReward(new PotionReward(new NuclearJuicePotion()));
        addReward(new RelicReward(new RadiationDispenserRelic()));
    }

    @Override
    public PowerTip getHoverTooltip() {
        ArrayList<String> evokedOrbsStr = new ArrayList<>();
        for (AbstractOrb o : evokedOrbs) {
            evokedOrbsStr.add(o.name);
        }
        PowerTip ret = new PowerTip(questStrings.TRACKER_TEXT[1], String.join("; ", evokedOrbsStr));
        return ret;
    }

    @Override
    public boolean canSpawn() {
        if (CompatUtil.pmLoaded() && PackFanaticQuest.anniv5 != null && PackFanaticQuest.abstractCardPack != null && PackFanaticQuest.packSummary != null && AbstractDungeon.player.chosenClass.toString().equals("THE_PACKMASTER")) {
            List<Object> currentPoolPacks = ReflectionHacks.getPrivateStatic(PackFanaticQuest.anniv5, "currentPoolPacks");
            //noinspection unchecked,rawtypes
            return currentPoolPacks.stream()
                    .map(p -> ReflectionHacks.getPrivate(p, PackFanaticQuest.abstractCardPack, "summary"))
                    .map(s -> (HashSet<Enum>)ReflectionHacks.getPrivate(s, PackFanaticQuest.packSummary, "tags"))
                    .flatMap(Collection::stream)
                    .anyMatch(tag -> tag.name().equals("Orbs"));
        }
        return AbstractDungeon.player.masterMaxOrbs > 0;
    }
    
}
