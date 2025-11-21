package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.abstracts.AbstractSQRelic;

import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class CustomerAppreciationAward extends AbstractSQRelic {
    public static final String ID = makeID(CustomerAppreciationAward.class.getSimpleName());

    public CustomerAppreciationAward() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.gainGold(10);
        AbstractDungeon.player.increaseMaxHp(1, true);
        List<AbstractCard> upgradeableCommons = AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.rarity == AbstractCard.CardRarity.COMMON && c.canUpgrade()).collect(Collectors.toList());
        if (!upgradeableCommons.isEmpty()) {
            AbstractCard card = upgradeableCommons.get(AbstractDungeon.miscRng.random(upgradeableCommons.size() - 1));
            card.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(card);
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        }
    }
}
