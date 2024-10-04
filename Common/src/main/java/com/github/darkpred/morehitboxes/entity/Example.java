package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.api.IPlaceHolderName;
import com.github.darkpred.multipartsupport.api.PlaceHolderNameFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

//TODO: Change license to Mit, add icon
public class Example extends Mob implements com.github.darkpred.multipartsupport.entity.GeckoLibMultiPartEntity<Example> {

    private final IPlaceHolderName<Example> placeHolderName = PlaceHolderNameFactory.create(this);

    protected Example(EntityType<? extends Example> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public IPlaceHolderName<Example> getPlaceHolderName() {
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
