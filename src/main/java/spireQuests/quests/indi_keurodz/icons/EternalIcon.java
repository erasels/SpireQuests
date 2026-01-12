package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.EternalStickerModifier;

public class EternalIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Eternal");
    private static EternalIcon singleton;

    public EternalIcon() {
        super(ID, EternalStickerModifier.icon);
    }

    public static EternalIcon get() {
        if (singleton == null) {
            singleton = new EternalIcon();
        }
        return singleton;
    }
}
