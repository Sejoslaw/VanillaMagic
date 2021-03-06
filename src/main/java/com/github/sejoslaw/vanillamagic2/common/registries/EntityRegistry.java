package com.github.sejoslaw.vanillamagic2.common.registries;

import com.github.sejoslaw.vanillamagic2.common.entities.EntityMeteor;
import com.github.sejoslaw.vanillamagic2.common.entities.EntitySpell;
import com.github.sejoslaw.vanillamagic2.core.VanillaMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, VanillaMagic.MODID);

    public static final RegistryObject<EntityType<EntitySpell>> SPELL = register(EntitySpell.class.getName().toLowerCase(), EntitySpell::new);
    public static final RegistryObject<EntityType<EntityMeteor>> METEOR = register(EntityMeteor.class.getName().toLowerCase(), EntityMeteor::new);

    public static void initialize() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.IFactory<T> factory) {
        return ENTITIES.register(id, () -> buildEntityType(factory, id));
    }

    private static <T extends Entity> EntityType<T> buildEntityType(EntityType.IFactory<T> factory, String registryName) {
        return EntityType.Builder
                .create(factory, EntityClassification.MISC)
                .setShouldReceiveVelocityUpdates(true)
                .setTrackingRange(500)
                .setUpdateInterval(600)
                .size(1.0F, 1.0F)
                .build(new ResourceLocation(VanillaMagic.MODID, registryName).toString());
    }
}
