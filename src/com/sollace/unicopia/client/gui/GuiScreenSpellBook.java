package com.sollace.unicopia.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.container.ContainerBook;
import com.sollace.unicopia.enchanting.IPageUnlockListener;
import com.sollace.unicopia.enchanting.PagesList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

public class GuiScreenSpellBook extends GuiContainer implements IPageUnlockListener {
	private static int currentPage = 0;
	private static ResourceLocation spellBookPageTextures = new ResourceLocation("unicopia", "textures/gui/container/pages/page-" + currentPage + ".png");
	private static final ResourceLocation spellBookGuiTextures = new ResourceLocation("unicopia", "textures/gui/container/book.png");
	
	private PlayerExtension playerExtension;
	
	private PageButton nextPage;
	private PageButton prevPage;
	
	public GuiScreenSpellBook(EntityPlayer player) {
		super(new ContainerBook(player.inventory, player.worldObj, new BlockPos(player)));
		player.openContainer = inventorySlots;
		((ContainerBook)inventorySlots).setListener(this);
		xSize = 405;
        ySize = 219;
        allowUserInput = true;
        playerExtension = PlayerExtension.get(player);
        if (playerExtension.getTotalPagesUnlocked() > 0) {
        	((ContainerBook)inventorySlots).initCraftingSlots(player);
        }
	}
	
	public void initGui() {
		super.initGui();
		buttonList.clear();
		
		int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
		
		buttonList.add(nextPage = new PageButton(1, x + 360, y + 160, true));
        buttonList.add(prevPage = new PageButton(2, x + 20, y + 160, false));
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		initGui();
		if (button.id == 1) {
			nextPage();
		} else {
			prevPage();
		}
	}
	
	public void nextPage() {
		if (currentPage == 0) {
			playerExtension.unlockPage(1);
		}
		if (currentPage < PagesList.getTotalPages() - 1) {
			currentPage++;
			if (playerExtension.getTotalPagesUnlocked() > 0) {
				((ContainerBook)inventorySlots).initCraftingSlots(playerExtension.getPlayer());
			}
			spellBookPageTextures = new ResourceLocation("unicopia", "textures/gui/container/pages/page-" + currentPage + ".png");
			onPageUnlocked();
			PagesList.readPage(currentPage);
		}
	}
	
	public void onPageUnlocked() {
		if (PagesList.hasUnreadPagesAfter(currentPage)) nextPage.triggerShake();
        if (PagesList.hasUnreadPagesBefore(currentPage)) prevPage.triggerShake();
	}
	
	public void prevPage() {
		if (currentPage > 0) {
			currentPage--;
			if (playerExtension.getTotalPagesUnlocked() > 0) {
				((ContainerBook)inventorySlots).initCraftingSlots(playerExtension.getPlayer());
			}
			spellBookPageTextures = new ResourceLocation("unicopia", "textures/gui/container/pages/page-" + currentPage + ".png");
			onPageUnlocked();
			PagesList.readPage(currentPage);
		}
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String text = (currentPage + 1) + "/" + PagesList.getTotalPages();
		fontRendererObj.drawString(text, 203 - fontRendererObj.getStringWidth(text)/2, 165, 0x0);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(spellBookGuiTextures);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawModalRectWithCustomSizedTexture(left, top, 0, 0, xSize, ySize, 512, 256);
        
        if (playerExtension.hasPageUnlock(currentPage)) {
        	if (mc.getTextureManager().getTexture(spellBookPageTextures) != TextureUtil.missingTexture) {
		        GlStateManager.enableBlend();
		        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        mc.getTextureManager().bindTexture(spellBookPageTextures);
		        
		        drawModalRectWithCustomSizedTexture(left, top, 0, 0, xSize, ySize, 512, 256);
		        GlStateManager.disableBlend();
        	}
        }
        
        if (playerExtension.getTotalPagesUnlocked() > 0) {
        	mc.getTextureManager().bindTexture(spellBookGuiTextures);
	        drawModalRectWithCustomSizedTexture(left + 170, top + 50, 407, 5, 68, 100, 512, 256);
        }
	}
	
	static class PageButton extends GuiButton {
        private final boolean direction;
        
        private int shakesLeft = 0;
        private float shakeCount = 0;
        
        public PageButton(int id, int x, int y, boolean direction) {
            super(id, x, y, 23, 13, "");
            this.direction = direction;
        }
        
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
            	int x = xPosition;
            	int y = yPosition;
            	if (shakesLeft > 0) {
            		shakeCount += (float)Math.PI/2;
            		if (shakeCount >= Math.PI * 2) {
            			shakeCount %= Math.PI*2;
            			shakesLeft--;
            		}
	            	x += (int)(Math.sin(shakeCount)*3);
	            	y -= (int)(Math.sin(shakeCount)*3);
            	}
            	
                boolean hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(spellBookGuiTextures);
                int u = 0;
                int v = 220;
                if (hovered) u += 23;
                if (!direction) v += 13;
                drawModalRectWithCustomSizedTexture(x, y, u, v, 23, 13, 512, 256);
            }
        }
        
        public void triggerShake() {
        	if (shakesLeft <= 0) {
        		shakesLeft = 5;
        	}
        }
    }
}
