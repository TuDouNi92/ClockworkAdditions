package com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic;

import com.github.guyapooye.clockworkadditions.registries.PartialModelRegistry;
import com.github.guyapooye.clockworkadditions.util.NumberUtil;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.content.kinetics.base.BackHalfShaftInstance;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class AestheticBearingInstance extends BackHalfShaftInstance<AestheticBearingBlockEntity> implements DynamicInstance {
    final OrientedData topInstance;

    final Vector3f rotationAxis;
    final Quaterniond blockOrientation;
    final Direction facing;
    private final Vector3d normalInWorld;
    final ClientShip shipOn;

    public AestheticBearingInstance(MaterialManager materialManager, AestheticBearingBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        facing = blockState.getValue(BlockStateProperties.FACING);
        rotationAxis = Direction.get(Direction.AxisDirection.POSITIVE, axis).step();

        blockOrientation = getBlockStateOrientation(facing);

        shipOn = VSGameUtilsKt.getShipObjectManagingPos((ClientLevel)world,pos);

        normalInWorld = VectorConversionsMCKt.toJOMLD(facing.getNormal());

        topInstance = getOrientedMaterial().getModel(PartialModelRegistry.AESTHETIC_BEARING_TOP, blockState).createInstance();

        rotateTop();
    }

    private void rotateTop() {

        Quaterniond orientation = new Quaterniond(blockOrientation);

        Vector3d centerInWorldRelative = new Vector3d();
        if (blockEntity.shiptraptionId != -1L) {
            ClientShip shiptraption = VSGameUtilsKt.getShipObjectWorld((ClientLevel)world).getAllShips().getById(blockEntity.shiptraptionId);
            if (shiptraption != null) {
                ShipTransform shiptraptionTransform = shiptraption.getRenderTransform();
                Quaterniond shipOrientation; shipOrientation = (Quaterniond) shiptraptionTransform.getShipToWorldRotation();

                BlockPos center = pos.relative(facing);
                int shipChunkX = shiptraption.getChunkClaim().getXMiddle();
                int shipChunkZ = shiptraption.getChunkClaim().getZMiddle();

                Vector3d posInWorld = new Vector3d(pos.getX(),pos.getY(),pos.getZ());

                Vector3d centerJoml = posInWorld.add(NumberUtil.blockPosOffset).fma(.5, normalInWorld, new Vector3d());
                Vector3d centerInShip = new Vector3d((shipChunkX << 4) + (center.getX() & 15), center.getY(), (shipChunkZ << 4) + (center.getZ() & 15))
                        .add(NumberUtil.blockPosOffset)
                        .fma(-.5,normalInWorld)
                ;
                Quaterniond shipOnOrientation = new Quaterniond();
//            System.out.println("B: " + centerJoml.toString(NumberFormat.getInstance()));
                if (shipOn != null) {
                    shipOnOrientation = new Quaterniond(shipOn.getRenderTransform().getShipToWorldRotation());
                    Matrix4d shipToWorld = new Matrix4d(shipOn.getRenderTransform().getShipToWorld());
                    shipToWorld.transformPosition(posInWorld);
                    shipToWorld.transformPosition(centerJoml);
                }
                centerJoml.sub(posInWorld);
                shipOnOrientation.invert().transform(centerJoml);

                shipOrientation.transform(centerJoml);
                centerJoml.add(posInWorld);
                centerInWorldRelative = shiptraptionTransform.getShipToWorld().transformPosition(centerInShip).sub(centerJoml, new Vector3d());
                shipOnOrientation.transform(centerInWorldRelative);

                shipOrientation.mul(orientation,orientation);
                shipOnOrientation.mul(orientation,orientation);
            }
        }


        topInstance.setPosition(getInstancePosition())
                .nudge((float) centerInWorldRelative.x, (float) centerInWorldRelative.y, (float) centerInWorldRelative.z)
                .setRotation(VectorConversionsMCKt.toMinecraft(orientation))
        ;
    }


    @Override
    public void beginFrame() {
        rotateTop();
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, topInstance);
    }

    @Override
    public void remove() {
        super.remove();
        topInstance.delete();
    }

    @Override
    public void update() {
        super.update();
        rotateTop();
    }

    private Quaterniond getBlockStateOrientation(Direction facing) {
        Quaternion orientation;

        if (facing.getAxis().isHorizontal()) {
            orientation = Vector3f.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.getOpposite()));
        } else {
            orientation = Quaternion.ONE.copy();
        }

        orientation.mul(Vector3f.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)));
        return new Quaterniond(orientation.i(),orientation.j(),orientation.k(),orientation.r());
    }
}
