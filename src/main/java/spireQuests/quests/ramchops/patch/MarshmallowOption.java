package spireQuests.quests.ramchops.patch;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import spireQuests.Anniv8Mod;
import spireQuests.quests.Trigger;
import spireQuests.quests.ramchops.effects.EatMarshmallowEffect;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.TexLoader.getTexture;
import static spireQuests.util.Wiz.adp;

public class MarshmallowOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID(MarshmallowOption.class.getSimpleName()));
    public static final String[] TEXT = uiStrings.TEXT;

    public MarshmallowOption(){

        this.label = TEXT[0];
        this.description = TEXT[1];
        this.usable = true;
        this.img = getTexture(makeContributionPath("ramchops", MarshmallowOption.class.getSimpleName()+ ".png"));
    }

    @Override
    public void useOption() {
        AbstractDungeon.effectList.add(new EatMarshmallowEffect());
    }
}
