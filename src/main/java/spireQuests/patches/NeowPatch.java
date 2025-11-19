package spireQuests.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.ui.QuestBoardProp;
import spireQuests.util.ActUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NeowPatch {
    @SpirePatch2(clz = NeowRoom.class, method = "render", paramtypez = SpriteBatch.class)
    public static class PostRender {
        @SpirePostfixPatch
        public static void Render(SpriteBatch sb) {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.render(sb);
            }
        }
    }

    // Required for custom events that override neow from ActLikeIt
    @SpirePatch2(clz = EventRoom.class, method = "render", paramtypez = SpriteBatch.class)
    public static class PostRenderEvent {
        @SpirePostfixPatch
        public static void Render(EventRoom __instance, SpriteBatch sb) {
            if (QuestBoardProp.questBoardProp != null &&
                    AbstractDungeon.floorNum <= 1 &&
                    CUSTOM_NEOW_EVENTS.stream().anyMatch(e -> e.isInstance(__instance.event))) {
                QuestBoardProp.questBoardProp.render(sb);
            }
        }
    }

    @SpirePatch(clz = NeowRoom.class, method = "update")
    public static class PostUpdate {
        @SpirePostfixPatch
        public static void Update() {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.update();
            }
        }
    }

    // Required for custom events that override neow from ActLikeIt
    @SpirePatch(clz = EventRoom.class, method = "update")
    public static class PostUpdateEvent {
        @SpirePostfixPatch
        public static void Update(EventRoom __instance) {
            if (QuestBoardProp.questBoardProp != null &&
                    AbstractDungeon.floorNum <= 1 &&
                    CUSTOM_NEOW_EVENTS.stream().anyMatch(e -> e.isInstance(__instance.event))) {
                QuestBoardProp.questBoardProp.update();
            }
        }
    }

    // We patch Exordium's constructor instead of NeowRoom's constructor because saving happens right after NeowRoom's
    // constructor is called, and we don't want that because it would make the quests generated for the board be saved
    // as seen immediately, causing inconsistencies if the user saves and reloads while still at Neow
    @SpirePatch(clz = Exordium.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractPlayer.class, ArrayList.class})
    @SpirePatch(clz = Exordium.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractPlayer.class, SaveFile.class})
    public static class ConstructorPatch {
        @SpirePostfixPatch
        public static void ConstructorPatch() {
            if (AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.room instanceof NeowRoom) {
                QuestBoardProp.questBoardProp = new QuestBoardProp((float) Settings.WIDTH * 0.5F - 425.0F * Settings.xScale, AbstractDungeon.floorY + 189.0F * Settings.yScale, true);
            }
        }
    }

    private static final Set<Class> CUSTOM_NEOW_EVENTS = new HashSet<>();
    @SpirePatch(cls="actlikeit.dungeons.CustomDungeon", method = SpirePatch.CONSTRUCTOR, paramtypes = {"actlikeit.dungeons.CustomDungeon", "com.megacrit.cardcrawl.characters.AbstractPlayer", "java.util.ArrayList"}, requiredModId = "actlikeit")
    public static class CustomDungeonPatch {
        @SpirePostfixPatch
        public static void patch(Object __instance, Object cd, AbstractPlayer player, ArrayList<String> emptyList) throws ClassNotFoundException {
            if (AbstractDungeon.currMapNode != null) {
                boolean normalNeow = AbstractDungeon.currMapNode.room instanceof NeowRoom;
                boolean customEvent = false;
                if(!normalNeow && ActUtil.getRealActNum() == 1 && AbstractDungeon.currMapNode.room instanceof EventRoom) {
                    Class eventClass = ReflectionHacks.getPrivate(cd, Class.forName("actlikeit.dungeons.CustomDungeon"), "onEnter");
                    if(eventClass != null) {
                        CUSTOM_NEOW_EVENTS.add(eventClass);
                        customEvent = true;
                    }
                }

                if(normalNeow || customEvent) {
                    QuestBoardProp.questBoardProp = new QuestBoardProp((float) Settings.WIDTH * 0.5F - 425.0F * Settings.xScale, AbstractDungeon.floorY + 189.0F * Settings.yScale, true);
                }
            }
        }
    }
}