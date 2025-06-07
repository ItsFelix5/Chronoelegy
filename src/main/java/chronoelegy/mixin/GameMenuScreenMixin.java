package chronoelegy.mixin;

import chronoelegy.Main;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("HEAD"))
    public void init(CallbackInfo info) {
        addDrawableChild(CheckboxWidget.builder(Text.translatable("checkbox.speedrunMode"), textRenderer).pos(10, 10).checked(Main.speedrunMode)
                .callback((checkbox, checked) -> Main.speedrunMode = checked).build());
    }
}
