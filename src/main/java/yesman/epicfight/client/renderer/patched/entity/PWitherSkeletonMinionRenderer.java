package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

@OnlyIn(Dist.CLIENT)
public class PWitherSkeletonMinionRenderer extends PHumanoidRenderer<PathfinderMob, HumanoidMobPatch<PathfinderMob>, HumanoidModel<PathfinderMob>, HumanoidMobRenderer<PathfinderMob, HumanoidModel<PathfinderMob>>, HumanoidMesh> {
	public PWitherSkeletonMinionRenderer(EntityRendererProvider.Context context) {
		super(Meshes.SKELETON, context);
	}

	@Override
	protected void setJointTransforms(HumanoidMobPatch<PathfinderMob> entitypatch, Armature armature, Pose pose, float partialTicks) {
		Vec3f rootScale = pose.getOrDefaultTransform("Root").scale();
		pose.getOrDefaultTransform("Head").jointLocal(JointTransform.getScale(new Vec3f(1.0F / rootScale.x, 1.0F / rootScale.y, 1.0F / rootScale.z)), OpenMatrix4f::mul);
	}
	
	@Override
	public HumanoidMesh getMesh(HumanoidMobPatch<PathfinderMob> entitypatch) {
		return Meshes.SKELETON;
	}
}