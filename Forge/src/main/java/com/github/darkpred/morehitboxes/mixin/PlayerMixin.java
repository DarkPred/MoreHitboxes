package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.entity.MultiPart;
import com.github.darkpred.morehitboxes.entity.MultiPartEntity;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Equivalent to what forge does with PartEntity
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * Set the target to the parent entity to allow for things like fire to work and share the part entity
     */
    @ModifyVariable(method = "attack", argsOnly = true, at = @At("HEAD"))
    private Entity replacePartWithParent(Entity target, @Share("part") LocalRef<Entity> partRef) {
        if (target instanceof MultiPart<?> part) {
            partRef.set(part.getEntity());
            return part.getParent();
        }
        return target;
    }

    /**
     * Replace the hurt call to the parent entity
     */
    @ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private Entity attackMultiPart(Entity target, DamageSource source, float amount, @Share("part") LocalRef<Entity> partRef) {
        if (target instanceof MultiPartEntity && partRef.get() != null) {
            return partRef.get();
        }
        return target;
    }
}
