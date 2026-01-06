package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.util.TexLoader;

public class PerishableStickerModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("PerishableSticker");
    private static final UIStrings strings = CardCrawlGame.languagePack.getUIString(MODIFIER_ID);

    public int REMAINING_TURNS;

    private static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/PerishableStickerIcon.png");

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        ExtraIcons.icon(icon).render(card);
    }

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PerishableStickerModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        REMAINING_TURNS = 5;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(strings.TEXT[0], String.format(strings.TEXT[1], REMAINING_TURNS)));
        return tips;
    }

    /*
     * Ticks remaining turns down by 1
     *
     * @returns true if the remaining turns is 0 or less than 0
     */
    public boolean tickRemainingTurns() {
        return --REMAINING_TURNS <= 0;
    }

}
