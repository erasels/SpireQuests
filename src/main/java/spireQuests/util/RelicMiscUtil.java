package spireQuests.util;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.Anniv8Mod;

public class RelicMiscUtil {

    public static void removeRelicFromPool(AbstractRelic r) {
        switch (r.tier) {
            case COMMON:
                AbstractDungeon.commonRelicPool.remove(r.relicId);
                break;
            case UNCOMMON:
                AbstractDungeon.uncommonRelicPool.remove(r.relicId);
                break;
            case RARE:
                AbstractDungeon.rareRelicPool.remove(r.relicId);
                break;
            case BOSS:
                AbstractDungeon.bossRelicPool.remove(r.relicId);
                break;
            case SHOP:
                AbstractDungeon.shopRelicPool.remove(r.relicId);
                break;
            case SPECIAL:
            case STARTER:
                break;
            default:
                Anniv8Mod.logger.info("{} tier attempted to be removed from pool.", r.tier.name());
                break;
        }
    }

    public static void addRelicToPool(AbstractRelic r) {
        switch (r.tier) {
            case COMMON:
                AbstractDungeon.commonRelicPool.add(AbstractDungeon.relicRng.random(AbstractDungeon.commonRelicPool.size() - 1),r.relicId);
                break;
            case UNCOMMON:
                AbstractDungeon.uncommonRelicPool.add(AbstractDungeon.relicRng.random(AbstractDungeon.uncommonRelicPool.size() - 1),r.relicId);
                break;
            case RARE:
                AbstractDungeon.rareRelicPool.add(AbstractDungeon.relicRng.random(AbstractDungeon.rareRelicPool.size() - 1),r.relicId);
                break;
            case BOSS:
                AbstractDungeon.bossRelicPool.add(AbstractDungeon.relicRng.random(AbstractDungeon.bossRelicPool.size() - 1),r.relicId);
                break;
            case SHOP:
                AbstractDungeon.shopRelicPool.add(AbstractDungeon.relicRng.random(AbstractDungeon.shopRelicPool.size() - 1),r.relicId);
                break;
            default:
                Anniv8Mod.logger.info("{} tier attempted to be added to pool.", r.tier.name());
                break;
        }
    }

    public static void removeSpecificRelic(AbstractRelic r) {
        AbstractRelic toRemove = null;
        for(AbstractRelic relic : Wiz.adp().relics) {
            if(relic == r) {
                r.onUnequip();
                toRemove = relic;
                break;
            }
        }
        if(toRemove == null) {
            Anniv8Mod.logger.info("Tried to remove {}, but wasn't found in ADP relics", r.name);
        } else {
            Wiz.adp().relics.remove(r);
            Wiz.adp().reorganizeRelics();
        }
    }

    public static void removeSpecificRelic(int i) {
        removeSpecificRelic(Wiz.adp().relics.get(i));
    }
}
