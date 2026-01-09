package spireQuests.util;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NodeUtil {
    public static boolean canPathToElite() {
        return canPathToNode(node -> node.room instanceof MonsterRoomElite);
    }

    public static boolean canPathToNode(Function<MapRoomNode, Boolean> condition) {
        MapRoomNode start = AbstractDungeon.getCurrMapNode();
        List<MapRoomNode> processing = new ArrayList<>();
        Set<MapRoomNode> seen = new HashSet<>();
        if (start.y == -1) {
            processing.addAll(AbstractDungeon.map.get(0));
        }
        else {
            processing.add(start);
        }

        while (!processing.isEmpty()) {
            MapRoomNode current = processing.remove(0);
            if (seen.contains(current)) {
                continue;
            }
            if (condition.apply(current)) {
                return true;
            }
            processing.addAll(current.getEdges().stream().map(e -> getNode(e.dstX, e.dstY)).filter(n -> n != null && !seen.contains(n)).collect(Collectors.toList()));
            seen.add(current);
        }
        return false;
    }

    private static MapRoomNode getNode(int x, int y) {
        if (y >= 0 && y < AbstractDungeon.map.size()) {
            List<MapRoomNode> row = AbstractDungeon.map.get(y);
            if (x >= 0 && x < row.size()) {
                return row.get(x);
            }
        }
        return null;
    }
}
