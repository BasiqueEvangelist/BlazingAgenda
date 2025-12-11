package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import me.basiqueevangelist.blazingagenda.BlazingAgendaUtil;
import me.basiqueevangelist.blazingagenda.client.DownloadedTexture;
import me.basiqueevangelist.blazingagenda.client.NotificationToast;
import me.basiqueevangelist.blazingagenda.screen.FashionScrapbookScreenHandler;
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

public class FashionScrapbookScreen extends BaseOwoHandledScreen<FlowLayout, FashionScrapbookScreenHandler> {
    private FlowLayout main;
    private FlowLayout addFlow;

    private FashionScrapbookScreenHandler.CostumeEntry mouseOverCostume = null;

    public FashionScrapbookScreen(FashionScrapbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        handler.uploadSucceeded = this::uploadSucceeded;
        handler.uploadRejected = this::uploadRejected;

        handler.refreshHandler = this::rebuildContent;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.main = GuiUtil.buildBookUi(rootComponent);

        rebuildContent();
    }

    private void rebuildContent() {
        this.main.configure(unused1 -> {
            main.clearChildren();

            this.addFlow = Containers.verticalFlow(Sizing.fill(), Sizing.fixed(50));

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

            main.child(this.addFlow);

            for (var costume : handler.data.costumes()) {
                var costumeFlow = Containers.verticalFlow(Sizing.fill(), Sizing.content());

                costumeFlow.mouseEnter().subscribe(() -> mouseOverCostume = costume);

                costumeFlow.mouseLeave().subscribe(() -> {
                    if (mouseOverCostume == costume)
                        mouseOverCostume = null;
                });

                costumeFlow.child(Components.label(Text.translatable("text.blazing-agenda.costumeNameDark", costume.name(), costume.ownerName()))
                    .horizontalTextAlignment(HorizontalAlignment.CENTER));

                var tx = new DownloadedTexture(costume.data());

                var imgComponent = tx.toComponent();

                costumeFlow.child(imgComponent
                    .preserveAspectRatio(true)
                    .verticalSizing(Sizing.fixed(150)));

                FlowLayout buttonRow = Containers.horizontalFlow(Sizing.content(), Sizing.content());

                buttonRow
                    .gap(5)
                    .horizontalAlignment(HorizontalAlignment.CENTER);
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

                main.child(costumeFlow);
            }
        });
    }

    @Override
    public void filesDragged(List<Path> paths) {
        var path = paths.get(0);

        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!BlazingAgendaUtil.looksLikePng(data)) return;

        if (mouseOverCostume != null) {
            handler.sendMessage(new FashionScrapbookScreenHandler.UpdateCostume(mouseOverCostume.id(), data));
            return;
        }

        addFlow.clearChildren();
        addFlow.verticalSizing(Sizing.content());

        DownloadedTexture tx = new DownloadedTexture(data);

        addFlow.child(tx.toComponent()
            .preserveAspectRatio(true)
            .verticalSizing(Sizing.fixed(150)));

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

    public void uploadSucceeded(FashionScrapbookScreenHandler.UploadSucceeded packet) {
    }

    public void uploadRejected(FashionScrapbookScreenHandler.UploadRejected packet) {
        var toast = new NotificationToast(packet.errorMessage(), null);
        toast.register();
    }
}
