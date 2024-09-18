package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mob.class)
//TODO: I think this is how its done. Maybe have to extend LivingEntity instead. https://fabricmc.net/wiki/tutorial:interface_injection
public class MobMixin extends Mob implements MultiPartEntity {
}
