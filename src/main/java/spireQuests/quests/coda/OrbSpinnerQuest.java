package spireQuests.quests.coda;

import java.util.ArrayList;

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

public class OrbSpinnerQuest extends AbstractQuest {

    private ArrayList<AbstractOrb> evokedOrbs = new ArrayList();

    public OrbSpinnerQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new PassiveTracker<Integer>(() -> evokedOrbs.size(), 4)
            .add(this);

        new TriggerEvent<AbstractOrb>(QuestTriggers.EVOKE_ORB, (orb) -> 
            {
                // IDK how you evoked an empty orb or null orb, but you did it chief.
                if (orb.ID == null || orb.ID == EmptyOrbSlot.ORB_ID) {
                    return;
                }

                for (AbstractOrb o : evokedOrbs) {
                    if (o.ID == orb.ID) {
                        return;
                    }
                }
                
                evokedOrbs.add(orb.makeCopy());
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
        PowerTip ret = new PowerTip(localization.EXTRA_TEXT[1], String.join("; ", evokedOrbsStr));
        return ret;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player.masterMaxOrbs > 0;
    }
    
}
