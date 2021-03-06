package twilightforest.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.entity.ModelTFBlockGoblin;
import twilightforest.entity.EntityTFBlockGoblin;

public class RenderTFBlockGoblin<T extends EntityTFBlockGoblin, M extends ModelTFBlockGoblin<T>> extends BipedRenderer<T, M> {

	private static final ResourceLocation textureLoc = TwilightForestMod.getModelTexture("blockgoblin.png");

	public RenderTFBlockGoblin(EntityRendererManager manager, M model, float shadowSize) {
		super(manager, model, shadowSize);
	}

	@Override
	public void render(T goblin, float yaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		super.render(goblin, yaw, partialTicks, stack, buffer, light);
		renderManager.renderEntityStatic(goblin.block, partialTicks, false);
		renderManager.renderEntityStatic(goblin.chain1, partialTicks, false);
		renderManager.renderEntityStatic(goblin.chain2, partialTicks, false);
		renderManager.renderEntityStatic(goblin.chain3, partialTicks, false);//renderEntity
	}

	@Override
	public ResourceLocation getEntityTexture(T entity) {
		return textureLoc;
	}
}
