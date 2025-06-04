
package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.client.DownloadedTexture;
import me.basiqueevangelist.blazingagenda.screen.FashionMagazineScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FashionMagazineScreen extends BookScreen<FashionMagazineScreenHandler> {
    public FashionMagazineScreen(FashionMagazineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected int pageCount() {
        return handler.data.costumes().size();
    }

    @Override
    protected Component getPageContent(int index) {
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

        costumeFlow.child(Components.button(Text.literal("Use"), unused -> {
            handler.sendMessage(new FashionMagazineScreenHandler.SetCostumeId(costume.id()));

            close();
        }));


        return costumeFlow;
    }

    @Override
    protected Identifier bookTexture() {
        return BlazingAgenda.id("textures/gui/fashion_magazine.png");
    }
}
