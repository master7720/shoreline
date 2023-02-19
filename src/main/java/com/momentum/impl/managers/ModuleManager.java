package com.momentum.impl.managers;

import com.momentum.Momentum;
import com.momentum.api.event.Listener;
import com.momentum.api.module.Module;
import com.momentum.impl.events.vanilla.KeyInputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Manages module functionality
 *
 * @author linus
 * @since 02/11/2023
 */
public class ModuleManager {

    /**
     * Manages module functionality
     */
    public ModuleManager() {

        // keybind impl
        Momentum.EVENT_BUS.subscribe(new Listener<KeyInputEvent>() {

            @Override
            public void invoke(KeyInputEvent event) {

                // check all modules for toggle
                for (Module m : Momentum.MODULE_REGISTRY.getModules()) {

                    // check if the module is toggle-able
                    // check if module's bind key is pressed
                    int bind = m.getBind();
                    if (Keyboard.isKeyDown(bind) && !Keyboard.isKeyDown(Keyboard.KEY_NONE)) {

                        // toggle the feature
                        m.toggle();
                    }
                }
            }
        });
    }
}