package chronoelegy.screen;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    public final Text text;
    public final List<Pair<Text, Dialogue>> options = new ArrayList<>();
    public final Runnable cb = () -> {};

    public Dialogue(Text text) {
        this.text = text;
    }

    public static Dialogue create(boolean skippable, Text text) {
        return new Root(skippable, text);
    }

    public Dialogue option(Text optionText, Dialogue dialogue) {
        options.add(new Pair<>(optionText, dialogue));
        return this;
    }

    public Dialogue ending(Text optionText) {
        options.add(new Pair<>(optionText, null));
        return this;
    }

    public static class Root extends Dialogue {
        public final boolean skippable;

        private Root(boolean skippable, Text text) {
            super(text);
            this.skippable = skippable;
        }
    }
}
