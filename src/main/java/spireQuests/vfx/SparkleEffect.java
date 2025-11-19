package spireQuests.vfx;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.UncommonPotionParticleEffect;

public class SparkleEffect extends UncommonPotionParticleEffect {
    public SparkleEffect(Hitbox hb, Color col) {
        super(hb);
        color = col.cpy();
        float xVariation = (hb.width / 2f) * MathUtils.random(-0.5f, 0.5f);
        float yVariation = (hb.height / 2f) * MathUtils.random(-0.5f, 0.5f);
        ReflectionHacks.setPrivate(this, UncommonPotionParticleEffect.class, "oX", xVariation);
        ReflectionHacks.setPrivate(this, UncommonPotionParticleEffect.class, "oY", yVariation);
    }
}
