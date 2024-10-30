package com.github.guyapooye.clockworkadditions.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;

public class WorldspaceUtil {

    public static Matrix4dc getShipToWorld(Level level, BlockPos pos) {
        VSGameUtilsKt.getShipManagingPos(level, pos);
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        return ship.getShipToWorld();
    }

    public static Matrix4dc getShipToWorldClient(Level level, BlockPos pos) {
        VSGameUtilsKt.getShipManagingPos(level, pos);
        ClientShip ship = (ClientShip) VSGameUtilsKt.getShipManagingPos(level, pos);

        if (ship == null) return new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        return ship.getRenderTransform().getShipToWorld();
    }

    public static Vector3d getWorldSpace(Level level, BlockPos pos) {
        return getShipToWorld(level, pos).transformPosition(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static Vector3d getWorldSpaceClient(Level level, BlockPos pos) {
        return getShipToWorldClient(level, pos).transformPosition(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static long getShipOrRigidBodyId(Level level, BlockPos pos) {
        Map<String, Long> dimensionToPhysBody = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getDimensionToGroundBodyIdImmutable();
        long worldPhysBodyId = dimensionToPhysBody.get(VSGameUtilsKt.getDimensionId(level));
        Ship nullableShip = VSGameUtilsKt.getShipManagingPos(level,pos);
        return nullableShip == null ? worldPhysBodyId : nullableShip.getId();
    }
}
