package com.github.darkpred.morehitboxes.internal;

import com.github.darkpred.morehitboxes.api.AnimationOverride;
import com.github.darkpred.morehitboxes.api.HitboxData;
import com.github.darkpred.morehitboxes.api.MultiPart;
import com.github.darkpred.morehitboxes.api.MultiPartEntity;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class FabricMultiPart<T extends Mob & MultiPartEntity<T>> extends Entity implements MultiPart<T> {
    public final T parent;
    private final EntityDimensions size;
    private final Vec3 offset;
    private final String partName;
    @Nullable
    private AnimationOverride animationOverride;

    public FabricMultiPart(T parent, HitboxData hitboxData) {
        super(parent.getType(), parent.level);
        this.parent = parent;
        this.size = EntityDimensions.scalable(hitboxData.width(), hitboxData.height());
        this.offset = hitboxData.pos();
        this.partName = hitboxData.name();
        this.refreshDimensions();
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        return parent.interact(player, hand);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        return parent.partHurt(this, source, amount);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        setRemoved(reason);
    }

    @Override
    public boolean is(@NotNull Entity entity) {
        return this == entity || parent == entity;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        if (animationOverride != null) {
            return size.scale(parent.getScale()).scale(animationOverride.scaleW(), animationOverride.scaleH());
        }
        return size.scale(parent.getScale());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public String getPartName() {
        return partName;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public Vec3 getOffset() {
        return offset;
    }

    @Override
    public void setOverride(AnimationOverride newOverride) {
        if (animationOverride != null && (animationOverride.scaleH() != newOverride.scaleH() || animationOverride.scaleW() != newOverride.scaleW())) {
            animationOverride = newOverride;
            refreshDimensions();
        } else {
            animationOverride = newOverride;
        }
    }

    @Override
    public AnimationOverride getOverride() {
        return animationOverride;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {

    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @ApiStatus.Internal
    @AutoService(MultiPart.Factory.class)
    public static class FabricMultiPartFactory implements MultiPart.Factory {

        @Override
        public <T extends Mob & MultiPartEntity<T>> MultiPart<T> create(T parent, HitboxData hitboxData) {
            return new FabricMultiPart<>(parent, hitboxData);
        }
    }
}
