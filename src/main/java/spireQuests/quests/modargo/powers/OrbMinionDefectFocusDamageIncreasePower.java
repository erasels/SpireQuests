package spireQuests.quests.modargo.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import spireQuests.abstracts.AbstractSQPower;
import spireQuests.quests.modargo.monsters.DefectEliteMonster;

import static spireQuests.Anniv8Mod.makeID;

public class OrbMinionDefectFocusDamageIncreasePower extends AbstractSQPower implements InvisiblePower {
    public static String POWER_ID = makeID(OrbMinionDefectFocusDamageIncreasePower.class.getSimpleName());

    public OrbMinionDefectFocusDamageIncreasePower(AbstractCreature owner) {
        super(POWER_ID, "", "modargo", NeutralPowertypePatch.NEUTRAL, false, owner, -1);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage + (float) DefectEliteMonster.getFocus() : damage;
    }
}
