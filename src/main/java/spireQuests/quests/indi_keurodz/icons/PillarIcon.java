
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class PillarIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Pillar");
    private static PillarIcon singleton;

    public PillarIcon() {
        super(ID, BossBlind.Pillar.frames.get(0));
    }

    public static PillarIcon get() {
        if (singleton == null) {
            singleton = new PillarIcon();
        }
        return singleton;
    }
}
