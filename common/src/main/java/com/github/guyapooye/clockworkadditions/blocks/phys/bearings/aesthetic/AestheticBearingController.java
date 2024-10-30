package com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic;

import com.github.guyapooye.clockworkadditions.forces.AbstractSingletonPhysController;
import org.valkyrienskies.core.api.ships.ServerShip;
import com.github.guyapooye.clockworkadditions.blocks.phys.bearings.aesthetic.AestheticBearingData.AlternatorBearingUpdateData;


public class AestheticBearingController extends AbstractSingletonPhysController<AestheticBearingData,AlternatorBearingUpdateData> {

    public static AestheticBearingController getOrCreate(ServerShip ship) {
        AestheticBearingController attachment = ship.getAttachment(AestheticBearingController.class);
        if (attachment == null) {
            attachment = new AestheticBearingController();
            ship.saveAttachment(AestheticBearingController.class,attachment);
        }
        return attachment;
    }

}
