package com.github.darkpred.multipartsupport.entity;

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
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeMultiPart<T extends Mob & MultiPartEntity<T>> extends PartEntity<T> implements MultiPart<T> {
    private final EntityDimensions size;
    private final Vec3 offset;
    private final String name;
    @Nullable
    private AnimationOverride animationOverride;

    public ForgeMultiPart(T parent, EntityHitboxManager.HitboxData hitboxData) {
        super(parent);
        this.size = EntityDimensions.scalable(hitboxData.width(), hitboxData.height());
        this.offset = hitboxData.pos();
        this.name = hitboxData.name();
        this.refreshDimensions();
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        return getParent().interact(player, hand);
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
        return getParent().partHurt(this, source, amount);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        setRemoved(reason);
    }

    @Override
    public boolean is(@NotNull Entity entity) {
        return this == entity || getParent() == entity;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        if (animationOverride != null) {
            return size.scale(getParent().getScale()).scale(animationOverride.scaleW(), animationOverride.scaleH());
        }
        return size.scale(getParent().getScale());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
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

    static class ForgeMultiPartFactory implements MultiPart.Factory {

        @Override
        public <T extends Mob & MultiPartEntity<T>> MultiPart<T> create(T parent, EntityHitboxManager.HitboxData hitboxData) {
            return new ForgeMultiPart<>(parent, hitboxData);
        }
    }
}
