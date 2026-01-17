package spireQuests.quests.quorry;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.unique.GreedAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQCard;

@NoPools
@NoCompendium
public class LaborStrike extends AbstractSQCard {
    public static final String ID = Anniv8Mod.makeID(LaborStrike.class.getSimpleName());

    private static final int DAMAGE = 9;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int MAGIC = 9;
    private static final int UPGRADE_MAGIC = 3;

    public LaborStrike() {
        super(ID, "quorry", 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY);
        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.tags.add(CardTags.STRIKE);
        this.tags.add(CardTags.STARTER_STRIKE);
    }

    @Override
    public void upp() {
        this.upgradeDamage(UPGRADE_DAMAGE);
        this.upgradeMagicNumber(UPGRADE_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GreedAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.magicNumber));
    }
}