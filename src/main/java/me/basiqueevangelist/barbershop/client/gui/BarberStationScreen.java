package me.basiqueevangelist.barbershop.client.gui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.pond.OwoScreenHandlerExtension;
import me.basiqueevangelist.barbershop.client.DownloadedTexture;
import me.basiqueevangelist.barbershop.screen.BarberStationScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BarberStationScreen extends BaseOwoHandledScreen<FlowLayout, BarberStationScreenHandler> {
    private final FlowLayout haircutsContainer = Containers.ltrTextFlow(Sizing.fill(100), Sizing.content());

    public BarberStationScreen(BarberStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        handler.uploadSucceeded = this::uploadSucceeded;
//        handler.uploadRejected = this::uploadRejected;

        handler.haircutList = this::haircutListReceived;

        this.playerInventoryTitleY = -10000;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fill(60), Sizing.fill(80));

        rootComponent
            .child(mainLayout)
            .verticalAlignment(VerticalAlignment.CENTER)
            .horizontalAlignment(HorizontalAlignment.CENTER);

        mainLayout
            .surface(Surface.PANEL)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .padding(Insets.of(5));

        mainLayout
            .child(Components.label(Text.translatable("text.thebarbershop.barber_station_screen").formatted(Formatting.BLACK))
                .margins(Insets.bottom(10)));

        haircutsContainer
            .gap(5);

        haircutsContainer
            .child(Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                .child(Components.label(Text.translatable("message.thebarbershop.loading")))
                .horizontalAlignment(HorizontalAlignment.CENTER));

        mainLayout.child(Containers.verticalScroll(Sizing.content(), Sizing.fill(100), haircutsContainer));

        ((OwoScreenHandlerExtension) getScreenHandler()).owo$attachToPlayer(MinecraftClient.getInstance().player);
        getScreenHandler().sendMessage(new BarberStationScreenHandler.ListHaircuts());
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public void filesDragged(List<Path> paths) {
        var path = paths.get(0);

        var flow = Containers.verticalFlow(Sizing.content(), Sizing.content());
        flow
            .gap(2)
            .padding(Insets.of(5))
            .surface(Surface.PANEL)
            .horizontalAlignment(HorizontalAlignment.CENTER);

        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DownloadedTexture tx = new DownloadedTexture(data);

        flow.child(tx.toComponent());

        TextBoxComponent nameBox;
        flow.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
            .child(Components.label(Text.literal("Name: ").formatted(Formatting.BLACK)))
            .child(nameBox = Components.textBox(Sizing.fixed(200)))
            .verticalAlignment(VerticalAlignment.CENTER));

        nameBox.setMaxLength(100);

        ButtonComponent button = Components.button(Text.literal("Submit"), bruh -> {
            String name = nameBox.getText();

            getScreenHandler().sendMessage(new BarberStationScreenHandler.UploadHaircut(name, data));
            flow.parent().remove();

        });

        flow.child(button);

        this.uiAdapter.rootComponent.child(Containers.overlay(flow)
            .zIndex(1000));
    }

    public void uploadSucceeded(BarberStationScreenHandler.UploadSucceeded packet) {
        getScreenHandler().sendMessage(new BarberStationScreenHandler.ListHaircuts());
    }

    public void haircutListReceived(BarberStationScreenHandler.HaircutList haircuts) {
        haircutsContainer.<FlowLayout>configure(container -> {
            container.clearChildren();

            for (var haircut : haircuts.haircuts()) {
                var tx = new DownloadedTexture(haircut.data());

                var haircutFlow = Containers.verticalFlow(Sizing.fill(19), Sizing.fixed(100));
                haircutFlow
                    .gap(2)
                    .padding(Insets.of(5))
                    .surface(Surface.PANEL)
                    .horizontalAlignment(HorizontalAlignment.CENTER);

                var imgComponent = tx.toComponent();
                imgComponent
                    .preserveAspectRatio(true)
                    .cursorStyle(CursorStyle.HAND)
                    .horizontalSizing(Sizing.fill(100));

                imgComponent.mouseDown().subscribe((mouseX, mouseY, button) -> {
                    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                    UISounds.playButtonSound();

                    getScreenHandler().sendMessage(new BarberStationScreenHandler.ExchangeHaircut(haircut.id(), 1));

                    return true;
                });

                haircutFlow.child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(85))
                    .child(imgComponent));

                var cross = Components.label(Text.literal("❌")
                    .formatted(Formatting.RED));

                GuiUtil.semiButton(cross, () -> {
                    haircutFlow.remove();
                    getScreenHandler().sendMessage(new BarberStationScreenHandler.DeleteHaircut(haircut.id()));
                });

                haircutFlow.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.literal(haircut.name()).formatted(Formatting.BLACK))
                        .margins(Insets.right(5)))
                    .child(cross));

                container.child(haircutFlow);
            }

            var addFlow = Containers.verticalFlow(Sizing.fill(19), Sizing.fixed(100));

            addFlow
                .gap(2)
                .padding(Insets.of(5))
                .surface(Surface.PANEL)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

            addFlow.child(Components.label(Text.translatable("text.thebarbershop.drag_or_click_to_add").formatted(Formatting.BLACK))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .horizontalSizing(Sizing.fill(100)));

            addFlow.mouseDown().subscribe((mouseX, mouseY, button) -> {
                if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                UISounds.playButtonSound();

                var imgPath = DialogUtil.openFileDialog("Open haircut image", null, List.of("*.png", "*.jpg", "*.jpeg"), "Image files", false);

                if (imgPath != null) {
                    filesDragged(List.of(Path.of(imgPath)));
                }

                return true;
            });

            container.child(addFlow);
        });
    }
}
