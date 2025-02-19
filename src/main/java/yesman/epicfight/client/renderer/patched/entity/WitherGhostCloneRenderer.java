package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.MeshProvider;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.client.renderer.entity.NoopLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import yesman.epicfight.world.entity.WitherGhostClone;

@OnlyIn(Dist.CLIENT)
public class WitherGhostCloneRenderer extends PatchedEntityRenderer<WitherGhostClone, WitherGhostPatch, NoopLivingEntityRenderer<WitherGhostClone>, WitherMesh> {
	@Override
	public void render(WitherGhostClone entity, WitherGhostPatch entitypatch, NoopLivingEntityRenderer<WitherGhostClone> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		RenderType renderType = RenderType.entityTranslucent(PWitherRenderer.WITHER_INVULNERABLE_LOCATION);
		WitherMesh mesh = this.getMeshProvider(entitypatch).get();
		Armature armature = entitypatch.getArmature();
		float tranparency = entity.isNoAi() ? 0.6F : Mth.sin((entity.tickCount + partialTicks) * 0.025F * Mth.PI) * 0.6F;
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks, false);
		mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, tranparency, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poseMatrices);
		
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			for (Layer.Priority priority : Layer.Priority.values()) {
				AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
				float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
				animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	@Override
	public MeshProvider<WitherMesh> getDefaultMesh() {
		return Meshes.WITHER;
	}
}