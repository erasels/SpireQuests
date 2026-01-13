package spireQuests.quests.indi_keurodz.patches;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import basemod.helpers.CardModifierManager;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import static spireQuests.Anniv8Mod.makeShaderPath;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.modifiers.FoilModifier;
import spireQuests.quests.indi_keurodz.modifiers.HoloModifier;
import spireQuests.quests.indi_keurodz.modifiers.NegativeModifier;
import spireQuests.quests.indi_keurodz.modifiers.PolychromeModifier;

public class EditionShaders {

    private static FrameBuffer fbo;
    private static ShaderProgram negativeShader;
    private static ShaderProgram polychromeShader;
    private static ShaderProgram foilShader;
    private static ShaderProgram holoShader;

    private static final Logger logger = LogManager.getLogger(EditionShaders.class.getName());

    private static float time = 0f;

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.CardCrawlGame", method = "update")
    public static class ShaderTime {

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(Object __obj_instance) {
            time += Gdx.graphics.getRawDeltaTime();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.helpers.input.InputHelper",
                        "updateLast");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderImage")
    public static class ImageCapture {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            if (shouldApplyShader(__instance)) {
                beginCapture(sb);
            }
        }

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            if (notNegativeShader(__instance)) {
                endCaptureAndRender(__instance, sb);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderTint")
    public static class NegativeCapture {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            if (CardModifierManager.hasModifier(__instance, NegativeModifier.ID)) {
                endCaptureAndRender(__instance, sb);
            }
        }
    }

    private static boolean shouldApplyShader(AbstractCard card) {
        if (Settings.hideCards)
            return false;
        return CardModifierManager.hasModifier(card, NegativeModifier.ID) ||
                CardModifierManager.hasModifier(card, FoilModifier.ID) ||
                CardModifierManager.hasModifier(card, HoloModifier.ID) ||
                CardModifierManager.hasModifier(card, PolychromeModifier.ID);
    }

    private static boolean notNegativeShader(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, NegativeModifier.ID)) {
            return false;
        }
        return CardModifierManager.hasModifier(card, FoilModifier.ID) ||
                CardModifierManager.hasModifier(card, HoloModifier.ID) ||
                CardModifierManager.hasModifier(card, PolychromeModifier.ID);
    }

    private static void beginCapture(SpriteBatch sb) {
        if (fbo == null) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false,
                    false);
        }

        sb.end();
        fbo.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(true, true, true, true);

        sb.begin();

        IntBuffer buf_rgb = BufferUtils.newIntBuffer(16);
        IntBuffer buf_a = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_RGB, buf_rgb);
        Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_ALPHA, buf_a);
        Gdx.gl.glBlendEquationSeparate(buf_rgb.get(0), GL30.GL_MAX);
        Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_MAX);
    }

    private static void endCaptureAndRender(AbstractCard card, SpriteBatch sb) {
        Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_FUNC_ADD);

        sb.end();
        fbo.end();
        sb.begin();

        TextureRegion t = new TextureRegion(fbo.getColorBufferTexture());
        t.flip(false, true);

        if (CardModifierManager.hasModifier(card, NegativeModifier.ID)) {
            if (negativeShader == null)
                negativeShader = loadShader("negative.frag");
            if (negativeShader != null) {
                renderWithShader(card, sb, t, negativeShader, (shader) -> {
                });
                return;
            }
        }

        if (CardModifierManager.hasModifier(card, FoilModifier.ID)) {

            if (foilShader == null)
                foilShader = loadShader("foil.frag");
            if (foilShader != null) {
                renderWithShader(card, sb, t, foilShader, (shader) -> {
                    float slowTime = time * 0.08f;
                    shader.setUniformf("u_roll", slowTime, slowTime);
                });
                return;
            }
        }

        if (CardModifierManager.hasModifier(card, HoloModifier.ID)) {
            if (holoShader == null)
                holoShader = loadShader("holo.frag");
            if (holoShader != null) {
                renderWithShader(card, sb, t, holoShader, (shader) -> {
                    float holoOffset = (float) (card.uuid.hashCode() % 100);
                    shader.setUniformf("u_holo", holoOffset, holoOffset / 100f);

                    shader.setUniformf("u_time", time);
                });
                return;
            }
        }

        if (CardModifierManager.hasModifier(card, PolychromeModifier.ID)) {
            if (polychromeShader == null)
                polychromeShader = loadShader("polychrome.frag");
            if (polychromeShader != null) {
                renderWithShader(card, sb, t, polychromeShader, (shader) -> {
                    float polyOffset = (float) (card.uuid.hashCode() % 100);
                    shader.setUniformf("u_polychrome", polyOffset, polyOffset / 100f);

                    shader.setUniformf("u_time", time * 2f);
                });
                return;
            }
        }

        // Fallback
        sb.draw(t, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);
    }

    private interface UniformSetter {
        void apply(ShaderProgram shader);
    }

    private static void renderWithShader(AbstractCard card, SpriteBatch sb, TextureRegion t, ShaderProgram shader,
            UniformSetter uniformLogic) {
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShaderProgram oldShader = sb.getShader();
        sb.setShader(shader);

        float w = 300f * card.drawScale * Settings.scale;
        float h = 420f * card.drawScale * Settings.scale;
        float x = card.current_x - (w / 2f);
        float y = card.current_y - (h / 2f);
        shader.setUniformf("u_targetRect", x, y, w, h);

        uniformLogic.apply(shader);

        sb.draw(t, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);

        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setShader(oldShader);
    }

    private static ShaderProgram loadShader(String fragFileName) {
        String vertPath = makeShaderPath(BalatroQuest.AUTHOR + "/passthrough.vert");
        String fragPath = makeShaderPath(BalatroQuest.AUTHOR + "/" + fragFileName);

        try {
            ShaderProgram shader = new ShaderProgram(
                    Gdx.files.internal(vertPath),
                    Gdx.files.internal(fragPath));

            if (!shader.isCompiled()) {
                logger.error("Shader compile error: " + fragFileName);
                logger.error(shader.getLog());
            }
            return shader;
        } catch (Exception e) {
            logger.error("Failed to load shader: " + fragFileName);
            logger.error(e);
            return null;
        }
    }

}
