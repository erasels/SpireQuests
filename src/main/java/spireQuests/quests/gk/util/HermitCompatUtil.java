package spireQuests.quests.gk.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.util.Wiz;

import java.lang.reflect.Field;

public final class HermitCompatUtil {

    // Hermit's IDs
    private static final String BIGSHOT_ID = "hermit:BigShotPower";
    private static final String COMBO_ID = "hermit:ComboPower";
    private static final String SNIPE_ID = "hermit:SnipePower";
    private static final String BLACKPOWDER_ID = "hermit:BlackPowder";

    // VigorPatch.isActive
    private static Field vigorIsActive;
    // BlackPowder.OOMPH
    private static final int BLACKPOWDER_OOMPH = 2;

    private static void init() {
        if (vigorIsActive != null) return;

        try {
            vigorIsActive = Class.forName("hermit.patches.VigorPatch").getDeclaredField("isActive");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns how many Dead On hits should occur.
     * This is 1 normally, +1 if Snipe Power is present.
     */
    public static int getDeadOnTimes(int baseTimes) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null && p.hasPower(SNIPE_ID))
            return baseTimes + 1;

        return baseTimes;
    }

    /**
     * Called after Deadon effect happened
     */
    public static void postDeadOnEffects(AbstractPlayer p, int times) {
        init();

        // BigShot
        if (p.hasPower(BIGSHOT_ID) && vigorIsActive != null) {
            try {
                int v = vigorIsActive.getInt(null);
                vigorIsActive.setInt(null, v + times);
            } catch (Exception ignored) {
            }
        }

        // Combo
        if (p.hasPower(COMBO_ID)) {
            try {
                AbstractPower comb = p.getPower(COMBO_ID);
                Class<?> cls = comb.getClass();

                Field usesF = cls.getDeclaredField("uses");
                Field amountF = cls.getDeclaredField("amount");
                usesF.setAccessible(true);
                amountF.setAccessible(true);

                int uses = usesF.getInt(comb);
                int amount = amountF.getInt(comb);

                if (uses < amount) {
                    usesF.setInt(comb, uses + 1);
                    cls.getMethod("flash").invoke(comb);
                }
            } catch (Exception ignored) {
            }
        }

        // Snipe
        if (p.hasPower(SNIPE_ID)) {
            AbstractPower snipe = p.getPower(SNIPE_ID);
            snipe.flash();
            AbstractDungeon.actionManager.addToTop(new ReducePowerAction(p, p, SNIPE_ID, 1));
        }

        // BlackPowder (for each hit)
        for (int i = 0; i < times; i++) {
            for (AbstractRelic r : p.relics) {
                if (BLACKPOWDER_ID.equals(r.relicId)) {
                    Wiz.atb(new RelicAboveCreatureAction(p, r));
                    Wiz.atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(BLACKPOWDER_OOMPH, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE, true));
                }
            }
        }
    }
}
