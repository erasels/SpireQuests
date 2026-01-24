package spireQuests.quests.coda;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.colorless.Mayhem;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import spireQuests.patches.QuestTriggers;
import spireQuests.questStats.StatRewardBox;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.util.CompatUtil;
import spireQuests.util.NodeUtil;

public class MonsterHunterQuest extends AbstractQuest{

    private ArrayList<AbstractCard> cardRewards = new ArrayList<>();


    public MonsterHunterQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
            .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger)
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);

            if (CardCrawlGame.isInARun()) {
                if (CompatUtil.pmLoaded()) {
                    cardRewards = getPMCardRewards();
                } else {
                    cardRewards = getCardRewards();
                }
            }

            addReward(new QuestReward.CardChoiceReward(cardRewards));
    }

    @Override
    public void onComplete() {
        super.onComplete();
        this.name = questStrings.EXTRA_TEXT[0];
    }

    private ArrayList<AbstractCard> getPMCardRewards() {
        switch (AbstractDungeon.id) {
            case "Exordium":
                return getPMAct1Cards();
            case "TheCity":
                return getPMAct2Cards();
            case "TheBeyond":
                return getPMAct3Cards();
            default:
                return getCardRewards();
            }
    }


    private ArrayList<AbstractCard> getPMAct1Cards() {
        ArrayList<AbstractCard> ret = new ArrayList<>();
        ret.add(CardLibrary.getCopy("anniv5:SkullClub"));
        ret.add(CardLibrary.getCopy("anniv5:CoreBlaster"));
        ret.add(CardLibrary.getCopy("anniv5:ShellPauldrons"));
        return ret;
    }
    private ArrayList<AbstractCard> getPMAct2Cards() {
        ArrayList<AbstractCard> ret = new ArrayList<>();
        ret.add(CardLibrary.getCopy("anniv5:StabManual"));
        ret.add(CardLibrary.getCopy("anniv5:SlaverWhip"));
        ret.add(CardLibrary.getCopy("anniv5:GremlinLance"));
        return ret;
    }
    private ArrayList<AbstractCard> getPMAct3Cards() {
        ArrayList<AbstractCard> ret = new ArrayList<>();
        ret.add(CardLibrary.getCopy("anniv5:EphemeralShroud"));
        ret.add(CardLibrary.getCopy("anniv5:StoneHelm"));
        ret.add(CardLibrary.getCopy("anniv5:SerpentineDagger"));
        return ret;
    }

    private ArrayList<AbstractCard> getCardRewards() {
        ArrayList<AbstractCard> ret = new ArrayList<>();
        ret.add(CardLibrary.getCopy(HandOfGreed.ID));
        ret.add(CardLibrary.getCopy(Metamorphosis.ID));
        ret.add(CardLibrary.getCopy(Mayhem.ID));
        return ret;
    }

    @Override
    public ArrayList<StatRewardBox> getStatRewardBoxes() {
        ArrayList<StatRewardBox> ret = new ArrayList<>();
        if (CompatUtil.pmLoaded()) {
            ret.add(new StatRewardBox(new QuestReward.CardChoiceReward(getPMAct1Cards())));
            ret.add(new StatRewardBox(new QuestReward.CardChoiceReward(getPMAct2Cards())));
            ret.add(new StatRewardBox(new QuestReward.CardChoiceReward(getPMAct3Cards())));
        } else {
            ret.add(new StatRewardBox(new QuestReward.CardChoiceReward(getCardRewards())));
        }
        return ret;
    }

    @Override
    public boolean canSpawn() {
        return NodeUtil.canPathToNodes(node -> node.room instanceof MonsterRoomElite, 3);
    }
    
}
