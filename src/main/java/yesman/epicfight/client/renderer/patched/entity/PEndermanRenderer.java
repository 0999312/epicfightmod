package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.MeshProvider;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.EndermanMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyesLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;

@OnlyIn(Dist.CLIENT)
public class PEndermanRenderer extends PatchedLivingEntityRenderer<EnderMan, EndermanPatch, EndermanModel<EnderMan>, EndermanRenderer, EndermanMesh> {
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public PEndermanRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		
		this.addPatchedLayer(EnderEyesLayer.class, new PatchedEyesLayer<>(ENDERMAN_EYE_TEXTURE, () -> Meshes.ENDERMAN));
	}
	
	@Override
	public MeshProvider<EndermanMesh> getDefaultMesh() {
		return Meshes.ENDERMAN;
	}
}