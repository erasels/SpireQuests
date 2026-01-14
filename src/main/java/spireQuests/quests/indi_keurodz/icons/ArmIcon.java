package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class ArmIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Arm");
    private static ArmIcon singleton;

    public ArmIcon() {
        super(ID, BossBlind.Arm.frames.get(0));
    }

    public static ArmIcon get() {
        if (singleton == null) {
            singleton = new ArmIcon();
        }
        return singleton;
    }
}
