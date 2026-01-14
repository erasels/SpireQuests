package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;

public class PerishableIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Perishable");
    private static PerishableIcon singleton;

    public PerishableIcon() {
        super(ID, PerishableModifier.icon);
    }

    public static PerishableIcon get() {
        if (singleton == null) {
            singleton = new PerishableIcon();
        }
        return singleton;
    }
}
