// Massively adapted from https://github.com/wisp-forest/lavender/blob/1.21/src/main/java/io/wispforest/lavender/client/LavenderBookScreen.java, which is under MIT.

package me.basiqueevangelist.blazingagenda.client.gui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.base.BaseUIModelHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.client.DownloadedTexture;
import me.basiqueevangelist.blazingagenda.screen.FashionMagazineScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FashionMagazineScreen extends BaseUIModelHandledScreen<FlowLayout, FashionMagazineScreenHandler> {
    private ButtonComponent previousButton;
    private ButtonComponent returnButton;
    private ButtonComponent nextButton;
    private TextBoxComponent searchBox;

    private FlowLayout leftPageAnchor;
    private FlowLayout rightPageAnchor;
    private FlowLayout bookmarkPanel;

    private int selectedPage = 0;

    public FashionMagazineScreen(FashionMagazineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, FlowLayout.class, BlazingAgenda.id("book"));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(this.template(Component.class, "primary-panel"));

        this.leftPageAnchor = this.component(FlowLayout.class, "left-page-anchor");
        this.rightPageAnchor = this.component(FlowLayout.class, "right-page-anchor");
        this.bookmarkPanel = this.component(FlowLayout.class, "bookmark-panel");

        (this.previousButton = this.component(ButtonComponent.class, "previous-button")).onPress(button -> this.turnPage(true));
        (this.nextButton = this.component(ButtonComponent.class, "next-button")).onPress(button -> this.turnPage(false));
        (this.returnButton = this.component(ButtonComponent.class, "back-button")).onPress(button -> {
//            if (Screen.hasShiftDown()) {
//                while (this.navStack.size() > 1) this.navStack.pop();
//                this.rebuildContent(this.book.flippingSound());
//            } else {
//                this.navPop();
//            }
        });

        this.searchBox = this.component(TextBoxComponent.class, "search-box");
        searchBox.visible = searchBox.active = false;
        searchBox.onChanged().subscribe(value -> {
//            var frame = this.currentNavFrame();
//
//            this.navStack.pop();
//            this.navPush(new NavFrame(frame.pageSupplier.replicator().apply(this), frame.selectedPage), true);
//
//            this.rebuildContent(null);
        });

//        var navTrail = getNavTrail(this.book);
//        for (int i = navTrail.size() - 1; i >= 0; i--) {
//            var frame = navTrail.get(i).createFrame(this);
//            if (frame == null) continue;
//
//            this.navPush(frame, true);
//        }
//
//        if (this.book.introEntry() != null && !LavenderClientStorage.wasBookOpened(this.book.id())) {
//            this.navPush(new NavFrame(new EntryPageSupplier(this, this.book.introEntry()), 0), true);
//        }

//        LavenderClientStorage.markBookOpened(this.book.id());
        this.rebuildContent();
    }

    private void rebuildContent() {
        if (selectedPage >= pageCount()) {
            selectedPage = (pageCount() - 1) / 2 * 2;
        }

//        this.returnButton.active(this.navStack.size() > 1);
        this.previousButton.active(selectedPage > 0);
        this.nextButton.active(selectedPage + 2 < pageCount());

//        searchBox.visible = searchBox.active = pageSupplier.searchable();

        int index = 0;
        while (index < 2) {
            var anchor = index == 0 ? this.leftPageAnchor : this.rightPageAnchor;
            anchor.clearChildren();

            if (selectedPage + index < pageCount()) {
                anchor.child(getPageContent(selectedPage + index));
            } else {
                anchor.child(this.template(Component.class, "empty-page-content"));
            }

            index++;
        }
    }

    private void turnPage(boolean left) {
        int previousPage = selectedPage;
        selectedPage = Math.max(0, Math.min(selectedPage + (left ? -2 : 2), pageCount() - 1)) / 2 * 2;

        if (selectedPage != previousPage) {
            this.rebuildContent();
        }
    }

    private int pageCount() {
        return handler.data.costumes().size();
    }

    private Component getPageContent(int index) {
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


    protected <C extends Component> C template(Class<C> expectedComponentClass, String name) {
        return this.template(expectedComponentClass, name, Map.of());
    }

    protected <C extends Component> C template(Class<C> expectedComponentClass, String name, Map<String, String> parameters) {
        return this.template(this.model, expectedComponentClass, name, parameters);
    }

    protected <C extends Component> C template(UIModel model, Class<C> expectedComponentClass, String name, Map<String, String> parameters) {
        var params = new HashMap<String, String>();
        params.put("book-texture", bookTexture().toString());
        params.putAll(parameters);

        return model.expandTemplate(expectedComponentClass, name, params);
    }

    protected Identifier bookTexture() {
        return BlazingAgenda.id("textures/gui/fashion_magazine.png");
    }

}
