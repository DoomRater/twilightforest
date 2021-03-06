package twilightforest.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.inventory.ContainerTFUncrafting;
import twilightforest.network.PacketUncraftingGui;
import twilightforest.network.TFPacketHandler;

import java.io.IOException;

public class GuiTFGoblinCrafting extends ContainerScreen<ContainerTFUncrafting> {

	private static final ResourceLocation textureLoc = TwilightForestMod.getGuiTexture("guigoblintinkering.png");

	public GuiTFGoblinCrafting(PlayerInventory inventory, World world, int x, int y, int z) {
		super(new ContainerTFUncrafting(inventory, world, x, y, z));
	}

	@Override
	protected void init() {
		super.init();

		int id = 0;

		this.buttons.add(new CycleButton(++id, guiLeft + 40, guiTop + 22, true, false));
		this.buttons.add(new CycleButton(++id, guiLeft + 40, guiTop + 55, false, false));

		//this.buttonList.add(new ModeButton(++id, guiLeft + 7, guiTop + 57));

		this.buttons.add(new CycleButtonMini(++id, guiLeft + 27, guiTop + 56, true));
		this.buttons.add(new CycleButtonMini(++id, guiLeft + 27, guiTop + 63, false));

		//this.buttonList.add(new RefreshButton(++id, guiLeft + 26, guiTop + 57));

		this.buttons.add(new CycleButton(++id, guiLeft + 121, guiTop + 22, true, true));
		this.buttons.add(new CycleButton(++id, guiLeft + 121, guiTop + 55, false, true));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.font.drawString(I18n.format(TFBlocks.uncrafting_table.get().getTranslationKey() + ".name"), 8, 6, 4210752);
		this.font.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);
		this.minecraft.getTextureManager().bindTexture(textureLoc);
		int frameX = (this.width - this.xSize) / 2;
		int frameY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(frameX, frameY, 0, 0, this.xSize, this.ySize);

		ContainerTFUncrafting tfContainer = (ContainerTFUncrafting) this.inventorySlots;

		// show uncrafting ingredients as background
		RenderHelper.enableGUIStandardItemLighting();
		RenderSystem.pushMatrix();
		RenderSystem.translatef(guiLeft, guiTop, 0);

		for (int i = 0; i < 9; i++) {
			Slot uncrafting = tfContainer.getSlot(2 + i);
			Slot assembly = tfContainer.getSlot(11 + i);

			if (uncrafting.getHasStack()) {
				drawSlotAsBackground(uncrafting, assembly);
			}
		}
		RenderSystem.popMatrix();

		// show the costs if there are any
		FontRenderer fontRendererObj = this.minecraft.fontRenderer;
		RenderHelper.disableStandardItemLighting();

		int costVal = tfContainer.getUncraftingCost();
		if (costVal > 0) {
			if (this.minecraft.player.experienceLevel < costVal && !this.minecraft.player.abilities.isCreativeMode) {
				int color = 0xA00000;
				String cost = "" + costVal;
				fontRendererObj.drawStringWithShadow(cost, frameX + 48 - fontRendererObj.getStringWidth(cost), frameY + 38, color);
			} else {
				int color = 0x80FF20;
				String cost = "" + costVal;
				fontRendererObj.drawStringWithShadow(cost, frameX + 48 - fontRendererObj.getStringWidth(cost), frameY + 38, color);
			}
		}

