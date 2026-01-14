
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class PlantIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Plant");
    private static PlantIcon singleton;

    public PlantIcon() {
        super(ID, BossBlind.Plant.frames.get(0));
    }

    public static PlantIcon get() {
        if (singleton == null) {
            singleton = new PlantIcon();
        }
        return singleton;
    }
}
