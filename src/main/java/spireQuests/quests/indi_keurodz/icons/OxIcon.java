
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class OxIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Ox");
    private static OxIcon singleton;

    public OxIcon() {
        super(ID, BossBlind.Ox.frames.get(0));
    }

    public static OxIcon get() {
        if (singleton == null) {
            singleton = new OxIcon();
        }
        return singleton;
    }
}
