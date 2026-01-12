package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class FishIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Fish");
    private static FishIcon singleton;

    public FishIcon() {
        super(ID, BossBlind.Fish.frames.get(0));
    }

    public static FishIcon get() {
        if (singleton == null) {
            singleton = new FishIcon();
        }
        return singleton;
    }
}
