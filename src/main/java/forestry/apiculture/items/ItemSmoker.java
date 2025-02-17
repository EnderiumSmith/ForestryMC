package forestry.apiculture.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import forestry.api.apiculture.hives.IHiveTile;
import forestry.core.items.ItemForestry;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class ItemSmoker extends ItemForestry {
	public ItemSmoker() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide && isSelected && level.random.nextInt(40) == 0) {
			addSmoke(level, entity, 1);
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
		addSmoke(level, entity, (count % 5) + 1);
	}

	private static HumanoidArm getHandSide(Entity entity) {
		if (entity instanceof LivingEntity LivingEntity) {
			InteractionHand activeHand = LivingEntity.getUsedItemHand();
			HumanoidArm handSide = LivingEntity.getMainArm();
			if (activeHand == InteractionHand.OFF_HAND) {
				handSide = handSide.getOpposite();
			}
			return handSide;
		}
		return HumanoidArm.RIGHT;
	}

	private static void addSmoke(Level level, Entity entity, int distance) {
		if (distance <= 0) {
			return;
		}
		Vec3 look = entity.getLookAngle();
		HumanoidArm handSide = getHandSide(entity);

		Vec3 handOffset;
		if (handSide == HumanoidArm.RIGHT) {
			handOffset = look.cross(new Vec3(0, 1, 0));
		} else {
			handOffset = look.cross(new Vec3(0, -1, 0));
		}

		Vec3 lookDistance = new Vec3(look.x * distance, look.y * distance, look.z * distance);
		Vec3 scaledOffset = handOffset.scale(1.0 / distance);
		Vec3 smokePos = lookDistance.add(entity.position()).add(scaledOffset);

		if (level.isClientSide) {
			ParticleRender.addEntitySmokeFX(level, smokePos.x, smokePos.y + 1, smokePos.z);
		}

		BlockPos blockPos = BlockPos.containing(smokePos.x, smokePos.y + 1, smokePos.z);
		TileUtil.actOnTile(level, blockPos, IHiveTile.class, IHiveTile::calmBees);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		ItemStack itemStack = player.getItemInHand(hand);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		TileUtil.actOnTile(context.getLevel(), context.getClickedPos(), IHiveTile.class, IHiveTile::calmBees);
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}
}
