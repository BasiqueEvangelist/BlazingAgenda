package me.basiqueevangelist.artsandcrafts.client;

import io.wispforest.owo.ui.component.TextureComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.util.Locale;
import java.util.UUID;

public class DownloadedTexture implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();
    private static final Logger LOGGER = LoggerFactory.getLogger("ArtsAndCrafts/DownloadedTexture");

    private final Identifier id;
    private final NativeImageBackedTexture tex;
    private final Cleaner.Cleanable cleanable;

    public DownloadedTexture(byte[] data) {
        this.id = new Identifier("artsandcrafts", "downloaded/" + UUID.randomUUID().toString().toLowerCase(Locale.ROOT));

        var imgBuf = MemoryUtil.memAlloc(data.length);
        try {
            imgBuf.put(data);
            imgBuf.rewind();

            this.tex = new NativeImageBackedTexture(NativeImage.read(imgBuf));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            MemoryUtil.memFree(imgBuf);
        }

        MinecraftClient.getInstance().getTextureManager().registerTexture(id, tex);
        cleanable = CLEANER.register(this, new Clean(id));
    }

    public Identifier id() {
        return id;
    }

    public NativeImage image() {
        return tex.getImage();
    }

    public TxComponent toComponent() {
        return new TxComponent();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
        cleanable.clean();
    }

    private record Clean(Identifier id) implements Runnable {
        @Override
        public void run() {
            TextureManager manager = MinecraftClient.getInstance().getTextureManager();

            if (manager.getOrDefault(id, null) == null) return;

            // TODO: readd and fix this
//            LOGGER.warn("Texture {} wasn't closed at GC", id);
            manager.destroyTexture(id);
        }
    }

    public class TxComponent extends TextureComponent {
        private boolean preserveAspectRatio;

        protected TxComponent() {
            super(
                DownloadedTexture.this.id,
                0,
                0,
                DownloadedTexture.this.image().getWidth(),
                DownloadedTexture.this.image().getHeight(),
                DownloadedTexture.this.image().getWidth(),
                DownloadedTexture.this.image().getHeight()
            );
        }

        @Override
        protected void applySizing() {
            if (!preserveAspectRatio) {
                super.applySizing();
                return;
            }

            var hSize = horizontalSizing.get();
            var vSize = verticalSizing.get();
            var margins = this.margins.get();

            int hAvail = this.space.width() - margins.horizontal();
            int vAvail = this.space.height() - margins.vertical();

            if (vSize.isContent() && !hSize.isContent()) {
                this.width = hSize.inflate(hAvail, this::determineHorizontalContentSize);
                this.height = width * image().getHeight() / image().getWidth();

                if (height > vAvail) {
                    this.height = vAvail;
                    this.width = height * image().getWidth() / image().getHeight();
                }
            }

            if (!vSize.isContent() && hSize.isContent()) {
                this.height = vSize.inflate(vAvail, this::determineVerticalContentSize);
                this.width = height * image().getWidth() / image().getHeight();

                if (width > hAvail) {
                    this.width = hAvail;
                    this.height = width * image().getHeight() / image().getWidth();
                }
            }
        }

        public boolean preserveAspectRatio() {
            return preserveAspectRatio;
        }

        public TxComponent preserveAspectRatio(boolean preserveAspectRatio) {
            this.preserveAspectRatio = preserveAspectRatio;
            return this;
        }
    }
}
