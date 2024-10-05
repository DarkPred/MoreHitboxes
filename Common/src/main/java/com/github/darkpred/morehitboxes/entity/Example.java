package com.github.darkpred.morehitboxes.entity;

import com.github.darkpred.morehitboxes.api.EntityHitboxData;
import com.github.darkpred.morehitboxes.api.EntityHitboxDataFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

//TODO: add icon
public class Example extends Mob implements GeckoLibMultiPartEntity<Example> {

    private final EntityHitboxData<Example> placeHolderName = EntityHitboxDataFactory.create(this);

    protected Example(EntityType<? extends Example> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityHitboxData<Example> getPlaceHolderName() {
        return placeHolderName;
    }

    @Override
    public boolean partHurt(MultiPart<Example> multiPart, @NotNull DamageSource source, float amount) {
        return hurt(source, amount);
    }

    @Override
    public void setAnchorPos(String boneName, Vec3 localPos) {

    }

    @Override
    public boolean canSetAnchorPos(String boneName) {
        return false;
    }
}
