package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;

public class EternalIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Eternal");
    private static EternalIcon singleton;

    public EternalIcon() {
        super(ID, EternalModifier.icon);
    }

    public static EternalIcon get() {
        if (singleton == null) {
            singleton = new EternalIcon();
        }
        return singleton;
    }
}
