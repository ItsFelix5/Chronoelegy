package chronoelegy;

import chronoelegy.block.ModBlocks;
import chronoelegy.item.ModItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.io.FileReader;
import java.io.IOException;

import static net.minecraft.client.data.BlockStateModelGenerator.createWeightedVariant;

public class DataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModelProvider::new);
    }

    private static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator generator) {
            for (Block block : new Block[]{ModBlocks.GRANDFATHER_CLOCK, ModBlocks.CUCKOO_CLOCK}) {
                generator.registerNorthDefaultHorizontalRotation(block);
                generator.registerParentedItemModel(block, ModelIds.getBlockModelId(block));
            }

            JsonObject parentObject;
            try {
                parentObject = JsonParser.parseReader(new FileReader("../../src/main/resources/assets/chronoelegy/models/block/table.json")).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            var models = BlockStateVariantMap.models(Properties.NORTH, Properties.SOUTH, Properties.EAST, Properties.WEST);
            for (int b = 0; b < 16; b++) {
                boolean n = (b & 1) != 0;
                boolean s = (b & 2) != 0;
                boolean e = (b & 4) != 0;
                boolean w = (b & 8) != 0;

                Identifier modelId = ModelIds.getBlockSubModelId(ModBlocks.TABLE, String.valueOf(b));
                generator.modelCollector.accept(modelId, () -> {
                    JsonObject jsonObject = parentObject.deepCopy();
                    JsonArray elements = jsonObject.getAsJsonArray("elements");

                    for (int i = 0; elements.size() > i;) {
                        String name = elements.get(i).getAsJsonObject().get("name").getAsString();
                        if (((n || e) && name.equals("NE")) ||
                            ((s || e) && name.equals("SE")) ||
                            ((n || w) && name.equals("NW")) ||
                            ((s || w) && name.equals("SW"))) elements.remove(i);
                        else i++;
                    }

                    return jsonObject;
                });

                if(!n && !s && !e && !w) generator.registerItemModel(ModBlocks.TABLE.asItem(), modelId);

                models.register(n, s, e, w, createWeightedVariant(modelId));
            }

            generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(ModBlocks.TABLE).with(models));
        }

        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            generator.register(ModItems.POCKET_WATCH);
            generator.register(ModItems.BROKEN_POCKET_WATCH);
        }
    }
}
