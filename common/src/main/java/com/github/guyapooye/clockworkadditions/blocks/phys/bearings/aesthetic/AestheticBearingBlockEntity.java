package com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic;

import com.github.guyapooye.clockworkadditions.util.GlueAssembler;
import com.github.guyapooye.clockworkadditions.util.NumberUtil;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.*;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.lang.Math;
import java.util.Map;
import java.util.Objects;

import static com.github.guyapooye.clockworkadditions.util.NumberUtil.isBasicallyZeroF;

public class AestheticBearingBlockEntity extends GeneratingKineticBlockEntity implements IDisplayAssemblyExceptions, IBearingBlockEntity {

    protected float angle;
    @Getter
    protected boolean running;
    @Setter
    protected boolean assembleNextTick;
    protected AssemblyException lastException;
    @Getter
    protected long shiptraptionId = -1L;
    private boolean shouldRefresh;
    private boolean hasAssembled = false;
    private long lastSpeed;

    public AestheticBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    public Ship getShiptraption() {
        return VSGameUtilsKt.getAllShips(level).getById(shiptraptionId);
    }

    @Override
    public float getGeneratedSpeed() {
//        Ship shiptraption = getShiptraption();
//        if (shiptraption == null) return 0;
////        System.out.println((float) shiptraption.getOmega().length());
//        return lastSpeed;
        return 0;
    }

    public void tick() {
        if (level.isClientSide) return;
        if (this.assembleNextTick) {
            this.assembleNextTick = false;
            if (!this.running) {
                this.assemble();
            }
        }
        ServerShip shiptraption = (ServerShip) getShiptraption();
        if (shiptraption != null) {
            long newSpeed = Math.round(shiptraption.getOmega().length());
            if (Math.abs(newSpeed-lastSpeed)>1 || isBasicallyZeroF(newSpeed, 5)) {
                Ship shipOn = VSGameUtilsKt.getShipManagingPos(level,worldPosition);

                Vector3d normalInWorld = VectorConversionsMCKt.toJOMLD(getBlockState().getValue(BearingBlock.FACING).getNormal());
                Vector3d axis = normalInWorld;
                if (shipOn != null) shipOn.getTransform().getShipToWorldRotation().transform(normalInWorld);
                newSpeed *= shiptraption.getTransform().getShipToWorldRotation().transformInverse(axis).angle(normalInWorld) > 90 ? -1 : 1;
                lastSpeed = newSpeed;
                updateGeneratedRotation();
            };
            if (shouldRefresh && hasAssembled) {
                AestheticBearingController controller = AestheticBearingController.getOrCreate(shiptraption);
                AestheticBearingData blockData = controller.data;
                if (blockData != null) {

                    boolean createdHingeConstraint = false,
                            createdAttachConstraint = false;

                    Ship shipOn = VSGameUtilsKt.getShipManagingPos(level,worldPosition);
                    Map<String, Long> dimensionPhysBody = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getDimensionToGroundBodyIdImmutable();
                    long otherShipId = dimensionPhysBody.get(VSGameUtilsKt.getDimensionId(level));
                    if (shipOn != null) {
                        otherShipId = shipOn.getId();
                    }

                    VSAttachmentConstraint attachConstraint = blockData.getAttachConstraint();

                    Vector3dc localPos1 = attachConstraint.getLocalPos0();
                    Vector3dc localPos2 = attachConstraint.getLocalPos1();

                    VSAttachmentConstraint newAttachConstraint = new VSAttachmentConstraint(
                            shiptraptionId,
                            otherShipId,
                            0,
                            localPos1,
                            localPos2,
                            10E10,
                            0
                    );


                    Integer attachId = VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).createNewConstraint(newAttachConstraint);


                    VSHingeOrientationConstraint hingeConstraint = blockData.getHingeConstraint();

                    Quaterniond localOrientation = (Quaterniond) hingeConstraint.getLocalRot0();

                    VSHingeOrientationConstraint newHingeConstraint = new VSHingeOrientationConstraint(
                            shiptraptionId,
                            otherShipId,
                            0,
                            localOrientation,
                            localOrientation,
                            10E10
                    );


                    Integer hingeId = VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).createNewConstraint(newHingeConstraint);

                    if (hingeId != null) createdHingeConstraint = true;
                    if (attachId != null) createdAttachConstraint = true;

                    AestheticBearingData.AlternatorBearingUpdateData updateData = new AestheticBearingData.AlternatorBearingUpdateData(
                            attachConstraint,
                            attachId,
                            hingeConstraint,
                            hingeId
                    );
                    controller.updateInducer(updateData);
                    if (createdAttachConstraint && createdHingeConstraint) shouldRefresh = false;
                }
            }
        }


    }
    private void assemble() {

        if (!(level.getBlockState(this.worldPosition).getBlock() instanceof BearingBlock)) return;

        Direction direction = this.getBlockState().getValue(BearingBlock.FACING);
        BlockPos center = this.worldPosition.relative(direction);
        DenseBlockPosSet selection;

        try {
            GlueAssembler var35 = GlueAssembler.INSTANCE;
            selection = var35.collectGlued(level, center);
            this.lastException = null;
        } catch (Throwable var33) {
            if (var33 instanceof AssemblyException) {
                this.lastException = (AssemblyException) var33;
                this.sendData();
            }
            return;
        }

        if (selection == null) return;
        running = true;

        ServerLevel serverLevel = (ServerLevel) this.level;
        ServerShip shiptraption = ShipAssemblyKt.createNewShipWithBlocks(center, selection, serverLevel);

        this.shiptraptionId = shiptraption.getId();

        if (level.isClientSide) return;
        Ship shipOn = VSGameUtilsKt.getShipManagingPos(level,worldPosition);

        Map<String, Long> dimensionToPhysBody = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getDimensionToGroundBodyIdImmutable();
        long otherShipId = dimensionToPhysBody.get(VSGameUtilsKt.getDimensionId(level));

        Vector3d posInWorld = VectorConversionsMCKt.toJOMLD(worldPosition).add(.5,.5,.5);
        Vector3d normalInWorld = VectorConversionsMCKt.toJOMLD(getBlockState().getValue(BearingBlock.FACING).getNormal());
        Vector3d axis = new Vector3d(normalInWorld);
        Quaterniond rotationInWorld = new Quaterniond();
        int shipChunkX = shiptraption.getChunkClaim().getXMiddle();
        int shipChunkZ = shiptraption.getChunkClaim().getZMiddle();
        Vector3d centerInShip = new Vector3d((shipChunkX << 4) + (center.getX() & 15), center.getY(), (shipChunkZ << 4) + (center.getZ() & 15)).add(NumberUtil.blockPosOffset);
        Vector3d scaling = new Vector3d(1);
        Vector3d posInOwnerShip = posInWorld.add(normalInWorld, new Vector3d());
        posInOwnerShip.fma(-.5,axis);
        centerInShip.fma(-.5,axis);
        if (shipOn != null) {
            otherShipId = shipOn.getId();
            shipOn.getShipToWorld().transformPosition(posInWorld);
            shipOn.getTransform().getShipToWorldRotation().transform(normalInWorld);
//            shipOn.getTransform().getShipToWorldRotation().transform(weirdAssFix);
//            shipOn.getTransform().getShipToWorldRotation().transform(positiveX);
            scaling = (Vector3d) shipOn.getTransform().getShipToWorldScaling();
            rotationInWorld = (Quaterniond) shipOn.getTransform().getShipToWorldRotation();
        }
//        Vector3d blockPosOffset = rotationInWorld.transform(NumberUtil.blockPosOffset);
        Quaterniond localOrientation = new Vector3d(1,0,0).rotationTo(axis, new Quaterniond());
        ShipDataCommon shipDataCommon = (ShipDataCommon)shiptraption;
        shipDataCommon.setTransform(ShipTransformImpl.Companion.create(posInWorld.add(normalInWorld,new Vector3d()), shiptraption.getInertiaData().getCenterOfMassInShip(), rotationInWorld, scaling));



        VSAttachmentConstraint attachmentConstraint = new VSAttachmentConstraint(shiptraptionId,otherShipId,0,centerInShip,posInOwnerShip,10E10,0);
        Integer attachId = VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).createNewConstraint(attachmentConstraint);

        VSHingeOrientationConstraint hingeOrientationConstraint = new VSHingeOrientationConstraint(shiptraptionId,otherShipId,0,localOrientation, localOrientation,10E10);
        Integer hingeId = VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).createNewConstraint(hingeOrientationConstraint);

        if (attachId == null && hingeId == null) return;

        VSConstraintAndId attachConstraint = new VSConstraintAndId(attachId,attachmentConstraint);
        VSConstraintAndId hingeConstraint = new VSConstraintAndId(hingeId,hingeOrientationConstraint);

