package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class GuiUtil {
    public static void hoverBlue(LabelComponent label) {
        label.mouseEnter().subscribe(
            () -> label.text(((MutableText) label.text()).formatted(Formatting.DARK_RED)));

        label.mouseLeave().subscribe(
            () -> label.text(((MutableText) label.text()).formatted(Formatting.RED)));
    }

    public static void semiButton(LabelComponent label, Runnable onPressed) {
        hoverBlue(label);
        label.cursorStyle(CursorStyle.HAND);

        label.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

            UISounds.playButtonSound();
            onPressed.run();

            return true;
        });
    }

    public static FlowLayout buildBookUi(FlowLayout root) {
        FlowLayout outer = Containers.verticalFlow(Sizing.fill(60), Sizing.fill(80));

        root
            .child(outer)
            .verticalAlignment(VerticalAlignment.CENTER)
            .horizontalAlignment(HorizontalAlignment.CENTER);

        outer
            .surface(Surface.flat(0xFFCCCCCC).and(Surface.outline(0xFF5800FF)))
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .padding(Insets.of(5));

        FlowLayout main = Containers.verticalFlow(Sizing.fill(), Sizing.content());

        outer.child(Containers.verticalScroll(Sizing.fill(), Sizing.fill(), main));

        return main;
    }
}
