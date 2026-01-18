package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.actions.FlexibleDiscoveryAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class JewelledBauble extends AbstractSQRelic {
    public static final String ID = makeID(JewelledBauble.class.getSimpleName());

    public JewelledBauble() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void atBattleStartPreDraw() {
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        List<AbstractCard> uncommons = AbstractDungeon.srcUncommonCardPool.group.stream()
                .filter(r -> r.rarity == AbstractCard.CardRarity.UNCOMMON)
                .filter(r -> !r.hasTag(AbstractCard.CardTags.HEALING))
                .map(AbstractCard::makeCopy)
                .collect(Collectors.toList());
        if (!uncommons.isEmpty()) {
            Collections.shuffle(uncommons, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));
            ArrayList<AbstractCard> choices = uncommons.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
            this.addToBot(new FlexibleDiscoveryAction(choices, false));
        }
    }
}
