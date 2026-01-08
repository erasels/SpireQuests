package spireQuests.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.util.QuestStringsUtils;
import spireQuests.util.TexLoader;

import java.util.Collections;
import java.util.List;

public class QuestboundMod extends AbstractCardModifier {
    public static String ID = Anniv8Mod.makeID("Questbound");
    public static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final Texture tex = TexLoader.getTexture(Anniv8Mod.modID + "Resources/images/ui/questboundIcon.png");

    public transient AbstractQuest boundQuest;
    public String boundQuestID;
    public int boundQuestIndex; // Only used for saving and loading

    public QuestboundMod(AbstractQuest quest) {
        boundQuest = quest;
        boundQuestID = quest.id;
    }

    public QuestboundMod(String questID, int questIndex) {
        boundQuestID = questID;
        boundQuestIndex = questIndex;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    /*@Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if(Anniv8Mod.questboundConfig) return uiStrings.TEXT[0] + rawDescription;
        return rawDescription;
    }*/

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        String questName = FontHelper.colorString(QuestStringsUtils.getQuestString(boundQuestID).TITLE, "y");
        return Collections.singletonList(new TooltipInfo(Anniv8Mod.keywords.get("Questbound").PROPER_NAME, String.format(uiStrings.TEXT[1], questName)));
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new QuestboundMod(boundQuestID, boundQuestIndex);
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        ExtraIcons.icon(tex).render(card);
    }

}
