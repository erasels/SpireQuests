package spireQuests.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.RelicMiscUtil;
import spireQuests.util.TexLoader;
import spireQuests.util.Wiz;

import java.util.ArrayList;

public class QuestboundRelicsPatch {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = SpirePatch.CLASS
    )
    public static class QuestboundRelicFields {
        public static SpireField<AbstractQuest> isQuestbound = new SpireField<>(()->null);
    }


    private static final Texture tex = TexLoader.getTexture(Anniv8Mod.modID + "Resources/images/ui/questboundIcon.png");
    private static float offsetX(AbstractRelic r) {
        return ReflectionHacks.getPrivate(r, AbstractRelic.class, "offsetX");
    }

    private static float rotation(AbstractRelic r) {
        return ReflectionHacks.getPrivate(r, AbstractRelic.class, "rotation");
    }

    @SpirePatch2(clz = AbstractRelic.class, method = "renderInTopPanel")
    public static class renderSB {
        @SpireInsertPatch(locator = Locator.class)
        public static void render(AbstractRelic __instance, SpriteBatch sb) {
            if(QuestboundRelicFields.isQuestbound.get(__instance) != null) {
                if (QuestboundRelicFields.isQuestbound.get(__instance).fail() || QuestboundRelicFields.isQuestbound.get(__instance).complete()) {
                    ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE);
                }
                sb.draw(tex, __instance.currentX + offsetX(__instance), __instance.currentY - 10.0F, 32.0F, 32.0F, 64.0F, 64.0F,
                        __instance.scale * 0.5F, __instance.scale * 0.5F, rotation(__instance), 0, 0, 64, 64, false, false);
                ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "renderCounter");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            QuestManager.quests().forEach(quest -> {
                if (quest.isFailed() || quest.isCompleted()) {
                    ArrayList<AbstractRelic> toRemove = new ArrayList<>();
                    for (AbstractRelic r : Wiz.adp().relics) {
                        if (QuestboundRelicFields.isQuestbound.get(r) == quest) {
                            toRemove.add(r);
                        }
                    }
                    for (AbstractRelic r : toRemove) {
                        RelicMiscUtil.removeSpecificRelic(r);
                    }
                }
            }
            );
        }
    }

}
