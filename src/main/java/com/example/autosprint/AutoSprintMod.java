package com.example.autosprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod("autosprint")
@EventBusSubscriber(modid = "autosprint", value = Dist.CLIENT)
public class AutoSprintMod {
    private static boolean enabled = true;
    public static KeyMapping toggleKey;

    public AutoSprintMod(IEventBus modEventBus) {
        modEventBus.addListener(this::registerKeys);
    }

    public void registerKeys(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping("key.autosprint.toggle", GLFW.GLFW_KEY_I, "key.categories.autosprint");
        event.register(toggleKey);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        if (toggleKey != null) {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;
                player.displayClientMessage(Component.literal("§7[AutoSprint] §fСтатус: " + (enabled ? "§aВКЛ" : "§cВЫКЛ")), true);
            }
        }

        if (!enabled) return;

        // Легитный сброс спринта во время прыжка на крит (когда летим вниз)
        boolean isFallingForCrit = !player.onGround() && player.getDeltaMovement().y < 0;
        boolean isMovingForward = player.input.hasForwardImpulse();

        if (isMovingForward && !isFallingForCrit && !player.isShiftKeyDown() && !player.horizontalCollision) {
            player.setSprinting(true);
            mc.options.keySprint.setDown(true);
        } else {
            mc.options.keySprint.setDown(false);
        }
    }
}
