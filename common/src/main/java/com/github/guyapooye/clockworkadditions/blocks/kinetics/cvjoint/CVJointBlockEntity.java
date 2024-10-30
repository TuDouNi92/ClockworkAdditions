package com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint;

import com.github.guyapooye.clockworkadditions.registries.BlockRegistry;
import com.github.guyapooye.clockworkadditions.registries.ConfigRegistry;
import com.github.guyapooye.clockworkadditions.util.NumberUtil;
import com.github.guyapooye.clockworkadditions.util.PlatformUtil;
import com.github.guyapooye.clockworkadditions.util.WorldspaceUtil;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;
import org.valkyrienskies.core.apigame.constraints.VSSlideConstraint;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.Map;

import static com.github.guyapooye.clockworkadditions.util.WorldspaceUtil.getShipOrRigidBodyId;
import static com.github.guyapooye.clockworkadditions.util.WorldspaceUtil.getWorldSpace;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class CVJointBlockEntity extends KineticBlockEntity {

    public BlockPos target;
    public boolean isOrigin;
    private int jointId;
    private boolean shouldRefresh;

    public CVJointBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        isOrigin = compound.getBoolean("IsOrigin");
        target = NbtUtils.readBlockPos(compound.getCompound("Target"));
        jointId = compound.getInt("Id");
        shouldRefresh = true;
    }
    private void refresh() {
        if (shouldRefresh) {
            shouldRefresh = false;
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (target == null) return;
        compound.putBoolean("IsOrigin", isOrigin);
        compound.putInt("Id", jointId);
        compound.put("Target", NbtUtils.writeBlockPos(target));
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        if (target == null) return neighbours;
        neighbours.add(target);
        return neighbours;
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (!(target instanceof CVJointBlockEntity other)) return 0;
        if (this.target == null || other.target == null) return 0;
        float result = (other.target.equals(getBlockPos()) && this.target.equals(other.getBlockPos())) ? 1 : 0;
        if (stateFrom.getValue(FACING).getAxisDirection() == stateTo.getValue(FACING).getAxisDirection()) result *= -1;
        return result;
    }

    public void detach() {
        target = null;
        if (source != null && !source.equals(getBlockPos().subtract(getBlockState().getValue(FACING).getNormal()))) {
            detachKinetics();
            removeSource();
            requestModelDataUpdate();
        }
    }

    public void attach(BlockPos pos) {
        if (pos.equals(target)) return;
        if (target != null) {
            CVJointBlockEntity old = BlockRegistry.CV_JOINT.get().getBlockEntity(level, target);
            if (old != null) {
                old.detach();
            }
            detach();
        }
        target = pos;
        CVJointBlockEntity targ = BlockRegistry.CV_JOINT.get().getBlockEntity(level, target);
        if (targ == null) return;
        detach();
        target = pos;
        attachKinetics();
        targ.attach(getBlockPos());

        if(level.isClientSide) return;

        ServerShip shipOn = VSGameUtilsKt.getShipManagingPos((ServerLevel) level,pos);

        if (shipOn == null) return;

        VSAttachmentConstraint constraint = new VSAttachmentConstraint(
                getShipOrRigidBodyId(level, pos),
                shipOn.getId(),
                0,
                VectorConversionsMCKt.toJOMLD(pos).add(NumberUtil.blockPosOffset),
                VectorConversionsMCKt.toJOMLD(worldPosition).add(NumberUtil.blockPosOffset),
                10E10,
                ConfigRegistry.server().stretchables.cvJointMaxLength.get());

        Integer constraintId = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewConstraint(constraint);

        if (constraintId == null) {
            System.out.println("SOMETHING FUCKED UP");
            return;
        }
//
//        CVJointPhysData.CreateData createData = new CVJointPhysData.CreateData(new VSConstraintAndId(constraintId,constraint));
//        CVJointPhysStorage.getOrCreate(shipOn).addInducer(createData);
    }

    @Override
    public void tick() {
        super.tick();
        if (target == null) {
            detach();
            return;
        }
        CVJointBlockEntity other = BlockRegistry.CV_JOINT.get().getBlockEntity(level, target);
        if (other == null) {
            if (level.isLoaded(target))
                detach();
            return;
        };
        if (isOrigin == other.isOrigin) isOrigin = !other.isOrigin;
        if (getWorldSpace(level,worldPosition).sub(getWorldSpace(other.level, other.worldPosition)).lengthSquared() > Mth.square(ConfigRegistry.server().stretchables.cvJointMaxLength.get()+10)) {
            target = null;
            other.target = null;
            detachKinetics();
            other.detachKinetics();
        }
    }

    @Override
    protected boolean isNoisy() {
        return true;
    }
}
