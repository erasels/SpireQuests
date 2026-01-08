package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.quests.gk.actions.TrapshotAction;
import spireQuests.quests.gk.util.HermitCompatUtil;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.quests.gk.util.HermitCompatUtil.HERMIT_GUN_EFFECT;
import static spireQuests.util.CompatUtil.*;

@NoPools
@NoCompendium
public class Trapshot extends AbstractBPCard {
    public static final String ID = makeID(Trapshot.class.getSimpleName());

    public Trapshot() {
        super(ID, "gk", 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY, HERMIT_COLOR);
        tags.add(DEADON_TAG);

        baseDamage = damage = 6;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (isDeadOn()) {
            int times = HermitCompatUtil.getDeadOnTimes(1);
            addToBot(new TrapshotAction(m, new DamageInfo(p, damage, damageTypeForTurn), times));
            // Trigger Hermit powers/relics
            HermitCompatUtil.postDeadOnEffects(p, times);
        } else {
            dmg(m, HERMIT_GUN_EFFECT);
        }
    }

    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (isDeadOnPos()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upp() {
        upgradeDamage(2);
    }

    public boolean isDeadOn() {
        boolean conc = AbstractDungeon.player.hasPower("hermit:Concentration");

        double hand_pos = (AbstractDungeon.player.hand.group.indexOf(this) + 0.5);
        double hand_size = (AbstractDungeon.player.hand.size());
        double relative = Math.abs(hand_pos - hand_size / 2);

        return relative < 1 || conc;
    }

    public boolean isDeadOnPos() {
        boolean conc = AbstractDungeon.player.hasPower("hermit:Concentration");

        double hand_pos = (AbstractDungeon.player.hand.group.indexOf(this) + 0.5);
        double hand_size = (AbstractDungeon.player.hand.size());
        double relative = Math.abs(hand_pos - hand_size / 2);

        return (relative < 1 || conc);
    }
}
