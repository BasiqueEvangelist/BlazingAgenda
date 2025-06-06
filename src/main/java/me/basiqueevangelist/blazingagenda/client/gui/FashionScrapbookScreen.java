package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.util.UISounds;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.client.DownloadedTexture;
import me.basiqueevangelist.blazingagenda.client.NotificationToast;
import me.basiqueevangelist.blazingagenda.screen.FashionScrapbookScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FashionScrapbookScreen extends BookScreen<FashionScrapbookScreenHandler> {
    private FlowLayout addFlow;

    public FashionScrapbookScreen(FashionScrapbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        handler.uploadSucceeded = this::uploadSucceeded;
        handler.uploadRejected = this::uploadRejected;

        handler.refreshHandler = this::rebuildContent;
    }

    @Override
    protected int pageCount() {
        return handler.data.costumes().size() + 1;
    }

    @Override
    protected void rebuildContent() {
        this.addFlow = null;

        super.rebuildContent();
    }

    @Override
    protected Component getPageContent(int index) {
        if (index == pageCount() - 1) {
            this.addFlow = Containers.verticalFlow(Sizing.fill(), Sizing.fill());

            var textContainer = Containers.verticalFlow(Sizing.fill(), Sizing.fill());

            textContainer.child(Components.label(Text.translatable("text.blazing-agenda.drag_or_click_to_add")
                .formatted(Formatting.BLACK))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .verticalTextAlignment(VerticalAlignment.CENTER)
                .horizontalSizing(Sizing.fill()))
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER);

            textContainer.mouseDown().subscribe((mouseX, mouseY, button) -> {
                if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                UISounds.playButtonSound();

                DialogUtil.openFileDialogAsync("Open costume image", null, List.of("*.png", "*.jpg", "*.jpeg"), "Image files", false)
                    .thenAcceptAsync(imgPath -> {
                        if (imgPath != null) {
                            filesDragged(List.of(Path.of(imgPath)));
                        }
                    }, MinecraftClient.getInstance());

                return true;
            });

            this.addFlow.child(textContainer);

            return this.addFlow;
        } else {
            var costume = handler.data.costumes().get(index);

            var costumeFlow = Containers.verticalFlow(Sizing.fill(), Sizing.fill());

            costumeFlow.child(Components.label(Text.translatable("text.blazing-agenda.costumeNameDark", costume.name(), costume.ownerName()))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .horizontalSizing(Sizing.fill()));

            var tx = new DownloadedTexture(costume.data());

            var imgComponent = tx.toComponent();

            costumeFlow.child(imgComponent
                .preserveAspectRatio(true)
                .verticalSizing(Sizing.fill()));

            costumeFlow.child(Components.spacer());

            FlowLayout buttonRow = Containers.horizontalFlow(Sizing.fill(), Sizing.content());

            buttonRow.horizontalAlignment(HorizontalAlignment.CENTER);
            costumeFlow.child(buttonRow);

            if (costume.canDelete()) {
                buttonRow.child(Components.button(Text.literal("Delete"), unused -> {
                    handler.sendMessage(new FashionScrapbookScreenHandler.DeleteCostume(costume.id()));
                }));
            }

            buttonRow.child(Components.button(Text.literal("Update"), unused -> {
                DialogUtil.openFileDialogAsync("Open costume image", null, List.of("*.png", "*.jpg", "*.jpeg"), "Image files", false)
                    .thenAcceptAsync(imgPath -> {
                        if (imgPath != null) {
                            byte[] data;
                            try {
                                data = Files.readAllBytes(Path.of(imgPath));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            handler.sendMessage(new FashionScrapbookScreenHandler.UpdateCostume(costume.id(), data));
                        }
                    }, MinecraftClient.getInstance());
            }));

            return costumeFlow;
        }
    }

    @Override
    public void filesDragged(List<Path> paths) {
        if (!addFlow.hasParent()) return;

        addFlow.clearChildren();

        var path = paths.get(0);

        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DownloadedTexture tx = new DownloadedTexture(data);

        addFlow.child(tx.toComponent()
            .preserveAspectRatio(true)
            .verticalSizing(Sizing.fill(75)));

        TextBoxComponent nameBox;
        addFlow.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
            .child(Components.label(Text.literal("Name: ").formatted(Formatting.BLACK)))
            .child(nameBox = new GreedyTextBoxComponent(Sizing.fixed(200)))
            .verticalAlignment(VerticalAlignment.CENTER));

        nameBox.setMaxLength(100);

        ButtonComponent button = Components.button(Text.literal("Submit"), bruh -> {
            String name = nameBox.getText();

            getScreenHandler().sendMessage(new FashionScrapbookScreenHandler.UploadCostume(name, data));
        });

        addFlow.child(button);
    }

    @Override
    protected Identifier bookTexture() {
        // TODO: use different texture
        return BlazingAgenda.id("textures/gui/fashion_magazine.png");
    }

    public void uploadSucceeded(FashionScrapbookScreenHandler.UploadSucceeded packet) {
    }

    public void uploadRejected(FashionScrapbookScreenHandler.UploadRejected packet) {
        var toast = new NotificationToast(packet.errorMessage(), null);
        toast.register();
    }
}
