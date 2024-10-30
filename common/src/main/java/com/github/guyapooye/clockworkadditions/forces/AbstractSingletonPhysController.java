package com.github.guyapooye.clockworkadditions.forces;

import kotlin.jvm.functions.Function1;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

@Getter
@SuppressWarnings("deprecation")
public abstract class AbstractSingletonPhysController<D extends PhysData<U>,U> implements ShipForcesInducer {
    public D data;
    @Override
    public void applyForces(@NotNull PhysShip physShip) {}
    @Override
    public void applyForcesAndLookupPhysShips(@NotNull PhysShip physShip, @NotNull Function1<? super Long, ? extends PhysShip> lookupPhysShip) {}

    public void clearInducer() {
        data = null;
    }
    public void updateInducer(U updateData) {
        if (this.data == null ) return;
        this.data.updateWith(updateData);
    }
}
