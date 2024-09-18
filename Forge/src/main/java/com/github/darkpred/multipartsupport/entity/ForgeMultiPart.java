package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.ForgeRegistrationFactory;
import com.github.darkpred.multipartsupport.registration.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class ForgeMultiPart<T extends Mob> extends PartEntity<T> implements MultiPart<T> {
    private final EntityDimensions size;
    private final Vec3 offset;
    private final String name;
    @Nullable
    private AnimationOverride animationOverride;

    public ForgeMultiPart(T parent, EntityHitboxManager.Hitbox hitbox) {
        super(parent);
    }

    @Override
    public MultiPart<T> create(T parent, EntityHitboxManager.Hitbox hitbox) {
        return new ForgeMultiPart<>(parent, hitbox);
    }
}
