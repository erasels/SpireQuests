
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class SerpentIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Serpent");
    private static SerpentIcon singleton;

    public SerpentIcon() {
        super(ID, BossBlind.Serpent.frames.get(0));
    }

    public static SerpentIcon get() {
        if (singleton == null) {
            singleton = new SerpentIcon();
        }
        return singleton;
    }
}
