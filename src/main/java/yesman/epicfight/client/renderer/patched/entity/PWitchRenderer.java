package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.MeshProvider;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.VillagerMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitchPatch;

@OnlyIn(Dist.CLIENT)
public class PWitchRenderer extends PatchedLivingEntityRenderer<Witch, WitchPatch, WitchModel<Witch>, WitchRenderer, VillagerMesh> {
	public PWitchRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(WitchItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	public MeshProvider<VillagerMesh> getMeshProvider(WitchPatch entitypatch) {
		return Meshes.WITCH;
	}
}