//        VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).removeConstraint(attachId);
        AestheticBearingController controller = AestheticBearingController.getOrCreate(shiptraption);
        controller.data = new AestheticBearingData(
                posInWorld,
                normalInWorld,
                shiptraptionId,
                attachConstraint,
                hingeConstraint
        );
        hasAssembled = true;
    }
    public void write(@NotNull CompoundTag compound, boolean clientPacket) {
        if(shiptraptionId !=-1L) {
            compound.putLong("ShiptraptionId", shiptraptionId);
            compound.putFloat("LastSpeed", lastSpeed);
        }
        compound.putBoolean("IsRunning",running);
        compound.putBoolean("HasAssembled", hasAssembled);
        super.write(compound,clientPacket);
    }
    public void read(@NotNull CompoundTag compound, boolean clientPacket) {
        if (this.wasMoved) super.read(compound, clientPacket);
        else{
            this.shiptraptionId = compound.getLong("ShiptraptionId");
            this.lastSpeed = compound.getLong("LastSpeed");
            this.running = compound.getBoolean("IsRunning");
            this.hasAssembled = compound.getBoolean("HasAssembled");
            this.lastException = AssemblyException.read(compound);
            this.shouldRefresh = true;
            super.read(compound,clientPacket);
        }
    }

    public final void destroy() {
        if (shiptraptionId == -1L || !hasAssembled || level.isClientSide) return;
        ServerShip ship = (ServerShip) getShiptraption();
        if (ship == null) return;
        AestheticBearingController controller = AestheticBearingController.getOrCreate(ship);
        AestheticBearingData blockData = controller.data;
        Integer attachId = blockData.getAttachId();
        Integer hingeId = blockData.getHingeId();
        if (attachId != null) VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).removeConstraint(attachId);
        if (hingeId != null) VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).removeConstraint(hingeId);
        controller.clearInducer();
    }

    public void lazyTick() {
        super.lazyTick();
        if (this.shiptraptionId != -1L) {
            Level var10000 = this.level;
            Objects.requireNonNull(var10000);
            if (!var10000.isClientSide) {
                this.sendData();
            }
        }

    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        return 0;
    }

    @Override
    public boolean isWoodenTop() {
        return false;
    }

    @Override
    public void setAngle(float forcedAngle) {

    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return false;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {

    }

    @Override
    public void onStall() {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public BlockPos getBlockPosition() {
        return worldPosition;
    }
}
