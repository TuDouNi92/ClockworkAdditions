package com.github.guyapooye.clockworkadditions.blocks.kinetics.cvjoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.guyapooye.clockworkadditions.forces.PhysData;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;

public class CVJointPhysData implements PhysData<CVJointPhysData.UpdateData> {

    @Nullable
    private VSRopeConstraint ropeConstraint;
    @JsonIgnore
    @Nullable
    private Integer ropeId;

    /*Don't use. Only for jackson*/
    @Deprecated
    public CVJointPhysData() {}
    public CVJointPhysData(VSConstraintAndId ropeConstraint) {
        this.ropeConstraint = (VSRopeConstraint) ropeConstraint.getVsConstraint();
        this.ropeId = ropeConstraint.getConstraintId();
    }

    @Override
    public void updateWith(UpdateData updateData) {
        ropeConstraint = updateData.ropeConstraint;
        ropeId = updateData.ropeId;
    }

    public record CreateData(VSConstraintAndId ropeConstraint) {}
    public record UpdateData(VSRopeConstraint ropeConstraint, Integer ropeId) {}
}
