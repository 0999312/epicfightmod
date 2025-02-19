package yesman.epicfight.api.animation.property;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationEvent.TimePeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.TimeStampedEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public abstract class AnimationProperty<T> {
	private static final Map<String, AnimationProperty<?>> SERIALIZABLE_ANIMATION_PROPERTY_KEYS = Maps.newHashMap();
	
	@SuppressWarnings("unchecked")
	public static <T> AnimationProperty<T> getSerializableProperty(String name) {
		if (!SERIALIZABLE_ANIMATION_PROPERTY_KEYS.containsKey(name)) {
			throw new IllegalStateException("No property key named " + name);
		}
		
		return (AnimationProperty<T>) SERIALIZABLE_ANIMATION_PROPERTY_KEYS.get(name);
	}
	
	private final Codec<T> codecs;
	private final String name;
	
	public AnimationProperty(String name, @Nullable Codec<T> codecs) {
		this.codecs = codecs;
		this.name = name;
		
		if (name != null) {
			if (SERIALIZABLE_ANIMATION_PROPERTY_KEYS.containsKey(name)) {
				throw new IllegalStateException("Animation property key " + name + " is already registered.");
			}
			
			SERIALIZABLE_ANIMATION_PROPERTY_KEYS.put(name, this);
		}
	}
	
	public AnimationProperty(String name) {
		this(name, null);
	}
	
	public T parseFrom(JsonElement e) {
		return this.codecs.parse(JsonOps.INSTANCE, e).resultOrPartial((errm) -> EpicFightMod.LOGGER.warn("Failed to parse property " + this.name + " because of " + errm)).orElseThrow();
	}
	
	public Codec<T> getCodecs() {
		return this.codecs;
	}
	
	public static class StaticAnimationProperty<T> extends AnimationProperty<T> {
		public StaticAnimationProperty(String rl, @Nullable Codec<T> codecs) {
			super(rl, codecs);
		}
		
		public StaticAnimationProperty() {
			this(null, null);
		}
		
		/**
		 * Events that are fired in every tick.
		 */
		public static final StaticAnimationProperty<AnimationEvent[]> EVENTS = new StaticAnimationProperty<AnimationEvent[]> ();
		
		/**
		 * Events that are fired in specific time.
		 */
		public static final StaticAnimationProperty<TimeStampedEvent[]> TIME_STAMPED_EVENTS = new StaticAnimationProperty<TimeStampedEvent[]> ();
		
		/**
		 * Events that are fired in specific time.
		 */
		public static final StaticAnimationProperty<TimePeriodEvent[]> TIME_PERIOD_EVENTS = new StaticAnimationProperty<TimePeriodEvent[]> ();
		
		/**
		 * Events that are fired when the animation starts.
		 */
		public static final StaticAnimationProperty<AnimationEvent[]> ON_BEGIN_EVENTS = new StaticAnimationProperty<AnimationEvent[]> ();
		
		/**
		 * Events that are fired when the animation ends.
		 */
		public static final StaticAnimationProperty<AnimationEvent[]> ON_END_EVENTS = new StaticAnimationProperty<AnimationEvent[]> ();
		
		/**
		 * You can modify the playback speed of the animation.
		 */
		public static final StaticAnimationProperty<PlaybackSpeedModifier> PLAY_SPEED_MODIFIER = new StaticAnimationProperty<PlaybackSpeedModifier> ();
		
		/**
		 * You can modify the playback speed of the animation.
		 */
		public static final StaticAnimationProperty<PlaybackTimeModifier> ELAPSED_TIME_MODIFIER = new StaticAnimationProperty<PlaybackTimeModifier> ();
		
		/**
		 * This property will be called both in client and server when modifying the pose
		 */
		public static final StaticAnimationProperty<PoseModifier> POSE_MODIFIER = new StaticAnimationProperty<PoseModifier> ();
		
		/**
		 * Fix the head rotation to the player's body rotation
		 */
		public static final StaticAnimationProperty<Boolean> FIXED_HEAD_ROTATION = new StaticAnimationProperty<Boolean> ();
	}
	
	public static class ActionAnimationProperty<T> extends AnimationProperty<T> {
		public ActionAnimationProperty(String rl, @Nullable Codec<T> codecs) {
			super(rl, codecs);
		}
		
		public ActionAnimationProperty() {
			this(null, null);
		}
		
		/**
		 * This property will set the entity's delta movement to (0, 0, 0) at the beginning of an animation if true.
		 */
		public static final ActionAnimationProperty<Boolean> STOP_MOVEMENT = new ActionAnimationProperty<Boolean> ("stop_movements", Codec.BOOL);
		
		/**
		 * This property will move entity's coord also as y axis if true.
		 * Don't recommend using this property because it's old system. Use the coord joint instead.
		 */
		public static final ActionAnimationProperty<Boolean> MOVE_VERTICAL = new ActionAnimationProperty<Boolean> ("move_vertically", Codec.BOOL);
		
		/**
		 * This property determines the time of entity not affected by gravity.
		 */
		public static final ActionAnimationProperty<TimePairList> NO_GRAVITY_TIME = new ActionAnimationProperty<TimePairList> ();
		
		/**
		 * Coord of action animation
		 */
		public static final ActionAnimationProperty<TransformSheet> COORD = new ActionAnimationProperty<TransformSheet> ();
		
		/**
		 * This property determines whether to move the entity in link animation or not.
		 */
		public static final ActionAnimationProperty<Boolean> MOVE_ON_LINK = new ActionAnimationProperty<Boolean> ("move_during_link", Codec.BOOL);
		
		/**
		 * You can specify the coord movement time in action animation. Must be registered in order of time.
		 */
		public static final ActionAnimationProperty<TimePairList> MOVE_TIME = new ActionAnimationProperty<TimePairList> ();
		
		/**
		 * Set the dynamic coordinates of action animation.
		 */
		public static final ActionAnimationProperty<MoveCoordSetter> COORD_SET_BEGIN = new ActionAnimationProperty<MoveCoordSetter> ();
		
		/**
		 * Set the dynamic coordinates of action animation.
		 */
		public static final ActionAnimationProperty<MoveCoordSetter> COORD_SET_TICK = new ActionAnimationProperty<MoveCoordSetter> ();
		
		/**
		 * Set the coordinates of action animation.
		 */
		public static final ActionAnimationProperty<MoveCoordGetter> COORD_GET = new ActionAnimationProperty<MoveCoordGetter> ();
		
		/**
		 * This property determines if the speed effect will increase the move distance.
		 */
		public static final ActionAnimationProperty<Boolean> AFFECT_SPEED = new ActionAnimationProperty<Boolean> ("move_speed_based_distance", Codec.BOOL);
		
		/**
		 * This property determines if the movement can be canceled by {@link LivingEntityPatch#shouldBlockMoving()}.
		 */
		public static final ActionAnimationProperty<Boolean> CANCELABLE_MOVE = new ActionAnimationProperty<Boolean> ("cancellable_movement", Codec.BOOL);
		
		/**
		 * Death animations won't be played if this value is true
		 */
		public static final ActionAnimationProperty<Boolean> IS_DEATH_ANIMATION = new ActionAnimationProperty<Boolean> ("is_death", Codec.BOOL);
		
		/**
		 * This property determines the update time of {@link ActionAnimationProperty#COORD_SET_TICK}
		 */
		public static final ActionAnimationProperty<TimePairList> COORD_UPDATE_TIME = new ActionAnimationProperty<TimePairList> ();
		
		/**
		 * This property determines if it reset the player basic attack combo counter or not {@link BasicAttack}
		 */
		public static final ActionAnimationProperty<Boolean> RESET_PLAYER_COMBO_COUNTER = new ActionAnimationProperty<Boolean> ("reset_combo_attack_counter", Codec.BOOL);
	}
	
	public static class AttackAnimationProperty<T> extends AnimationProperty<T> {
		public AttackAnimationProperty(String rl, @Nullable Codec<T> codecs) {
			super(rl, codecs);
		}
		
		public AttackAnimationProperty() {
			this(null, null);
		}
		
		/**
		 * This property determines if the animation has a fixed amount of move distance not depending on the distance between attacker and target entity
		 */
		public static final AttackAnimationProperty<Boolean> FIXED_MOVE_DISTANCE = new AttackAnimationProperty<Boolean> ("fixed_movement_distance", Codec.BOOL);
		
		/**
		 * This property determines how much the playback speed will be affected by entity's attack speed.
		 */
		public static final AttackAnimationProperty<Float> ATTACK_SPEED_FACTOR = new AttackAnimationProperty<Float> ("attack_speed_factor", Codec.FLOAT);
		
		/**
		 * This property determines the basis of the speed factor. Default basis is the total animation time.
		 */
		public static final AttackAnimationProperty<Float> BASIS_ATTACK_SPEED = new AttackAnimationProperty<Float> ("basis_attack_speed", Codec.FLOAT);
		
		/**
		 * This property adds interpolated colliders when detecting colliding entities by using @MultiCollider.
		 */
		public static final AttackAnimationProperty<Integer> EXTRA_COLLIDERS = new AttackAnimationProperty<Integer> ("extra_colliders", Codec.INT);
		
		/**
		 * This property determines a minimal distance between attacker and target.
		 */
		public static final AttackAnimationProperty<Double> REACH = new AttackAnimationProperty<Double> ("reach", Codec.DOUBLE);
	}
	
	public static class AttackPhaseProperty<T> extends AnimationProperty<T> {
		public AttackPhaseProperty(String rl, @Nullable Codec<T> codecs) {
			super(rl, codecs);
		}
		
		public AttackPhaseProperty() {
			this(null, null);
		}
		
		public static final AttackPhaseProperty<ValueModifier> MAX_STRIKES_MODIFIER = new AttackPhaseProperty<ValueModifier> ("max_strikes", ValueModifier.CODECS);
		public static final AttackPhaseProperty<ValueModifier> DAMAGE_MODIFIER = new AttackPhaseProperty<ValueModifier> ("damage", ValueModifier.CODECS);
		public static final AttackPhaseProperty<Set<ExtraDamageInstance>> EXTRA_DAMAGE = new AttackPhaseProperty<Set<ExtraDamageInstance>> ();
		public static final AttackPhaseProperty<ValueModifier> ARMOR_NEGATION_MODIFIER = new AttackPhaseProperty<ValueModifier> ("armor_negation", ValueModifier.CODECS);
		public static final AttackPhaseProperty<ValueModifier> IMPACT_MODIFIER = new AttackPhaseProperty<ValueModifier> ("impact", ValueModifier.CODECS);
		public static final AttackPhaseProperty<StunType> STUN_TYPE = new AttackPhaseProperty<StunType> ();
		public static final AttackPhaseProperty<SoundEvent> SWING_SOUND = new AttackPhaseProperty<SoundEvent> ();
		public static final AttackPhaseProperty<SoundEvent> HIT_SOUND = new AttackPhaseProperty<SoundEvent> ();
		public static final AttackPhaseProperty<RegistryObject<HitParticleType>> PARTICLE = new AttackPhaseProperty<RegistryObject<HitParticleType>> ();
		public static final AttackPhaseProperty<Priority> HIT_PRIORITY = new AttackPhaseProperty<Priority> ();
		public static final AttackPhaseProperty<Set<TagKey<DamageType>>> SOURCE_TAG = new AttackPhaseProperty<Set<TagKey<DamageType>>> ();
		public static final AttackPhaseProperty<Function<LivingEntityPatch<?>, Vec3>> SOURCE_LOCATION_PROVIDER = new AttackPhaseProperty<Function<LivingEntityPatch<?>, Vec3>> ();
	}
	
	@FunctionalInterface
	public interface Registerer<T> {
		void register(Map<AnimationProperty<T>, Object> properties, AnimationProperty<T> key, T object);
	}
	
	@FunctionalInterface
	public interface PoseModifier {
		void modify(DynamicAnimation self, Pose pose, LivingEntityPatch<?> entitypatch, float elapsedTime, float partialTicks);
	}
	
	@FunctionalInterface
	public interface PlaybackSpeedModifier {
		float modify(DynamicAnimation self, LivingEntityPatch<?> entitypatch, float speed, float prevElapsedTime, float elapsedTime);
	}
	
	@FunctionalInterface
	public interface PlaybackTimeModifier {
		Pair<Float, Float> modify(DynamicAnimation self, LivingEntityPatch<?> entitypatch, float speed, float prevElapsedTime, float elapsedTime);
	}
}