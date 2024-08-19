package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.behavior.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.behavior.MoveToTargetSinkStopInaction;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class PiglinPatch extends HumanoidMobPatch<Piglin> {
	public PiglinPatch() {
		super(Faction.PIGLINS);
	}
	
	public static void initAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PIGLIN, EpicFightAttributes.IMPACT.get(), 1.0D);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.PIGLIN_IDLE);
		animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotions.CELEBRATE, AnimationManager.getInstance().byId(Animations.PIGLIN_CELEBRATE1.getId() + this.original.getRandom().nextInt(3)));
		animator.addLivingAnimation(LivingMotions.ADMIRE, Animations.PIGLIN_ADMIRE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.PIGLIN_DEATH);
		animator.addLivingAnimation(LivingMotions.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		animator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_CROSSBOW_AIM);
		animator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		if (this.original.isBaby()) {
			SPSpawnData packet = new SPSpawnData(this.original.getId());
			EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
		}
		
		super.onStartTracking(trackingPlayer);
	}
	
	@Override
	public void processSpawnData(ByteBuf buf) {
		ClientAnimator animator = this.getClientAnimator();
		animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_RUN);
		animator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.getOriginal().getOffhandItem().is(ItemTags.PIGLIN_LOVED))
			this.currentLivingMotion = LivingMotions.ADMIRE;
		else if (this.original.isDancing())
			this.currentLivingMotion = LivingMotions.CELEBRATE;
		else
			super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 13, MeleeAttack.class);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		BrainRecomposer.recomposePiglinBrain(this.original.getBrain(), (builder != null) ? new AnimatedCombatBehavior<>(this, builder.build(this)) : null, new MoveToTargetSinkStopInaction());
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
	}
}
