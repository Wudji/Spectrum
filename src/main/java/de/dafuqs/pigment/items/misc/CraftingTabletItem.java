package de.dafuqs.pigment.items.misc;

import de.dafuqs.pigment.InventoryHelper;
import de.dafuqs.pigment.inventories.CraftingTabletScreenHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;
import java.util.Optional;

public class CraftingTabletItem extends Item {

    private static final Text TITLE = new TranslatableText("item.pigment.crafting_tablet");

    public CraftingTabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            Recipe storedRecipe = getStoredRecipe(world, itemStack);
            if(storedRecipe == null || user.isSneaking()) {
                user.openHandledScreen(createScreenHandlerFactory(world, (ServerPlayerEntity) user, itemStack));
            } else {
                tryCraftRecipe((ServerPlayerEntity) user, storedRecipe);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        } else {
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(World world, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new CraftingTabletScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, serverPlayerEntity.getBlockPos()), itemStack), TITLE);
    }

    private Recipe getStoredRecipe(World world, ItemStack itemStack) {
        NbtCompound nbtCompound = itemStack.getTag();

        if(nbtCompound != null && nbtCompound.contains("recipe")) {
            String recipeString = nbtCompound.getString("recipe");
            Identifier recipeIdentifier = new Identifier(recipeString);

            Optional<? extends Recipe> optional = world.getRecipeManager().get(recipeIdentifier);
            if(optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    private void tryCraftRecipe(ServerPlayerEntity serverPlayerEntity, Recipe recipe) {
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        Inventory playerInventory = serverPlayerEntity.getInventory();
        if (InventoryHelper.removeFromInventory(ingredients, playerInventory, true)) {
            InventoryHelper.removeFromInventory(ingredients, playerInventory, false);

            ItemStack craftingResult = recipe.getOutput().copy();
            boolean insertInventorySuccess = serverPlayerEntity.getInventory().insertStack(craftingResult);
            ItemEntity itemEntity;
            if (insertInventorySuccess && craftingResult.isEmpty()) {
                craftingResult.setCount(1);
                itemEntity = serverPlayerEntity.dropItem(craftingResult, false);
                if (itemEntity != null) {
                    itemEntity.setDespawnImmediately();
                }

                serverPlayerEntity.world.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverPlayerEntity.currentScreenHandler.sendContentUpdates();
            } else {
                itemEntity = serverPlayerEntity.dropItem(craftingResult, false);
                if (itemEntity != null) {
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayerEntity.getUuid());
                }
            }
        }

    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        Recipe recipe = getStoredRecipe(world, itemStack);
        if(recipe == null) {
            tooltip.add(new TranslatableText("item.pigment.crafting_tablet.tooltip.no_recipe"));
        } else {
            tooltip.add(new TranslatableText("item.pigment.crafting_tablet.tooltip.recipe", recipe.getOutput().getCount(), recipe.getOutput().getName().getString()));
        }

    }


}