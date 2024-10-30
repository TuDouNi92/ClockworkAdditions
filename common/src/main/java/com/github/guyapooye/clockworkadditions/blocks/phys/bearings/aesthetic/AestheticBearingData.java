package com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.guyapooye.clockworkadditions.forces.PhysData;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic.AestheticBearingData.AlternatorBearingUpdateData;
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint;

@Getter
@Setter
public class AestheticBearingData implements PhysData<AlternatorBearingUpdateData> {
    @Nullable
    private final Vector3dc bearingPosition;
    @Nullable
    private final Vector3dc bearingAxis;
    private final long shiptraptionId;
    @Nullable
    private VSAttachmentConstraint attachConstraint;
    @JsonIgnore
    @Nullable
    private Integer attachId;
    @Nullable
    private VSHingeOrientationConstraint hingeConstraint;
    @JsonIgnore
    @Nullable
    private Integer hingeId;
    /*Don't use. Only for jackson*/
    @Deprecated
    public AestheticBearingData() {

        this.bearingPosition = null;
        this.bearingAxis = null;
        this.shiptraptionId = -1L;
    }
    AestheticBearingData(@Nullable Vector3dc bearingPosition,
                                @Nullable Vector3dc bearingAxis,
                                long shiptraptionID,
                                VSConstraintAndId attachConstraint,
                                VSConstraintAndId hingeConstraint
    ) {
        this.bearingPosition = bearingPosition;
        this.bearingAxis = bearingAxis;
        this.shiptraptionId = shiptraptionID;
        this.attachConstraint = (VSAttachmentConstraint) attachConstraint.getVsConstraint();
        this.attachId = attachConstraint.getConstraintId();
        this.hingeConstraint = (VSHingeOrientationConstraint) hingeConstraint.getVsConstraint();

    }
    public final void updateWith(AlternatorBearingUpdateData updateData) {
        attachConstraint = updateData.attachConstraint;
        attachId = updateData.attachId;
        hingeConstraint = updateData.hingeConstraint;
        hingeId = updateData.hingeId;
    }


    public record AlternatorBearingUpdateData(VSAttachmentConstraint attachConstraint,
                                              Integer attachId,
                                              VSHingeOrientationConstraint hingeConstraint,
                                              Integer hingeId){}
}
