package com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint;

import com.github.guyapooye.clockworkadditions.forces.AbstractMultiPhysController;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint.CVJointPhysData.CreateData;
import com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint.CVJointPhysData.UpdateData;
import org.valkyrienskies.core.api.ships.ServerShip;

public class CVJointPhysStorage extends AbstractMultiPhysController<CVJointPhysData,UpdateData,CreateData> {
    @Override
    public CVJointPhysData fromCreateData(CreateData createData) {
        return new CVJointPhysData(createData.ropeConstraint());
    }
    protected static CVJointPhysStorage getOrCreate(ServerShip ship) {
         CVJointPhysStorage instance = ship.getAttachment(CVJointPhysStorage.class);
         if (instance == null) {
             instance = new CVJointPhysStorage();
             ship.saveAttachment(CVJointPhysStorage.class, instance);
         }
         return instance;
    }
}
