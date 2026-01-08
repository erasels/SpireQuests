package spireQuests.patches;

import basemod.Pair;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ShowMarkedNodesOnMapPatch {
    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class ImageField {
        public static final SpireField<ArrayList<Pair<String, Texture>>> images = new SpireField<>(ArrayList::new);

        /**
         * Mark a node on the map.
         * @param node Spot on map to mark.
         * @param questID Quest that makes the mark.
         * @param texture Texture to use as mark.
         * @return whether the marking was successful.
         */
        public static boolean MarkNode(MapRoomNode node, String questID, Texture texture){
            ArrayList<Pair<String, Texture>> textures = images.get(node);
            if(textures.size() >= 8){
                LogManager.getLogger().info("Too many markings! Marking was not added");
                return false;
            }
            textures.add(new Pair<>(questID, texture));
            ImageField.images.set(node, textures);
            return true;
        }

        /**
         * Checks if a node has a mark of a quest.
         * @param node Node to check.
         * @param questID Quest to check against.
         * @return if a mark made by quest QuestID is found on the node.
         */
        public static boolean CheckMarks(MapRoomNode node, String questID){ return CheckMarks(node, questID, null); }

        /**
         * Checks if a node has a mark of a quest.
         * @param node Node to check.
         * @param questID Quest to check against.
         * @param texture texture to check.
         * @return if a mark with Texture texture made by quest QuestID is found on the node.
         */
        public static boolean CheckMarks(MapRoomNode node, String questID, Texture texture){
            for(Pair<String, Texture> pair : images.get(node)){
                if(pair.getKey().equals(questID) && (texture == null || pair.getValue().equals(texture))){
                    return true;
                }
            }
            return false;
        }

        /**
         * Clears all marks of a quest on the map.
         * @param questID ID of quest of which marks to remove.
         */
        public static void ClearMarks(String questID){ ClearMarks(questID, null); }

        /**
         * Clears all marks with specific Texture of a quest on the map.
         * @param questID ID of quest of which marks to remove.
         * @param texture texture of marks to remove.
         */
        public static void ClearMarks(String questID, Texture texture){
            for(ArrayList<MapRoomNode> row : AbstractDungeon.map){
                for(MapRoomNode node : row){
                    ClearMark(node, questID, texture);
                }
            }
        }

        /**
         * Clears all marks of a quest on a node.
         * @param questID ID of quest of which marks to remove.
         */
        public static void ClearMark(MapRoomNode node, String questID){ ClearMark(node, questID, null); }

        /**
         * Clears all marks with specific Texture of a quest on a node.
         * @param node node from which to remove marks.
         * @param questID ID of quest of which marks to remove.
         * @param texture texture of marks to remove.
         */
        public static void ClearMark(MapRoomNode node, String questID, Texture texture){
            ArrayList<Pair<String, Texture>> textures = images.get(node);
            textures.removeIf(pair -> pair.getKey().equals(questID) && (texture == null || pair.getValue().equals(texture)));
            images.set(node, textures);
        }
    }

    /**
     * Patch that adds rendering of marks to the render function that renders map icons
     */
    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RenderPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void renderImage(MapRoomNode __instance, SpriteBatch sb) {
            int images = 0;

            Integer imgWidth = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "IMG_WIDTH");
            float imgHeight = 48.0F;
            float scale = ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");
            float offsetX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
            float offsetY = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
            float spacingX = ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");
            float x = (float) __instance.x * spacingX + offsetX - 64.0F + __instance.offsetX;
            float y = (float) __instance.y * Settings.MAP_DST_Y + offsetY + DungeonMapScreen.offsetY - 64.0F + __instance.offsetY;

            ArrayList<Float> orderOffsetX = new ArrayList<>(Arrays.asList(1f, 1f, 0f, 0f, 1.5f, .5f, -.5f, .5f));
            ArrayList<Float> orderOffsetY = new ArrayList<>(Arrays.asList(1f, 0f, 0f, 1f, .5f, -.5f, .5f, 1.5f));

            ArrayList<Pair<String, Texture>> imageList = ImageField.images.get(__instance);
            imageList.sort(Comparator.comparing(Pair::getKey));
            for(Pair<String, Texture> pair : imageList) {
                Texture image = pair.getValue();
                if(images >= 8){
                    LogManager.getLogger().info("Too many markings! Only showing the first 8.");
                    break;
                }
                if (image != null) {
                    sb.setColor(Color.WHITE);
                    sb.draw(image, x + orderOffsetX.get(images)*imgWidth * scale, y + orderOffsetY.get(images)*imgHeight * scale, 64.0F, 64.0F, 64.0F, 64.0F, scale * Settings.scale, scale * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                    images++;
                }
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