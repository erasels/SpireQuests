package spireQuests.quests.soytheproton.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.soytheproton.MothQuest;
import spireQuests.quests.soytheproton.relics.IoMoth;
import spireQuests.quests.soytheproton.vfx.DelayedFlightEffect;
import spireQuests.quests.soytheproton.vfx.ExclamationParticle;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.quests.soytheproton.MothQuest.MOTH_SFX;

public class MothFriendPatch {

    public static AnimationState mothState;
    private static TextureAtlas mothAtlas = null;
    private static Skeleton mothSkeleton;
    private static AnimationStateData mothStateData;
    public static float drawX;
    public static float drawY;
    public static boolean flyingMoth;
    public static boolean isFlyingMoth;

    private static boolean hasMoth(boolean check) {
        if(check) {
            for (AbstractQuest q : QuestManager.quests()) {
                if (q instanceof MothQuest && !q.isFailed() || isFlyingMoth)
                    return true;
            }
            return AbstractDungeon.player.hasRelic(IoMoth.ID);
        } else {
            for(AbstractQuest q : QuestManager.quests()) {
                if(q instanceof MothQuest)
                    return true;
            }
            return false;
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "render")
    public static class RenderMoth {
        @SpireInsertPatch(locator = RenderLocator.class)
        public static void Insert(SpriteBatch sb) {
            if(!hasMoth(true))
                return;
            if(mothAtlas == null) {
                flyingMoth = false;
                mothAtlas = new TextureAtlas(Gdx.files.internal(makeContributionPath("soytheproton", "animations/QuestMoth.atlas")));
                SkeletonJson json = new SkeletonJson(mothAtlas);
                json.setScale(Settings.scale/4.5F);
                SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(makeContributionPath("soytheproton", "animations/QuestMoth.json")));
                mothSkeleton = new Skeleton(skeletonData);
                mothSkeleton.setColor(Color.WHITE);
                mothStateData = new AnimationStateData(skeletonData);
                mothState = new AnimationState(mothStateData);
                mothStateData.setMix("flyaway", "idle", 0.1F);
                mothState.setAnimation(0, "idle", true);
            }

            if(!flyingMoth) {
                drawX = AbstractDungeon.player.drawX - 100.0F * Settings.scale;
                drawY = AbstractDungeon.player.drawY + 200.0F * Settings.scale;
            }

            mothState.update(Gdx.graphics.getDeltaTime());
            mothState.apply(mothSkeleton);
            mothSkeleton.updateWorldTransform();
            mothSkeleton.setPosition(drawX, drawY);
            mothSkeleton.setColor(AbstractDungeon.player.tint.color);
            mothSkeleton.setFlip(false,false);
            sb.end();
            CardCrawlGame.psb.begin();
            AbstractCreature.sr.draw(CardCrawlGame.psb, mothSkeleton);
            CardCrawlGame.psb.end();
            sb.begin();
        }

        private static class RenderLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "render");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz= AbstractPlayer.class,method = "damage")
    public static class MothDamagePatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"damageAmount"})
        public static void Insert(DamageInfo info, int damageAmount) {
            if(!hasMoth(false) || isFlyingMoth)
                return;
            if(info.owner != null && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
                CardCrawlGame.sound.play(MOTH_SFX,0.1F);
                AbstractDungeon.effectList.add(new ExclamationParticle(drawX,drawY + 50.0F * Settings.scale));
                for(AbstractQuest q : QuestManager.quests()) {
                    if(q instanceof MothQuest && q.isFailed()) AbstractDungeon.effectList.add(new DelayedFlightEffect());
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class, "damageReceivedThisCombat");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz= AbstractRoom.class, method = "dispose")
    public static class DisposeMoth {
        @SpirePostfixPatch
        public static void Postfix() {
            if(mothAtlas != null) {
                mothAtlas.dispose();
                mothAtlas = null;
            }
        }
    }
}
