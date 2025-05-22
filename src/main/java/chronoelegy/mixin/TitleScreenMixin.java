package chronoelegy.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow @Final private static Text COPYRIGHT;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    /**
     * @author ItsFelix5
     * @reason mod?
     */
    @Overwrite
    public void init() {
        assert this.client != null;

        this.client.createIntegratedServerLoader().start("Map", () -> this.client.setScreen(this));
        if(true) return;

        boolean mutableGameExists = false;
        try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("World")) {
            mutableGameExists = session.levelDatExists();
        } catch (IOException e) {
            SystemToast.addWorldAccessFailureToast(this.client, "World");
        }
        final boolean gameExists = mutableGameExists;

        int copyrightWidth = this.textRenderer.getWidth(COPYRIGHT);
        int y = this.height / 4 + 48;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("menu.game.new"), button -> {
                    if(gameExists) this.client.setScreen(new ConfirmScreen(
                            delete -> {
                                if(delete) {
                                    try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("World")) {
                                        session.deleteSessionLock();
                                    } catch (IOException e) {
                                        SystemToast.addWorldAccessFailureToast(this.client, "World");
                                    }
                                    createWorld();
                                }else client.setScreen(this);
                            },
                            Text.translatable("menu.resetTitle"),
                            Text.translatable("menu.resetWarning"),
                            Text.translatable("controls.reset"),
                            ScreenTexts.CANCEL
                    ));
                    else createWorld();
                }).dimensions(this.width / 2 - 100, y, 200, 20).build()
        );
        y += 24;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("menu.game.continue"), button -> this.client.createIntegratedServerLoader().start("World", () -> this.client.setScreen(this)))
                        .dimensions(this.width / 2 - 100, y, 200, 20)
                        .build()
        ).active = gameExists;
        y += 24;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("menu.game.edit"), button -> this.client.createIntegratedServerLoader().start("Map", () -> this.client.setScreen(this)))
                        .dimensions(this.width / 2 - 100, y, 200, 20)
                        .build()
        );
        y += 24;
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("GitHub"), button -> Util.getOperatingSystem().open("TODO"))
                        .dimensions(this.width / 2 - 100, y, 98, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Modrinth"), button -> Util.getOperatingSystem().open("TODO")).dimensions(this.width / 2 + 2, y, 98, 20).build()
        );
        y += 24;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options)))
                        .dimensions(this.width / 2 - 100, y, 98, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("menu.quit"), button -> this.client.scheduleStop()).dimensions(this.width / 2 + 2, y, 98, 20).build()
        );
        this.addDrawableChild(
                new PressableTextWidget(this.width - copyrightWidth - 2, this.height - 10, copyrightWidth, 10, COPYRIGHT, button -> this.client.setScreen(new CreditsAndAttributionScreen(this)), this.textRenderer)
        );
    }

    @Unique
    private void createWorld() {
        assert this.client != null;

        Path map = client.getLevelStorage().resolve("Map");
        Path world = client.getLevelStorage().resolve("World");

        try {
            FileUtils.deleteDirectory(world.toFile());

            for (Path src : new Path[]{map.resolve("data"), map.resolve("entities"), map.resolve("region")})
                try (Stream<Path> stream = Files.walk(src)) {
                    stream.forEach(toCopy -> {
                        try {
                            Path target = world.resolve(map.relativize(toCopy));
                            target.getParent().toFile().mkdirs();
                            Files.copy(toCopy, target);
                        } catch (FileAlreadyExistsException ignored) {
                        } catch (IOException e) {
                            e.printStackTrace();
                            SystemToast.addWorldAccessFailureToast(this.client, "Map");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    SystemToast.addWorldAccessFailureToast(this.client, "Map");
                }

            Files.copy(map.getParent().resolve("level.dat"), world.resolve("level.dat"));

            this.client.createIntegratedServerLoader().start("World", () -> this.client.setScreen(this));
        } catch (IOException e) {
            e.printStackTrace();
            SystemToast.addWorldAccessFailureToast(this.client, "Map");
        }
    }

    /**
     * @author ItsFelix5
     * @reason no
     */
    @Overwrite
    public void renderPanoramaBackground(DrawContext context, float deltaTicks) {
        super.renderPanoramaBackground(context, deltaTicks);
    }
}
