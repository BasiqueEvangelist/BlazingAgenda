package me.basiqueevangelist.artsandcrafts.client.gui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.pond.OwoScreenHandlerExtension;
import me.basiqueevangelist.artsandcrafts.client.DownloadedTexture;
import me.basiqueevangelist.artsandcrafts.screen.BarberStationScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BarberStationScreen extends BaseOwoHandledScreen<FlowLayout, BarberStationScreenHandler> {
    private static final Surface SURFACE = Surface.flat(0xFFCCCCCC).and(Surface.outline(0xFF5800FF));

    private final FlowLayout haircutsContainer = Containers.ltrTextFlow(Sizing.fill(100), Sizing.content());
    private final LabelComponent usedLabel = Components.label(Text.literal(""));

    private boolean canCreate = true;

    public BarberStationScreen(BarberStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        handler.uploadSucceeded = this::uploadSucceeded;
//        handler.uploadRejected = this::uploadRejected;

        handler.infoResponse = this::infoReceived;

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
            .surface(SURFACE)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .padding(Insets.of(5));

        mainLayout
            .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.translatable("text.artsandcrafts.barber_station_screen").formatted(Formatting.BLACK))
                    .margins(Insets.right(5)))
                .child(usedLabel)
                .margins(Insets.bottom(10)));

        haircutsContainer
            .gap(5);

        haircutsContainer
            .child(Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                .child(Components.label(Text.translatable("message.artsandcrafts.loading")))
                .horizontalAlignment(HorizontalAlignment.CENTER));

        mainLayout.child(Containers.verticalScroll(Sizing.content(), Sizing.fill(100), haircutsContainer));

        ((OwoScreenHandlerExtension) getScreenHandler()).owo$attachToPlayer(MinecraftClient.getInstance().player);
        getScreenHandler().sendMessage(new BarberStationScreenHandler.RequestInfo());
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public void filesDragged(List<Path> paths) {
        if (!canCreate) return;

        var path = paths.get(0);

        var flow = Containers.verticalFlow(Sizing.content(), Sizing.content());
        flow
            .gap(2)
            .padding(Insets.of(5))
            .surface(SURFACE)
            .horizontalAlignment(HorizontalAlignment.CENTER);

        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DownloadedTexture tx = new DownloadedTexture(data);

        flow.child(tx.toComponent()
            .preserveAspectRatio(true)
            .horizontalSizing(Sizing.fill(90)));

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
        getScreenHandler().sendMessage(new BarberStationScreenHandler.RequestInfo());
    }

    public void infoReceived(BarberStationScreenHandler.InfoResponse haircuts) {
        this.canCreate = haircuts.canCreate();

        haircutsContainer.<FlowLayout>configure(container -> {
            container.clearChildren();

            for (var haircut : haircuts.haircuts()) {
                var tx = new DownloadedTexture(haircut.data());

                var haircutFlow = Containers.verticalFlow(Sizing.fill(19), Sizing.fixed(100));
                haircutFlow
                    .gap(2)
                    .padding(Insets.of(5))
                    .surface(SURFACE)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .margins(Insets.bottom(5));

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
                    getScreenHandler().sendMessage(new BarberStationScreenHandler.RequestInfo());
                });

                haircutFlow.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.literal(haircut.name()).formatted(Formatting.BLACK))
                        .margins(Insets.right(5)))
                    .child(cross));

                container.child(haircutFlow);
            }

            if (haircuts.canCreate()) {
                var addFlow = Containers.verticalFlow(Sizing.fill(19), Sizing.fixed(100));

                addFlow
                    .gap(2)
                    .padding(Insets.of(5))
                    .surface(SURFACE)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .cursorStyle(CursorStyle.HAND);

                addFlow.child(Components.label(Text.translatable("text.artsandcrafts.drag_or_click_to_add").formatted(Formatting.BLACK))
                    .horizontalTextAlignment(HorizontalAlignment.CENTER)
                    .horizontalSizing(Sizing.fill(100))
                    .cursorStyle(CursorStyle.HAND));

                addFlow.mouseDown().subscribe((mouseX, mouseY, button) -> {
                    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                    UISounds.playButtonSound();

                    DialogUtil.openFileDialogAsync("Open haircut image", null, List.of("*.png", "*.jpg", "*.jpeg"), "Image files", false)
                        .thenAcceptAsync(imgPath -> {
                            if (imgPath != null) {
                                filesDragged(List.of(Path.of(imgPath)));
                            }
                        }, MinecraftClient.getInstance());

                    return true;
                });

                container.child(addFlow);
            }
        });

        int totalUsed = 0;

        for (var haircut : haircuts.haircuts()) {
            totalUsed += haircut.data().length;
        }

        usedLabel.text(Text.translatable(
                "text.artsandcrafts.total_used",
                toKiB(totalUsed),
                toKiB(haircuts.maxTotalSize()),
                haircuts.haircuts().size(),
                haircuts.maxTotalSlots()
            )
            .formatted(Formatting.BLACK));
    }

    private static double toKiB(int bytes) {
        double kib = (double)bytes / 1024;

        return (double) (int)(kib * 10) / 10;
    }
}
