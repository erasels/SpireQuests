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
import spireQuests.quests.indi_keurodz.icons.RentalIcon;
import spireQuests.util.TexLoader;

public class RentalModifier extends AbstractCardModifier {

    public static final String ID = Anniv8Mod.makeID("Rental");

    public static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/RentalStickerIcon.png");

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
        return new RentalModifier();
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        final Keyword KEYWORD = Anniv8Mod.keywords.get(ID);
        tips.add(new TooltipInfo("[" + RentalIcon.ID + "Icon] " + KEYWORD.PROPER_NAME, KEYWORD.DESCRIPTION));
        return tips;
    }
}
