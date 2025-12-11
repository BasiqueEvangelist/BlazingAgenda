
package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.client.DownloadedTexture;
import me.basiqueevangelist.blazingagenda.screen.FashionMagazineScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class FashionMagazineScreen extends BaseOwoHandledScreen<FlowLayout, FashionMagazineScreenHandler> {
    public FashionMagazineScreen(FashionMagazineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var main = GuiUtil.buildBookUi(rootComponent);

        for (var costume : handler.data.costumes()) {
            var costumeFlow = Containers.verticalFlow(Sizing.fill(), Sizing.content());

            costumeFlow.child(Components.label(Text.translatable("text.blazing-agenda.costumeNameDark", costume.name(), costume.ownerName()))
                .horizontalTextAlignment(HorizontalAlignment.CENTER));

            var tx = new DownloadedTexture(costume.data());

            var imgComponent = tx.toComponent();

            costumeFlow.child(imgComponent
                .preserveAspectRatio(true)
                .verticalSizing(Sizing.fixed(150)));

            costumeFlow.child(Components.button(Text.literal("Use"), unused -> {
                handler.sendMessage(new FashionMagazineScreenHandler.SetCostumeId(costume.id()));

                close();
            }));

            main.child(costumeFlow);
        }

        if (handler.data.costumes().isEmpty()) {
            main.child(Components.label(Text.translatable("text.blazing-agenda.no_costumes").formatted(Formatting.BLACK))
                .horizontalTextAlignment(HorizontalAlignment.CENTER));
        }
    }
}
