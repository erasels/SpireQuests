package spireQuests.quests.indi_keurodz.patches;

import basemod.ReflectionHacks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;

public class ShowBossBlindsOnMapPatch {

    private static final float FRAME_TIME = 0.08f;

    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class BossBlindField {
        // 21 frames of animation
        public static final SpireField<Array<AtlasRegion>> frames = new SpireField<>(() -> null);
        public static final SpireField<Integer> current_frame = new SpireField<>(() -> 0);
        public static final SpireField<Float> elapsed_time = new SpireField<>(() -> 0f);
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class RenderPatch {

        @SpireInsertPatch(locator = Locator.class)
        public static void renderImage(MapRoomNode __instance, SpriteBatch sb) {
            Array<AtlasRegion> frames = BossBlindField.frames.get(__instance);
            int current_frame = BossBlindField.current_frame.get(__instance);
            if (frames != null) {
                AtlasRegion image = frames.get(current_frame);
                int imgWidth = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "IMG_WIDTH");
                float scale = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");
                float offsetX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
                float offsetY = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
                float spacingX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");

                sb.setColor(Color.WHITE);
                sb.draw(image,
                        (float) __instance.x * spacingX + offsetX - 60.0F + __instance.offsetX + imgWidth * scale,
                        (float) __instance.y * Settings.MAP_DST_Y + offsetY + DungeonMapScreen.offsetY - 68.0F
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
}
