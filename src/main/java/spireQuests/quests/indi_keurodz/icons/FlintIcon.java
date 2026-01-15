package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class FlintIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Flint");
    private static FlintIcon singleton;

    public FlintIcon() {
        super(ID, BossBlind.Flint.frames.get(0));
    }

    public static FlintIcon get() {
        if (singleton == null) {
            singleton = new FlintIcon();
        }
        return singleton;
    }
}
