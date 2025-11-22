package spireQuests.quests.modargo.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQPower;
import spireQuests.util.Wiz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class IgnitePower extends AbstractSQPower implements HealthBarRenderPower {
    public static final String POWER_ID = Anniv8Mod.makeID(IgnitePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public IgnitePower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "modargo", AbstractPower.PowerType.DEBUFF,false, owner, amount);
    }

    @Override
    public void atStartOfTurn() {
        if (!this.owner.isPlayer && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flashWithoutSound();
            playApplyPowerSfx();
            addToBot(new DamageAction(this.owner, new DamageInfo(Wiz.p(), amount, DamageInfo.DamageType.HP_LOSS)));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (owner.isPlayer) {
            flashWithoutSound();
            addToBot(new DamageAction(this.owner, new DamageInfo(Wiz.p(), amount, DamageInfo.DamageType.HP_LOSS)));
        }
    }

    @Override
    public void updateDescription() {
        description = (this.owner.isPlayer ? DESCRIPTIONS[0] : DESCRIPTIONS[1]).replace("{0}", this.amount + "");
    }

    @Override
    public int getHealthBarAmount() {
        return owner.isPlayer ? 0 : this.amount;
    }

    @Override
    public Color getColor() {
        return new Color(-5963521);
    }

    private static Constructor<?> igniteConstructor = null;

    public static AbstractPower create(AbstractCreature owner, int amount) {
        if (Loader.isModLoaded("anniv5")) {
            try {
                if (igniteConstructor == null) {
                    igniteConstructor = Class.forName("thePackmaster.powers.shamanpack.IgnitePower").getConstructor(AbstractCreature.class, int.class);
                }
                return (AbstractPower)igniteConstructor.newInstance(owner, amount);
            } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create IgnitePower");
            }
        }
        return new IgnitePower(owner, amount);
    }
}
