package spireQuests.quests.iry.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

// BrokenHeartPendant:
// Two's Company Quest reward -
// Gain 2 Strength while there exactly 2 enemies.
public class BrokenHeartPendant extends AbstractSQRelic {
    private static final int STR_AMT = 2;
    private boolean isActive = false;

    public static final String ID = makeID(BrokenHeartPendant.class.getSimpleName());

    public BrokenHeartPendant() {
        super(ID, "iry", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    // Check monster count and update strength at battle start or when monster count changes
    public void atBattleStart() {
        this.isActive = false;
        updateStrength();
    }

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        updateStrength();
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        updateStrength();
    }

    // Update check placed in action queue similar to Red Skull
    public void updateStrength(){
        this.addToBot(new AbstractGameAction() {
            public void update() {
                boolean twos = exactlyTwoMonsters();
                if (twos && !isActive) {
                    BrokenHeartPendant.this.flash(); // same procedure as red skull
                    BrokenHeartPendant.this.pulse = true;
                    AbstractPlayer p = AbstractDungeon.player;
                    this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, STR_AMT), STR_AMT));
                    this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, BrokenHeartPendant.this));
                    BrokenHeartPendant.this.isActive = true;
                    AbstractDungeon.player.hand.applyPowers();
                } else if(!twos){
                    if (isActive) {
                        AbstractPlayer p = AbstractDungeon.player;
                        this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, -STR_AMT), -STR_AMT));
                    }
                    BrokenHeartPendant.this.stopPulse();
                    BrokenHeartPendant.this.isActive = false;
                    AbstractDungeon.player.hand.applyPowers();
                }

                this.isDone = true;
            }
        });
    }

    public static boolean exactlyTwoMonsters(){
        int count = 0;
        for(AbstractMonster m2: AbstractDungeon.getCurrRoom().monsters.monsters){
            if (!m2.isDeadOrEscaped()) {
                ++count;
            }
        }
        return count == 2;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0], STR_AMT);
    }
}
