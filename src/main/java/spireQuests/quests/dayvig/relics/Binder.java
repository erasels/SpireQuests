package spireQuests.quests.dayvig.relics;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.abstracts.AbstractSQRelic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static spireQuests.Anniv8Mod.makeID;

public class Binder extends AbstractSQRelic {

    public static final String ID = makeID(Binder.class.getSimpleName());

    public Binder() {
        super(ID, "dayvig", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public void onEquip() {
        ArrayList<AbstractCard> upgradableCards = new ArrayList();
        for (AbstractCard c: AbstractDungeon.player.masterDeck.group){
            if (c.canUpgrade()){ upgradableCards.add(c); }
        }

        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));

        if (upgradableCards.isEmpty()){
            return;
        }
        if (upgradableCards.size() == 1){
            upgradableCards.get(0).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        }
        else {
            upgradableCards.get(0).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
            upgradableCards.get(1).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 20.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect((upgradableCards.get(1)).makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        }
        }
}

