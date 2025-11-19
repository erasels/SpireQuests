package spireQuests.abstracts;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.util.TexLoader;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.modID;

public abstract class AbstractSQRelic extends CustomRelic {
    public AbstractCard.CardColor color;

    public AbstractSQRelic(String setId, AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        this(setId, null, tier, sfx);
    }

    public AbstractSQRelic(String setId, String packageName, AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        super(setId, TexLoader.getTexture(makeContributionPath(packageName, setId.replace(modID + ":", "") + ".png")), tier, sfx);
        outlineImg = TexLoader.getTexture(makeContributionPath(packageName, setId.replace(modID + ":", "") + "Outline.png"));
    }

    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}