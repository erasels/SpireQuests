
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class HouseIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("House");
    private static HouseIcon singleton;

    public HouseIcon() {
        super(ID, BossBlind.House.frames.get(0));
    }

    public static HouseIcon get() {
        if (singleton == null) {
            singleton = new HouseIcon();
        }
        return singleton;
    }
}
