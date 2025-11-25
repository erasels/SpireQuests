package spireQuests.quests.indi_keurodz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.relics.BurningBlood;

import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;

public class BalatroQuest extends AbstractQuest {
    private static TextureAtlas BossBlinds;

    public static int BLIND_FIGHTS_COMPLETED = 0;

    public BalatroQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        BossBlinds = new TextureAtlas(
                Gdx.files.internal(Anniv8Mod.makeContributionPath("indi_keurodz", "BossBlinds.atlas")));

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 10, () -> {
            return BLIND_FIGHTS_COMPLETED;
        }).add(this);

        // if (QuestManager.quests().stream().anyMatch(x -> x instanceof BalatroQuest))

        addReward(new QuestReward.RelicReward(new BurningBlood()));
        needHoverTip = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        (new GoldStakeRelic()).instantObtain();
    }

}
