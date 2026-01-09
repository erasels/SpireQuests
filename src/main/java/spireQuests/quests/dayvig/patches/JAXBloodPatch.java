package spireQuests.quests.dayvig.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.dayvig.relics.MutagenBlood;
import spireQuests.util.Wiz;

public class JAXBloodPatch {
    @SpirePatch(clz = JAX.class, method = "use", paramtypez = {AbstractPlayer.class, AbstractMonster.class})

    public static class MutagenBloodPatch {
        public static SpireReturn Prefix(JAX __instance, AbstractPlayer p, AbstractMonster m){
            if (AbstractDungeon.player.hasRelic(MutagenBlood.ID)) {
                Wiz.atb(new LoseHPAction(p, p, 1));
                Wiz.atb(new ApplyPowerAction(p, p, new StrengthPower(p, __instance.magicNumber), __instance.magicNumber));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
