package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.icons.PerishableIcon;
import spireQuests.util.TexLoader;

public class PerishableModifier extends AbstractCardModifier {

    public static final String ID = Anniv8Mod.makeID("Perishable");
    private static final Keyword KEYWORD = Anniv8Mod.keywords.get(ID);

    public int remaining_turns;

    public static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/PerishableStickerIcon.png");

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        ExtraIcons.icon(icon).render(card);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PerishableModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        remaining_turns = 5;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo("[" + PerishableIcon.ID + "Icon] " + KEYWORD.PROPER_NAME,
                KEYWORD.DESCRIPTION.replace("5", remaining_turns + "")));
        return tips;
    }

    /*
     * Ticks remaining turns down by 1
     *
     * @returns true if the remaining turns is 0 or less than 0
     */
    public boolean tickRemainingTurns() {
        return --remaining_turns <= 0;
    }

}
