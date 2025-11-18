package spireQuests.quests.pandemonium;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.PeasantsTunic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PristineCardsQuest extends AbstractQuest {

    protected static final int PRISTINE_CARDS_RATE = 50;


    public PristineCardsQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggeredUpdateTracker<>(QuestTriggers.DECK_CHANGE, 0, 3, () -> {
            ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
            return (int)deck.stream().filter(c -> CardModifierManager.hasModifier(c,PristineModifier.ID)).count();
        }).add(this);


        useDefaultReward = false;
        rewardsText = localization.EXTRA_TEXT[1];
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
        List<AbstractCard> toUpgrade = deck.stream().filter(c -> c.canUpgrade() && CardModifierManager.hasModifier(c, PristineModifier.ID)).collect(Collectors.toList());
        AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        float spacing = Settings.WIDTH / (float)(toUpgrade.size() + 1);
        int i = 1;
        for (AbstractCard c : toUpgrade) {
            c.upgrade();
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), spacing * i, Settings.HEIGHT/2f));
            i++;
        }
    }


}
