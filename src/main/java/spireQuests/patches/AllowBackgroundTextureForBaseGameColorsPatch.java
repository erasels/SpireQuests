package spireQuests.patches;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderFixSwitches;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import spireQuests.abstracts.AbstractSQCard;

public class AllowBackgroundTextureForBaseGameColorsPatch {
    @SpirePatch2(clz = RenderFixSwitches.RenderBgSwitch.class, method = "Prefix")
    public static class CardRenderPatch {
        @SpireInstrumentPatch
        public static ExprEditor useBackgroundTextureForBaseGameColors() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(BaseMod.class.getName()) && m.getMethodName().equals("isBaseGameCardColor")) {
                        m.replace(String.format("{ $_ = $proceed($$) && !%s.useBackgroundTexture(__instance); }", AllowBackgroundTextureForBaseGameColorsPatch.class.getName()));
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "renderCardBack", paramtypez = {SpriteBatch.class})
    public static class SingleCardViewPopupPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> useBackgroundTextureForBaseGameColors(SpriteBatch sb, AbstractCard ___card) {
            if (useBackgroundTexture(___card)) {
                AbstractSQCard card = (AbstractSQCard)___card;
                // Copied from how BaseMod renders these
                sb.draw(card.getBackgroundLargeTexture(), Settings.WIDTH / 2.0F - 512.0F, Settings.HEIGHT / 2.0F - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1024, 1024, false, false);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    public static boolean useBackgroundTexture(AbstractCard card) {
        if (card instanceof AbstractSQCard) {
            AbstractSQCard c = (AbstractSQCard)card;
            return c.textureBackgroundSmallImg != null && !c.textureBackgroundSmallImg.isEmpty() && c.textureBackgroundLargeImg != null && !c.textureBackgroundLargeImg.isEmpty();
        }
        return false;
    }
}
