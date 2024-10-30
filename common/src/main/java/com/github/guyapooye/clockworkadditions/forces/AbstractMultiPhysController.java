package com.github.guyapooye.clockworkadditions.forces;

import com.mojang.datafixers.util.Pair;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("deprecation")
public abstract class AbstractMultiPhysController<D extends PhysData<U>,U,C> implements ShipForcesInducer {
    private final Map<Integer, D> inducerData = new HashMap<>();
    private final ConcurrentHashMap<Integer, U> updatedData = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Pair<Integer,C>> createdData = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> removedData = new ConcurrentLinkedQueue<>();
    private int nextId;
    @Override
    public final void applyForces(@NotNull PhysShip physShip) {}
    @Override
    public void applyForcesAndLookupPhysShips(@NotNull PhysShip physShip, @NotNull Function1<? super Long, ? extends PhysShip> lookupPhysShip) {
        //Create inducer
        for (Pair<Integer, C> data: createdData) addInducer(data.getFirst(), fromCreateData(data.getSecond()));
        //Remove inducer
        for (int id : removedData) removeInducerInternal(id);
        //Update inducer
        updatedData.forEach(this::updateInducerInternal);
        //Apply  inducer
        inducerData.forEach((id, data) -> applyIndividually(data,physShip,lookupPhysShip));
    }
    public void applyIndividually(D data, PhysShip physShip, Function1<? super Long, ? extends PhysShip> lookupPhysShip) {}
    private void addInducer(int id, D data) {
        inducerData.put(id, data);
    }

    private void removeInducerInternal(int id) {
        inducerData.remove(id);
    }
    public abstract D fromCreateData(C createData);

    private void updateInducerInternal(int id ,U update) {
        D data = inducerData.get(id);
        if (data == null ) return;
        data.updateWith(update);
    }
    public int addInducer(@NotNull C data) {
        int id;
        createdData.add(new Pair<>((id = nextId), data));
        return id;
    }
    public void updateInducer(int id, @NotNull U data) {
        updatedData.put(id, data);
    }
    public void removeData(int id) {
        removedData.add(id);
    }
}
