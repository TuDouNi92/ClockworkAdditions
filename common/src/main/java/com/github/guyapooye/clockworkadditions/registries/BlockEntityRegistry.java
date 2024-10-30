package com.github.guyapooye.clockworkadditions.registries;

import com.github.guyapooye.clockworkadditions.blocks.bearings.flap.KineticFlapBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.bearings.flap.KineticFlapBearingInstance;
import com.github.guyapooye.clockworkadditions.blocks.bearings.flap.KineticFlapBearingRenderer;
import com.github.guyapooye.clockworkadditions.blocks.fluids.extensiblehose.ExtensibleHoseBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint.CVJointBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint.CVJointInstance;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint.CVJointRenderer;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.handlebar.HandlebarBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.handlebar.HandlebarInstance;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.invertedresistor.InvertedRedstoneResistorBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.invertedresistor.InvertedRedstoneResistorRenderer;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.pedals.PedalsBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.pedals.PedalsInstance;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.pedals.PedalsRenderer;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.temp.BasePhysicsBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic.AestheticBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic.AestheticBearingInstance;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic.AestheticBearingRenderer;
import com.github.guyapooye.clockworkadditions.blocks.redstone.gyro.RedstoneGyroBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;

import static com.github.guyapooye.clockworkadditions.ClockworkAdditions.REGISTRATE;

public class BlockEntityRegistry {
    public static final BlockEntityEntry<BasePhysicsBearingBlockEntity> BASE_BEARING = REGISTRATE
            .blockEntity("base_bearing", BasePhysicsBearingBlockEntity::new)
            .validBlocks(BlockRegistry.BASE_BEARING)
            .register();
    public static final BlockEntityEntry<AestheticBearingBlockEntity> AESTHETIC_BEARING = REGISTRATE
            .blockEntity("aesthetic_bearing", AestheticBearingBlockEntity::new)
            .instance(() -> AestheticBearingInstance::new)
            .validBlocks(BlockRegistry.AESTHETIC_BEARING)
            .renderer(() -> AestheticBearingRenderer::new)
            .register();
    public static final BlockEntityEntry<KineticFlapBearingBlockEntity> KINETIC_FLAP_BEARING = REGISTRATE
            .blockEntity("kinetic_flap_bearing", KineticFlapBearingBlockEntity::new)
            .instance(() -> KineticFlapBearingInstance::new)
            .validBlocks(BlockRegistry.KINETIC_FLAP_BEARING)
            .renderer(() -> KineticFlapBearingRenderer::new)
            .register();
    public static final BlockEntityEntry<PedalsBlockEntity> PEDALS = REGISTRATE
            .blockEntity("mechanical_pedals", PedalsBlockEntity::new)
            .instance(() -> PedalsInstance::new)
            .validBlocks(BlockRegistry.PEDALS)
            .renderer(() -> PedalsRenderer::new)
            .register();
    public static final BlockEntityEntry<? extends HandlebarBlockEntity> HANDLEBAR = REGISTRATE
            .blockEntity("handlebar", HandlebarBlockEntity::new)
            .instance(() -> HandlebarInstance::new)
            .validBlocks(BlockRegistry.HANDLEBAR)
            .register();;
    public static final BlockEntityEntry<RedstoneGyroBlockEntity> REDSTONE_GYRO = REGISTRATE
            .blockEntity("redstone_gyro", RedstoneGyroBlockEntity::new)
            .validBlocks(BlockRegistry.REDSTONE_GYRO)
            .register();
    public static final BlockEntityEntry<CVJointBlockEntity> CV_JOINT = REGISTRATE
            .blockEntity("cv_joint", CVJointBlockEntity::new)
            .instance(() -> CVJointInstance::new)
            .validBlocks(BlockRegistry.CV_JOINT)
            .renderer(() -> CVJointRenderer::new)
            .register();
    public static final BlockEntityEntry<InvertedRedstoneResistorBlockEntity> INVERTED_RESISTOR = REGISTRATE
            .blockEntity("inverted_redstone_resistor", InvertedRedstoneResistorBlockEntity::new)
            .validBlocks(BlockRegistry.INVERTED_RESISTOR)
            .renderer(() -> InvertedRedstoneResistorRenderer::new)
            .register();
    public static final BlockEntityEntry<? extends ExtensibleHoseBlockEntity<?>> EXTENSIBLE_HOSE = registerExtensibleHose();
    @ExpectPlatform
    public static BlockEntityEntry<? extends ExtensibleHoseBlockEntity<?>> registerExtensibleHose() {
        throw new AssertionError();
    }

    public static void register() {}
}
