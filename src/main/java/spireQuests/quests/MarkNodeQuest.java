package spireQuests.quests;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.util.NodeUtil;

import java.util.ArrayList;

public interface MarkNodeQuest {

    /**
     * This is called to mark nodes when the quest is picked up.
     * You need to implement this method; it will be called by the logic in MarkNodesOnMapPatch.
     * Make sure to use rng for any random selection (not rng()!) and make sure rng is used the same on pickup, and on save and reload.
     * Use ShowMarkedNodesOnMapPatch.MarkNode() in the function to mark nodes.
     * @param map The map of slay the spire, as a list of rows, which are lists of nodes.
     * @param rng the variables used to randomly determine stuff
     */
    void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng);

    /**
     * Automatically generate a new Random based on seed, act number, and quest id.
     * You only need to @Override this if the marks added by your quest depend on factors other than the current act or seed (like TreasureMapQuest, which depends on the node the quest was picked up at).
     * @return new Random for determining stuff randomly
     */
    default Random rng() {
        return new Random(Settings.seed ^ AbstractDungeon.actNum * 31L ^ ((AbstractQuest) this).id.hashCode());
    }
}
