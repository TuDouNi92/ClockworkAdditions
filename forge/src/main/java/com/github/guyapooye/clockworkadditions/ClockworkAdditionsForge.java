package com.github.guyapooye.clockworkadditions;

import com.github.guyapooye.clockworkadditions.registries.ConfigRegistryImpl;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import static com.github.guyapooye.clockworkadditions.ClockworkAdditions.*;

@Mod(MOD_ID)
public class ClockworkAdditionsForge {
    private DeferredRegister<CreativeModeTab> TAB_REGISTER;
    public ClockworkAdditionsForge() {
        this.TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "clockworkadditions");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modEventBus);
        TAB_REGISTER.register("general", ClockworkAdditions::getCreativeModeTab);
        TAB_REGISTER.register(modEventBus);

        ClockworkAdditions.init();

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        ConfigRegistryImpl.register(modLoadingContext);
    }
}