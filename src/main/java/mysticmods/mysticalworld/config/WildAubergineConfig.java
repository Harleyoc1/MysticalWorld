package mysticmods.mysticalworld.config;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class WildAubergineConfig extends FeatureConfig {
  private final int chance;
  private final List<ResourceKey<Level>> dimensions;
  private ForgeConfigSpec.IntValue configChance;
  private ForgeConfigSpec.ConfigValue<List<? extends String>> configDimensions;

  public WildAubergineConfig(int chance, List<BiomeDictionary.Type> biomeTypes, List<BiomeDictionary.Type> biomeRestrictions, List<ResourceKey<Level>> dimensions) {
    super(biomeTypes, biomeRestrictions);
    this.chance = chance;
    this.dimensions = dimensions;
  }

  public int getChance() {
    return configChance.get();
  }

  @Override
  public boolean shouldSpawn() {
    return getChance()<0;
  }

  @Override
  public GenerationStep.Decoration getStage() {
    return GenerationStep.Decoration.VEGETAL_DECORATION;
  }

  @Override
  public void apply(ForgeConfigSpec.Builder builder) {
    builder.comment("Wild Aubergine Generation").push("wild_aubergine");
    configChance = builder.comment("Spawns roughly once every X (0 to disable)").defineInRange("chance", chance, 0, Integer.MAX_VALUE);
    StringJoiner sb = new StringJoiner(",");
    biomes.forEach(o -> sb.add(o.getName()));
    configBiomes = builder.comment("List of biome types to spawn (default [" + sb + "], pass empty list for every biome").define("biomes", sb.toString());
    StringJoiner sb2 = new StringJoiner(",");
    biomeRestrictions.forEach(biome -> sb2.add(biome.getName()));
    configBiomeRestrictions = builder.comment("Which biome types this feature shouldn't spawn in (default END, NETHER)").define("biomeRestrictions", sb2.toString());
    configDimensions = builder.comment("The dimensions that this feature should spawn in as a list (default [\"minecraft:overworld\"])").defineList("dimensions", dimensions.stream().map(ResourceKey::location).map(ResourceLocation::toString).collect(Collectors.toList()), (o) -> o instanceof String);
    builder.pop();
  }

  @Override
  public void reset() {
    super.reset();
    this.storedDimension = null;
  }

  private Set<ResourceKey<Level>> storedDimension = null;

  public Set<ResourceKey<Level>> getDimensions() {
    if (storedDimension == null) {
      storedDimension = configDimensions.get().stream().map(o -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    }

    return storedDimension;
  }
}
