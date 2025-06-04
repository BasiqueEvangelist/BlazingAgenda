package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Sizing;
import org.lwjgl.glfw.GLFW;

public class GreedyTextBoxComponent extends TextBoxComponent {
    public GreedyTextBoxComponent(Sizing horizontalSizing) {
        super(horizontalSizing);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean res = super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z)
            return true;

        return res;
    }
}
