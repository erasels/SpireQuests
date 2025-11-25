
package spireQuests.quests.indi_keurodz.patches;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class DisableLoseGoldSfxPatch {
    public static boolean ENABLED = false;

    @SpirePatch2(clz = AbstractPlayer.class, method = "loseGold")
    public static class IdkMan {
        @SpireInstrumentPatch
        public static ExprEditor noGoldSfx() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(SoundMaster.class.getName())
                            && m.getMethodName().equals("play")) {
                        m.replace(String.format("if (!%s.ENABLED) { $_ = $proceed($$); }",
                                DisableLoseGoldSfxPatch.class.getName()));
                    }
                }
            };
        }
    }

}
