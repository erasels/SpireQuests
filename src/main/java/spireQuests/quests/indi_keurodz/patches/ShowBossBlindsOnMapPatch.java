package spireQuests.quests.indi_keurodz.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import basemod.ReflectionHacks;
import javassist.CtBehavior;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class ShowBossBlindsOnMapPatch {

    private static final float FRAME_TIME = 0.085f;

    private static final float OFFSET_Y = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
    private static final float OFFSET_X = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
    private static final float SPACING_X = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");

    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class BossBlindField {
        // 21 frames of animation
        public static final SpireField<BossBlind> blind = new SpireField<>(() -> null);
        public static final SpireField<Integer> current_frame = new SpireField<>(() -> 0);
        public static final SpireField<Float> elapsed_time = new SpireField<>(() -> 0f);
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class RenderPatch {

        @SpireInsertPatch(locator = Locator.class)
        public static void renderImage(MapRoomNode __instance, SpriteBatch sb) {
            BossBlind blind = BossBlindField.blind.get(__instance);
            int current_frame = BossBlindField.current_frame.get(__instance);
            if (blind != null) {
                AtlasRegion image = blind.frames.get(current_frame);
                int imgWidth = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "IMG_WIDTH");
                float scale = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");

                sb.setColor(Color.WHITE);
                sb.draw(image,
                        __instance.x * SPACING_X + OFFSET_X - 68.0F + __instance.offsetX + imgWidth * scale,
                        __instance.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 68.0F
                                + __instance.offsetY + 68.0F * scale,
                        68f, 68f, 68f, 68f, scale * Settings.scale, scale * Settings.scale, 0f);
            }

            float elapsed_time = BossBlindField.elapsed_time.get(__instance);
            BossBlindField.elapsed_time.set(__instance, elapsed_time + Gdx.graphics.getDeltaTime());

            if (BossBlindField.elapsed_time.get(__instance) > FRAME_TIME) {
                BossBlindField.current_frame.set(__instance, (current_frame + 1) % 21);
                BossBlindField.elapsed_time.set(__instance, elapsed_time - FRAME_TIME);
            }

        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MapRoomNode.class, "renderEmeraldVfx");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class RenderTooltipsOnHoverPatch {

        @SpirePostfixPatch()
        public static void renderTooltip(MapRoomNode __instance, SpriteBatch sb) {
            BossBlind blind = BossBlindField.blind.get(__instance);

            if (blind != null && __instance.hb.hovered) {
                int imgWidth = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "IMG_WIDTH");
                float scale = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");

                TipHelper.renderGenericTip(
                        __instance.x * SPACING_X + OFFSET_X - 64f + __instance.offsetX + imgWidth * 1.75f,
                        __instance.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 64f
                                + __instance.offsetY + 64f * scale,
                        blind.tooltip.title,
                        blind.tooltip.description);

            }

        }

    }
}
