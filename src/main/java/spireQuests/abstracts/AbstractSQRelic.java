package spireQuests.abstracts;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static spireQuests.Anniv8Mod.*;
import static spireQuests.util.TexLoader.getTexture;

public abstract class AbstractSQRelic extends CustomRelic {
    public AbstractCard.CardColor color;
    private static final Texture MISSING_TEXTURE = getTexture(makeImagePath("ui/missing.png"));

    public AbstractSQRelic(String setId, AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        this(setId, null, tier, sfx);
    }

    public AbstractSQRelic(String setId, String packageName, AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        super(setId, getTexture(makeContributionPath(packageName, setId.replace(modID + ":", "") + ".png")), tier, sfx);
        outlineImg = getTexture(makeContributionPath(packageName, setId.replace(modID + ":", "") + "Outline.png"));
        Texture large = getTexture(makeContributionPath(packageName, setId.replace(modID + ":", "") + "Large.png"));
        if(large != MISSING_TEXTURE) largeImg = large;
    }

    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}