
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class WaterIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Water");
    private static WaterIcon singleton;

    public WaterIcon() {
        super(ID, BossBlind.Water.frames.get(0));
    }

    public static WaterIcon get() {
        if (singleton == null) {
            singleton = new WaterIcon();
        }
        return singleton;
    }
}
