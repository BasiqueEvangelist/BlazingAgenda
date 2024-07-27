package me.basiqueevangelist.blazingagenda.client.gui;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class DialogUtil {
    private DialogUtil() {

    }

    public static CompletableFuture<String> openFileDialogAsync(String title, @Nullable String defaultPath, @Nullable List<String> patterns, @Nullable String filterDesc, boolean allowMultipleSelections) {
        CompletableFuture<String> result = new CompletableFuture<>();

        Thread taskThread = new Thread(() -> {
            try {
                result.complete(openFileDialog(title, defaultPath, patterns, filterDesc, allowMultipleSelections));
            } catch (Throwable t) {
                result.completeExceptionally(t);
            }
        }, "File dialog thread");
        taskThread.start();

        return result;
    }

    public static String openFileDialog(String title, @Nullable String defaultPath, @Nullable List<String> patterns, @Nullable String filterDesc, boolean allowMultipleSelections) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer patternsBuf = null;

            if (patterns != null) {
                patternsBuf = stack.mallocPointer(patterns.size());

                for (int i = 0; i < patterns.size(); i++) {
                    stack.nUTF8Safe(patterns.get(i), true);
                    patternsBuf.put(i, stack.getPointerAddress());
                }
            }

            return TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, patternsBuf, filterDesc, allowMultipleSelections);
        }
    }

    public static String saveFileDialog(String title, @Nullable String defaultPath, @Nullable List<String> patterns, @Nullable String filterDesc) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer patternsBuf = null;

            if (patterns != null) {
                patternsBuf = stack.mallocPointer(patterns.size());

                for (int i = 0; i < patterns.size(); i++) {
                    stack.nUTF8Safe(patterns.get(i), true);
                    patternsBuf.put(i, stack.getPointerAddress());
                }
            }

            return TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath, patternsBuf, filterDesc);
        }
    }
}