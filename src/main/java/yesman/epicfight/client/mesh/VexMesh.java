package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class VexMesh extends HumanoidMesh implements MeshProvider<VexMesh> {
	public final AnimatedModelPart leftWing;
	public final AnimatedModelPart rightWing;
	
	public VexMesh(Map<String, float[]> arrayMap, Map<MeshPartDefinition, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.leftWing = this.getOrLogException(this.parts, "leftWing");
		this.rightWing = this.getOrLogException(this.parts, "rightWing");
	}

	@Override
	public VexMesh get() {
		return this;
	}
}