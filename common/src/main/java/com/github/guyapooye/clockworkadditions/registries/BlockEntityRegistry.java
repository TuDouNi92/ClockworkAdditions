package com.github.guyapooye.clockworkadditions.registries;

import com.github.guyapooye.clockworkadditions.blocks.bearings.flap.KineticFlapBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.bearings.flap.KineticFlapBearingRenderer;
import com.github.guyapooye.clockworkadditions.blocks.bearings.heli.PhysicsBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.bearings.heli.archived.BasePhysicsBearingBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.copycats.CWACopycatBlockEntity;
import com.github.guyapooye.clockworkadditions.blocks.copycats.wingalikes.CopycatWingBlock;
import com.github.guyapooye.clockworkadditions.blocks.fluids.extensiblehose.ExtensibleHoseBlock;
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
import com.github.guyapooye.clockworkadditions.blocks.redstone.gyro.RedstoneGyroBlockEntity;
import com.simibubi.create.content.contraptions.bearing.BearingInstance;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.github.guyapooye.clockworkadditions.ClockworkAdditions.REGISTRATE;

public class BlockEntityRegistry {
    public static final BlockEntityEntry<BasePhysicsBearingBlockEntity> BASE_PHYS_BEARING = REGISTRATE
            .blockEntity("base_physics_bearing", BasePhysicsBearingBlockEntity::new)
            .instance(() -> BearingInstance::new)
            .validBlocks(BlockRegistry.BASE_PHYSICS_BEARING)
            .renderer(() -> BearingRenderer::new)
            .register();

    public static final BlockEntityEntry<PhysicsBearingBlockEntity> HELI_PHYS_BEARING = REGISTRATE
            .blockEntity("heli_physics_bearing", PhysicsBearingBlockEntity::new)
            .instance(() -> BearingInstance::new)
            .validBlocks(BlockRegistry.HELI_PHYSICS_BEARING)
            .renderer(() -> BearingRenderer::new)
            .register();
    public static final BlockEntityEntry<KineticFlapBearingBlockEntity> KINETIC_FLAP_BEARING = REGISTRATE
            .blockEntity("kinetic_flap_bearing", KineticFlapBearingBlockEntity::new)
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
    public static final BlockEntityEntry<? extends CWACopycatBlockEntity> COPYCAT = REGISTRATE
            .blockEntity("copycat", CWACopycatBlockEntity::new)
            .validBlocks(BlockRegistry.COPYCAT_WING,BlockRegistry.COPYCAT_FLAP)
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
