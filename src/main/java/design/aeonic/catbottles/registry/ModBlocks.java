package design.aeonic.catbottles.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.content.bottles.CatBottleBlock;
import design.aeonic.catbottles.content.bottles.CatBottleBlockEntity;
import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.base.cats.ICatContainer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModBlocks {

	public static BlockEntityEntry<CatBottleBlockEntity> CAT_BOTTLE_ENTITY;

	public static final Map<CatBottleItem.CatType, BlockEntry<CatBottleBlock>> CAT_BOTTLES =
			Stream.of(ICatContainer.CatType.values()).collect(Collectors.toUnmodifiableMap($ -> $, type ->
					CatBottles.REG.block("cat_bottle_" + type, prv -> new CatBottleBlock(prv, type))
							.properties(props -> BlockBehaviour.Properties.of(Material.DECORATION).dynamicShape()
									.strength(0.3F)
									.sound(CatBottleBlock.SOUND_TYPE).noDrops()
									.noOcclusion().isViewBlocking((x, y, z) -> false)
									.lightLevel(s -> s.getValue(CatBottleBlock.IS_GLOWY) ? 8 : 0)
									.randomTicks())
							.tag(BlockTags.MINEABLE_WITH_PICKAXE)
							.addLayer(() -> RenderType::cutout)
							.loot((prv, self) -> {})//prv.add(self, new LootTable.Builder())) // mIsSiNg LoOt TaBlE eNtRy FoR bLoCk
							.lang("Bottle of Cat")
							.blockstate((ctx, prv) -> prv.getVariantBuilder(ctx.get()).forAllStatesExcept(s -> {
								var isBig = s.getValue(CatBottleBlock.IS_BIG);
								var shape = s.getValue(CatBottleBlock.BOTTLE_SHAPE);

								String spec = (isBig ? "big" : "small") + (shape == CatBottleBlock.BottleShape.CHAIN
												? "_chain" : (shape == CatBottleBlock.BottleShape.TIPPED ? "_tipped" : ""));

								return ConfiguredModel.builder().modelFile(prv.models().withExistingParent(
										ctx.getName() + "_" + spec, CatBottles.MOD_ID +
												":block/template_" + spec + "_cat_bottle")
										.texture("cat", "minecraft:entity/cat/" + type)
										.texture("bottle", type.getTexture()))
										.rotationY((int) s.getValue(CatBottleBlock.ROTATION).toYRot())
										.build();
							}, CatBottleBlock.IS_GLOWY))
							.register()));

	static {
		var validBlocks = CAT_BOTTLES.values().stream().map(CatBlockSupplier::new).toArray(CatBlockSupplier[]::new);
		CAT_BOTTLE_ENTITY = CatBottles.REG.blockEntity("cat_bottle_block", CatBottleBlockEntity::new).validBlocks(validBlocks).register();
	}

	record CatBlockSupplier(BlockEntry<CatBottleBlock> entry) implements NonNullSupplier<CatBottleBlock> {
		@Nonnull
		@Override
		public CatBottleBlock get() {
			return entry.get();
		}
	}

	public static void register() {}

}
