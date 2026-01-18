package spireQuests.quests.quorry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class FatalStrikesQuest extends AbstractQuest {

    public static final String id = makeID(FatalStrikesQuest.class.getSimpleName());
    protected static final int FATAL_STRIKE_COUNT = 5;


    public FatalStrikesQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.FATAL_CARD, FATAL_STRIKE_COUNT)
                .triggerCondition((card) -> card.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && AbstractDungeon.actNum == 2)
                .setFailureTrigger(QuestTriggers.ACT_CHANGE, (act) -> act > 2)
                .add(this);


        useDefaultReward = false;
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
        List<AbstractCard> toReplace = deck.stream().filter(c -> c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)).collect(Collectors.toList());
        AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        float spacing = Settings.WIDTH / (float) (toReplace.size() + 1);
        int i = 1;
        for (AbstractCard c : toReplace) {
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractCard cNew = CardLibrary.getCopy(LaborStrike.ID, c.timesUpgraded, 0);
            AbstractDungeon.player.masterDeck.addToBottom(cNew);
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(cNew.makeStatEquivalentCopy(), spacing * i, Settings.HEIGHT / 2f));
            i++;
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new LaborStrike()));
    }
}
