package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Equivalent to what forge does with PartEntity
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow
    private Entity camera;

    @Shadow
    public abstract Entity getCamera();

    @Redirect(method = "setCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;camera:Lnet/minecraft/world/entity/Entity;", opcode = Opcodes.PUTFIELD))
    public void setCameraNoMultiPart(ServerPlayer instance, Entity entityToSpectate) {
        Entity entity = getCamera();
        if (entity instanceof MultiPart<?> part) {
            camera = part.getParent();
        }
        if (camera == null) {
            camera = entityToSpectate == null ? this.getCamera() : entityToSpectate;
        }
    }
}
