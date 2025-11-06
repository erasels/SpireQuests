package spireQuests.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.ui.QuestUI;

public class QuestUIPatches {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "render"
    )
    public static class RenderUI {

        private static final float X_POS = Settings.WIDTH - (10f * Settings.scale);
        private static final float COMPATIBILITY_OFFSET = 0; //should check if some other stuff adds right side UI?
        private static final float Y_POS = ((float) ReflectionHacks.getPrivateStatic(AbstractRelic.class, "START_Y")) - (64f * Settings.scale) - COMPATIBILITY_OFFSET;

        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractDungeon __instance, SpriteBatch sb) {
            QuestUI.update(X_POS, Y_POS); //eh
            QuestUI.render(sb, X_POS, Y_POS);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "topGradientColor");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
