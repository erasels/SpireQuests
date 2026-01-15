
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class ManacleIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Manacle");
    private static ManacleIcon singleton;

    public ManacleIcon() {
        super(ID, BossBlind.Manacle.frames.get(0));
    }

    public static ManacleIcon get() {
        if (singleton == null) {
            singleton = new ManacleIcon();
        }
        return singleton;
    }
}
