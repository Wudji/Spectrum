package de.dafuqs.spectrum.inventories;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CraftingTabletScreen extends HandledScreen<CraftingTabletScreenHandler> {

    public static final Identifier BACKGROUND = new Identifier(SpectrumCommon.MOD_ID, "textures/gui/container/altar.png");

    public CraftingTabletScreen(CraftingTabletScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.backgroundHeight = 194;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // draw "title" and "inventory" texts
        int titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2; // 8;
        int titleY = 7;
        Text title = this.title;
        int inventoryX = 8;
        int intInventoryY = 100;

        this.textRenderer.draw(matrices, title, titleX, titleY, 4210752);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, inventoryX, intInventoryY, 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }


}