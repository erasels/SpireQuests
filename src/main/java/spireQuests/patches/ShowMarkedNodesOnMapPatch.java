package spireQuests.patches;

import basemod.Pair;
import basemod.ReflectionHacks;
import basemod.helpers.TooltipInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public class ShowMarkedNodesOnMapPatch {

    public static class TipInfo {
        float x;
        float y;
        String header;
        String body;

        public TipInfo(float x, float y, String header, String body) {
            this.x = x;
            this.y = y;
            this.header = header;
            this.body = body;
        }
    }

    static TipInfo tooltip;

    @FunctionalInterface
    public interface Renderer {
        void render(SpriteBatch sb, float x, float y, float scale);
    }

    public static class NodeMark {
        private final Renderer render;
        private final Predicate<Object> equalz;

        private float animTime = 0f;
        private int frame = 0;

        private TooltipInfo tooltip;

        public NodeMark(Texture tex, TooltipInfo tooltip) {
            render = (sb, x, y, scale) -> {
                sb.draw(tex, x, y, 64.0F, 64.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
            };
            equalz = (o) -> o.equals(tex);
            this.tooltip = tooltip;
        }

        public NodeMark(Array<AtlasRegion> regions, float fps, TooltipInfo tooltip) {
            final float secondsPerFrame = 1f / fps;

            render = (sb, x, y, scale) -> {
                animTime += Gdx.graphics.getDeltaTime();
                if (animTime >= secondsPerFrame) {
                    animTime -= secondsPerFrame;
                    frame = (frame + 1) % regions.size;
                }
                sb.draw(regions.get(frame), x, y, 64.0F, 64.0F, 64.0F, 64.0F, scale, scale, 0.0F);
            };
            equalz = (o) -> o.equals(regions);
            this.tooltip = tooltip;
        }

        public void render(SpriteBatch sb, float x, float y, float scale) {
            render.render(sb, x, y, scale);
        }

        public boolean equalz(Object o) {
            return equalz.test(o);
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class ImageField {
        public static final SpireField<ArrayList<Pair<String, NodeMark>>> images = new SpireField<>(ArrayList::new);

        /**
         * Mark a node on the map.
         *
         * @param node    Spot on map to mark.
         * @param questID Quest that makes the mark.
         * @param texture Texture to use as mark.
         * @return whether the marking was successful.
         */
        public static boolean MarkNode(MapRoomNode node, String questID, Texture texture) {
            return MarkNode(node, questID, texture, null);
        }

        public static boolean MarkNode(MapRoomNode node, String questID, Texture texture, TooltipInfo tooltip) {
            ArrayList<Pair<String, NodeMark>> textures = images.get(node);
            if (textures.size() >= 8) {
                LogManager.getLogger().info("Too many markings! Marking was not added");
                return false;
            }
            textures.add(new Pair<>(questID, new NodeMark(texture, tooltip)));
            ImageField.images.set(node, textures);
            return true;
        }

        /**
         * Mark a node on the map.
         *
         * @param node    Spot on map to mark.
         * @param questID Quest that makes the mark.
         * @param regions AtlasRegions for animated mark.
         * @param fps     fps at which to play the animation.
         * @return whether the marking was successful.
         */
        public static boolean MarkNode(MapRoomNode node, String questID, Array<AtlasRegion> regions, float fps) {
            return MarkNode(node, questID, regions, fps, null);
        }

        public static boolean MarkNode(MapRoomNode node, String questID, Array<AtlasRegion> regions, float fps,
                TooltipInfo tooltip) {
            ArrayList<Pair<String, NodeMark>> textures = images.get(node);
            if (textures.size() >= 8) {
                LogManager.getLogger().info("Too many markings! Marking was not added");
                return false;
            }
            textures.add(new Pair<>(questID, new NodeMark(regions, fps, tooltip)));
            ImageField.images.set(node, textures);
            return true;
        }

        /**
         * Checks if a node has a mark of a quest.
         *
         * @param node    Node to check.
         * @param questID Quest to check against.
         * @return if a mark made by quest QuestID is found on the node.
         */
        public static boolean CheckMarks(MapRoomNode node, String questID) {
            return CheckMarks(node, questID, (Texture) null);
        }

        /**
         * Checks if a node has a mark of a quest.
         *
         * @param node    Node to check.
         * @param questID Quest to check against.
         * @param texture texture to check.
         * @return if a mark with Texture texture made by quest QuestID is found on the
         *         node.
         */
        public static boolean CheckMarks(MapRoomNode node, String questID, Texture texture) {
            for (Pair<String, NodeMark> pair : images.get(node)) {
                if (pair.getKey().equals(questID) && (texture == null || pair.getValue().equalz(texture))) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks if a node has a mark of a quest.
         *
         * @param node    Node to check.
         * @param questID Quest to check against.
         * @param regions Atlas regions to check.
         * @return if a mark with rehions Array<AtlasRegion> made by quest QuestID is
         *         found on the node.
         */
        public static boolean CheckMarks(MapRoomNode node, String questID, Array<AtlasRegion> regions) {
            for (Pair<String, NodeMark> pair : images.get(node)) {
                if (pair.getKey().equals(questID) && (regions == null || pair.getValue().equalz(regions))) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Clears all marks of a quest on the map.
         *
         * @param questID ID of quest of which marks to remove.
         */
        public static void ClearMarks(String questID) {
            ClearMarks(questID, null);
        }

        /**
         * Clears all marks with specific Texture of a quest on the map.
         *
         * @param questID ID of quest of which marks to remove.
         * @param texture texture of marks to remove.
         */
        public static void ClearMarks(String questID, Texture texture) {
            for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                for (MapRoomNode node : row) {
                    ClearMark(node, questID, texture);
                }
            }
        }

        /**
         * Clears all marks of a quest on a node.
         *
         * @param questID ID of quest of which marks to remove.
         */
        public static void ClearMark(MapRoomNode node, String questID) {
            ClearMark(node, questID, null);
        }

        /**
         * Clears all marks with specific Texture of a quest on a node.
         *
         * @param node    node from which to remove marks.
         * @param questID ID of quest of which marks to remove.
         * @param texture texture of marks to remove.
         */
        public static void ClearMark(MapRoomNode node, String questID, Texture texture) {
            ArrayList<Pair<String, NodeMark>> textures = images.get(node);
            textures.removeIf(
                    pair -> pair.getKey().equals(questID) && (texture == null || pair.getValue().equalz(texture)));
            images.set(node, textures);
        }

        public static boolean hasTooltip(MapRoomNode node) {
            for (Pair<String, NodeMark> pair : images.get(node)) {
                if (pair.getValue().tooltip != null) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Patch that adds rendering of marks to the render function that renders map
     * icons
     */
    @SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = { SpriteBatch.class })
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
            float y = (float) __instance.y * Settings.MAP_DST_Y + offsetY + DungeonMapScreen.offsetY - 64.0F
                    + __instance.offsetY;

            ArrayList<Float> orderOffsetX = new ArrayList<>(Arrays.asList(1f, 1f, 0f, 0f, 1.5f, .5f, -.5f, .5f));
            ArrayList<Float> orderOffsetY = new ArrayList<>(Arrays.asList(1f, 0f, 0f, 1f, .5f, -.5f, .5f, 1.5f));

            ArrayList<Pair<String, NodeMark>> imageList = ImageField.images.get(__instance);
            imageList.sort(Comparator.comparing(Pair::getKey));
            for (Pair<String, NodeMark> pair : imageList) {
                NodeMark mark = pair.getValue();
                if (images >= 8) {
                    LogManager.getLogger().info("Too many markings! Only showing the first 8.");
                    break;
                }
                if (mark != null) {
                    sb.setColor(Color.WHITE);
                    mark.render(sb, x + orderOffsetX.get(images) * imgWidth * scale,
                            y + orderOffsetY.get(images) * imgHeight * scale, scale);

                    if (mark.tooltip != null && __instance.hb.hovered) {

                        float tooltip_offset_x = imgWidth * 2f;
                        float tooltip_offset_y = imgHeight * 1.75f;

                        tooltip = new TipInfo(x + tooltip_offset_x, y + tooltip_offset_y, mark.tooltip.title,
                                mark.tooltip.description);
                    }

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

    @SpirePatch2(clz = DungeonMap.class, method = "renderMapBlender")
    public static class RenderTooltipsPatch {

        @SpirePrefixPatch
        public static void RenderTipsEarly() {
            if (tooltip != null) {
                TipHelper.renderGenericTip(tooltip.x, tooltip.y, tooltip.header, tooltip.body);
            }
            tooltip = null;
        }
    }

}