		costVal = tfContainer.getRecraftingCost();
		if (costVal > 0) {
			if (this.minecraft.player.experienceLevel < costVal && !this.minecraft.player.abilities.isCreativeMode) {
				int color = 0xA00000;
				String cost = "" + costVal;
				fontRendererObj.drawStringWithShadow(cost, frameX + 130 - fontRendererObj.getStringWidth(cost), frameY + 38, color);
			} else {
				int color = 0x80FF20;
				String cost = "" + costVal;
				fontRendererObj.drawStringWithShadow(cost, frameX + 130 - fontRendererObj.getStringWidth(cost), frameY + 38, color);
			}
		}

	}

	private void drawSlotAsBackground(Slot backgroundSlot, Slot appearSlot) {

		int screenX = appearSlot.xPos;
		int screenY = appearSlot.yPos;
		ItemStack itemStackToRender = backgroundSlot.getStack();
		this.zLevel = 50.0F;
		itemRenderer.zLevel = 50.0F;

        itemRenderer.renderItemIntoGUI(itemStackToRender, screenX, screenY);
        itemRenderer.renderItemOverlayIntoGUI(this.font, itemStackToRender, screenX, screenY, "");

		boolean itemBroken = ContainerTFUncrafting.isMarked(itemStackToRender);

		// draw 50% gray rectangle over the item
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();
		Gui.drawRect(screenX, screenY, screenX + 16, screenY + 16, itemBroken ? 0x80FF8b8b : 0x9f8b8b8b);
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();

        itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	@Override
	protected void actionPerformed(Button button) throws IOException {
		super.actionPerformed(button);

		if (this.inventorySlots instanceof ContainerTFUncrafting) {
			ContainerTFUncrafting uncrafting = (ContainerTFUncrafting) this.inventorySlots;

			if (button instanceof CycleButton) {
				CycleButton cycleButton = (CycleButton) button;

				if (cycleButton.constructive) {
					TFPacketHandler.CHANNEL.sendToServer(new PacketUncraftingGui(cycleButton.up ? 4 : 5));

					if (((CycleButton) button).up) {
						uncrafting.recipeInCycle++;
					} else {
						uncrafting.recipeInCycle--;
					}

					uncrafting.onCraftMatrixChanged(uncrafting.assemblyMatrix);
				} else {
					TFPacketHandler.CHANNEL.sendToServer(new PacketUncraftingGui(cycleButton.up ? 0 : 1));

					if (((CycleButton) button).up) {
						uncrafting.unrecipeInCycle++;
					} else {
						uncrafting.unrecipeInCycle--;
					}

					uncrafting.onCraftMatrixChanged(uncrafting.tinkerInput);
				}
			}

			//if (button instanceof ModeButton) {
			//	TFPacketHandler.CHANNEL.sendToServer(new PacketUncraftingGui(2));

			//	uncrafting.ingredientMode = !uncrafting.ingredientMode;
			//}

			if (button instanceof CycleButtonMini) {
				TFPacketHandler.CHANNEL.sendToServer(new PacketUncraftingGui(((CycleButtonMini) button).up ? 2 : 3));

				if (((CycleButtonMini) button).up) {
					uncrafting.ingredientsInCycle++;
				} else {
					uncrafting.ingredientsInCycle--;
				}

				uncrafting.onCraftMatrixChanged(uncrafting.tinkerInput);
			}

			this.buttons.clear();
			this.init();
			this.updateScreen();
		}
	}

	private static class CycleButton extends Button {
		private final boolean up;
		private final boolean constructive;

		CycleButton(int buttonId, int x, int y, boolean up, boolean constructive) {
			super(buttonId, x, y, 14, 9, "");
			this.up = up;
			this.constructive = constructive;
		}

		@Override
		public void renderButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(GuiTFGoblinCrafting.textureLoc);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				int textureX = 176;
				int textureY = 0;

				if (this.isHovered) textureX += this.width;

				// what's up
				if (!this.up) textureY += this.height;

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, this.width, this.height);
			}
		}
	}

//	private static class ModeButton extends GuiButton {
//		ModeButton(int buttonId, int x, int y) {
//			super(buttonId, x, y, 18, 12, "");
//		}

//		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
//			if (this.visible) {
//				mc.getTextureManager().bindTexture(GuiTFGoblinCrafting.textureLoc);
//				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

//				int textureX = 176;
//				int textureY = 18;

//				if (this.hovered) textureX += this.width;

//				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, this.width, this.height);
//			}
//		}
//	}

	private class CycleButtonMini extends Button {
		private final boolean up;

		CycleButtonMini(int buttonId, int x, int y, boolean up) {
			super(buttonId, x, y, 8, 6, "");
			this.up = up;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(GuiTFGoblinCrafting.textureLoc);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				int textureX = 176;
				int textureY = 41;

				if (this.isHovered) textureX += this.width;

				// what's up
				if (!this.up) textureY += this.height;

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, this.width, this.height);
			}
		}
	}

//	static class RefreshButton extends GuiButton {
//		RefreshButton(int buttonId, int x, int y) {
//			super(buttonId, x, y, 8, 6, "");
//		}

//		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
//			if (this.visible) {
//				mc.getTextureManager().bindTexture(GuiTFGoblinCrafting.textureLoc);
//				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

//				int textureX = 176;
//				int textureY = 30;

//				if (this.hovered) textureX += this.width;

//				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, this.width, this.height);
//			}
//		}
//	}
}
