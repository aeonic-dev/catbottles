package design.aeonic.catbottles.content.bottles;

import design.aeonic.catbottles.base.blocks.SimpleEntityBlock;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Locale;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CatBottleBlock extends SimpleEntityBlock<CatBottleBlockEntity> implements ICatContainer<CatBottleBlockEntity> {

    public enum BottleShape implements StringRepresentable {
        NORMAL,
        CHAIN,
        TIPPED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    // chain property actually represents just being below a supporting face; chains are the most applicable example
    public static final BooleanProperty IS_BIG = BooleanProperty.create("is_big");
    public static final BooleanProperty IS_GLOWY = BooleanProperty.create("glowy");
    public static final DirectionProperty ROTATION = BlockStateProperties.HORIZONTAL_FACING;

    public static final EnumProperty<BottleShape> BOTTLE_SHAPE = EnumProperty.create("shape", BottleShape.class);

    public static final SoundType SOUND_TYPE = new SoundType(.8f, 1.5f, SoundEvents.BONE_BLOCK_BREAK, SoundEvents.BONE_BLOCK_STEP, SoundEvents.BONE_BLOCK_PLACE, SoundEvents.CAT_AMBIENT, SoundEvents.GLASS_FALL);

    public static final VoxelShape SHAPE_BIG = Shapes.box(
            5 / 16d, 0 / 16d, 5 / 16d,
            12 / 16d, 10 / 16d, 12 / 16d);

    public static final VoxelShape SHAPE_BIG_TIPPED_NORTH = Shapes.box(
            5 / 16d, 0 / 16d, 2 / 16d,
            12 / 16d, 7 / 16d, 12 / 16d);

    public static final VoxelShape SHAPE_BIG_TIPPED_SOUTH = Shapes.box(
            5 / 16d, 0 / 16d, 5 / 16d,
            12 / 16d, 7 / 16d, 15 / 16d);

    public static final VoxelShape SHAPE_BIG_TIPPED_EAST = Shapes.box(
            5 / 16d, 0 / 16d, 5 / 16d,
            15 / 16d, 7 / 16d, 12 / 16d);

    public static final VoxelShape SHAPE_BIG_TIPPED_WEST = Shapes.box(
            2 / 16d, 0 / 16d, 5 / 16d,
            12 / 16d, 7 / 16d, 12 / 16d);

    public static final VoxelShape SHAPE_BIG_CHAIN = SHAPE_BIG
            .move(0, 5 / 16d, 0);

    public static final VoxelShape SHAPE_SMALL = Shapes.box(
            6 / 16d, 0 / 16d, 6 / 16d,
            11 / 16d, 8 / 16d, 11 / 16d);

    public static final VoxelShape SHAPE_SMALL_TIPPED_NORTH = Shapes.box(
            6 / 16d, 0 / 16d, 2 / 16d,
            11 / 16d, 5 / 16d, 11 / 16d);

    public static final VoxelShape SHAPE_SMALL_TIPPED_SOUTH = Shapes.box(
            6 / 16d, 0 / 16d, 6 / 16d,
            11 / 16d, 5 / 16d, 15 / 16d);

    public static final VoxelShape SHAPE_SMALL_TIPPED_EAST = Shapes.box(
            6 / 16d, 0 / 16d, 6 / 16d,
            15 / 16d, 5 / 16d, 11 / 16d);

    public static final VoxelShape SHAPE_SMALL_TIPPED_WEST = Shapes.box(
            2 / 16d, 0 / 16d, 6 / 16d,
            11 / 16d, 5 / 16d, 11 / 16d);

    public static final VoxelShape SHAPE_SMALL_CHAIN = SHAPE_SMALL
            .move(0, 7 / 16d, 0);

    public static final VoxelShape[] TIPPED_SHAPES = {
            SHAPE_BIG_TIPPED_SOUTH, SHAPE_BIG_TIPPED_WEST, SHAPE_BIG_TIPPED_NORTH, SHAPE_BIG_TIPPED_EAST,
            SHAPE_SMALL_TIPPED_SOUTH, SHAPE_SMALL_TIPPED_WEST, SHAPE_SMALL_TIPPED_NORTH, SHAPE_SMALL_TIPPED_EAST
    };

    // on random tick, chance of tipping over
    private static final float TIP_CHANCE = .05f;

    private final CatBottleItem.CatType type;

    public CatBottleBlock(Properties blockProps, CatBottleItem.CatType type) {
        super(blockProps, ModBlocks.CAT_BOTTLE_ENTITY, CatBottleBlockEntity::new);
        this.type = type;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(BOTTLE_SHAPE) == BottleShape.NORMAL && pRandom.nextFloat() <= TIP_CHANCE) {
            pLevel.setBlock(pPos, pState.setValue(BOTTLE_SHAPE, BottleShape.TIPPED), 2);
            pLevel.playSound(null, pPos, SoundEvents.BONE_BLOCK_BREAK, SoundSource.BLOCKS, 1, 1);
            pLevel.playSound(null, pPos, SoundEvents.CAT_STRAY_AMBIENT, SoundSource.BLOCKS, 1, 1);
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public CompoundTag getItemData(CatBottleBlockEntity catBottleBlockEntity) {
        return catBottleBlockEntity.getItemData();
    }

    @Override
    public CompoundTag getEntityData(CatBottleBlockEntity catBottleBlockEntity) {
        return catBottleBlockEntity.getEntityData();
    }

    @Override
    public CatBottleItem.CatType getCatType(CatBottleBlockEntity catBottleBlockEntity) {
        return type;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var stack = pPlayer.getItemInHand(pHand);
        if (stack.is(Items.GLOW_INK_SAC) && !pState.getValue(IS_GLOWY)) {
            if (!pPlayer.isCreative()) stack.shrink(1);
            var newState = pState.setValue(IS_GLOWY, true);
            pLevel.setBlock(pPos, newState, 2);
            pPlayer.playSound(SoundEvents.CAT_EAT, 1, 1);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float dist) {
        super.fallOn(level, state, pos, entity, dist);
        if ((entity instanceof Player player && !player.isCreative() && dist > player.getMaxFallDistance()) ||
                (entity instanceof FallingBlockEntity fallingBlock &&
                        (fallingBlock.getBlockState().is(Blocks.ANVIL) || fallingBlock.getBlockState().is(Blocks.POINTED_DRIPSTONE))
                        && fallingBlock.fallDistance > fallingBlock.getMaxFallDistance() - 2)) {
            if (level.getBlockEntity(pos) instanceof CatBottleBlockEntity be)
                be.setShouldReleaseEntity(true);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1, 1);
        }
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (pPlayer.isCreative() && pLevel.getBlockEntity(pPos) instanceof CatBottleBlockEntity be)
            be.setShouldDropItem(false);
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pLevel instanceof ServerLevel level && !pState.is(pNewState.getBlock()) && level.getBlockEntity(pPos) instanceof CatBottleBlockEntity be) {
            if (be.shouldReleaseEntity()) {
                var cat = spawnFrom(be, level, pPos, c -> {});
            }
            else if (be.shouldDropItem()){
                Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), be.makeStack(type));
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public void onPropertyDataChange(BlockState state, ServerLevel level, BlockPos pos, CatBottleBlockEntity entity) {
        level.setBlock(pos, state.setValue(IS_BIG, entity.getItemData().getBoolean(ICatContainer.TAG_IS_BIG)), 2);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        return switch (pDirection) {
            case DOWN, UP -> {
                var flag = canPlaceHere(pLevel, pCurrentPos);
                if (flag == -1) yield Blocks.AIR.defaultBlockState();
                yield pState.setValue(BOTTLE_SHAPE, flag == 1 ? BottleShape.CHAIN : BottleShape.NORMAL);
            }
            default -> super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
        };
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return canPlaceHere(pContext.getLevel(), pContext.getClickedPos()) != -1 ?
                super.getStateForPlacement(pContext) : null; // can't place
    }

    /**
     * @return -1: can't place, 0: place on floor, 1: place on chain
     */
    public int canPlaceHere(LevelReader level, BlockPos pos) {
        if (SupportType.CENTER.isSupporting(level.getBlockState(pos.below()), level, pos, Direction.UP)) return 0;
        if (SupportType.CENTER.isSupporting(level.getBlockState(pos.above()), level, pos, Direction.DOWN) &&
                !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above())) return 1;
        return -1;
    }

    @Override
    protected StateProperty<?, ?>[] getStateProperties() {
        return new StateProperty[] {
                StateProperty.of(IS_BIG, c -> c.getItemInHand()
                        .getOrCreateTagElement(ICatContainer.TAG_ITEM).getBoolean(ICatContainer.TAG_IS_BIG)),
                StateProperty.of(ROTATION, UseOnContext::getHorizontalDirection),
                StateProperty.of(IS_GLOWY, $ -> false),
                StateProperty.of(BOTTLE_SHAPE, c -> canPlaceHere(
                        c.getLevel(), c.getClickedPos()) == 1 ? BottleShape.CHAIN : BottleShape.NORMAL)
        };
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 offset = pState.getOffset(pLevel, pPos);

        boolean isBig = pState.getValue(IS_BIG);
        var shape = switch (pState.getValue(BOTTLE_SHAPE)) {
            case NORMAL ->  isBig ? SHAPE_BIG : SHAPE_SMALL;
            case CHAIN ->   isBig ? SHAPE_BIG_CHAIN : SHAPE_SMALL_CHAIN;
            case TIPPED ->  TIPPED_SHAPES[(isBig ? 0 : 4) + pState.getValue(ROTATION).get2DDataValue()];
        };
        return shape.move(offset.x - 1/32d, offset.y, offset.z - 1/32d);
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
        return false;
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 1 / 16f;
    }
}
