package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPart;
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

    @ModifyVariable(method = "attack", at = @At(value = "STORE"), ordinal = 1)
//TODO: Chcekc if this is still true: Can't use the name of the variable because that crashes in production
    private Entity replaceHurtEntity(Entity entity) {
        if (entity instanceof MultiPart<?> part) {
            return part.getParent();
        }
        return entity;
    }
}
