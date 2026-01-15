
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class WallIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Wall");
    private static WallIcon singleton;

    public WallIcon() {
        super(ID, BossBlind.Wall.frames.get(0));
    }

    public static WallIcon get() {
        if (singleton == null) {
            singleton = new WallIcon();
        }
        return singleton;
    }
}
