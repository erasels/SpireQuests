package spireQuests.quests.modargo.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;

public class ShowMarkedNodesOnMapPatch {
    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class ImageField {
        public static final SpireField<Texture> image = new SpireField<>(() -> null);
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RenderPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void renderImage(MapRoomNode __instance, SpriteBatch sb) {
            Texture image = ImageField.image.get(__instance);
            if (image != null) {
                int imgWidth = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "IMG_WIDTH");
                float scale = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");
                float offsetX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
                float offsetY = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
                float spacingX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");

                sb.setColor(Color.WHITE);
                sb.draw(image, (float)__instance.x * spacingX + offsetX - 64.0F + __instance.offsetX + imgWidth * scale, (float)__instance.y * Settings.MAP_DST_Y + offsetY + DungeonMapScreen.offsetY - 64.0F + __instance.offsetY + 48.0F * scale, 64.0F, 64.0F, 64.0F, 64.0F, scale * Settings.scale, scale * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
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
