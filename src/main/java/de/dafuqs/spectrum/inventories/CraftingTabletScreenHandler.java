package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.enums.SpectrumColor;
import de.dafuqs.spectrum.inventories.slots.LockableCraftingResultSlot;
import de.dafuqs.spectrum.inventories.slots.ReadOnlySlot;
import de.dafuqs.spectrum.items.misc.CraftingTabletItem;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.altar.AltarCraftingRecipe;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Optional;

public class CraftingTabletScreenHandler extends AbstractRecipeScreenHandler<Inventory> {

   private final CraftingTabletInventory craftingInventory;
   private final CraftingResultInventory craftingResultInventory;
   private final ScreenHandlerContext context;
   private final PlayerEntity player;
   private final World world;
   private final ItemStack craftingTabletItemStack;

   private final LockableCraftingResultSlot lockableCraftingResultSlot;

   public CraftingTabletScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, ScreenHandlerContext.EMPTY, null);
   }

   public CraftingTabletScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ItemStack craftingTabletItemStack) {
      super(SpectrumScreenHandlerTypes.CRAFTING_TABLET, syncId);
      this.craftingInventory = new CraftingTabletInventory(this);
      this.craftingResultInventory = new CraftingResultInventory();
      this.context = context;
      this.world = playerInventory.player.getEntityWorld();
      this.craftingTabletItemStack = craftingTabletItemStack;
      this.player = playerInventory.player;

      // crafting slots
      int m;
      int n;
      for(m = 0; m < 3; ++m) {
         for(n = 0; n < 3; ++n) {
            this.addSlot(new Slot(craftingInventory, n + m * 3, 30 + n * 18, 19 + m * 18));
         }
      }

      // spectrum slots
      this.addSlot(new ReadOnlySlot(craftingInventory, 9,  44, 77));
      this.addSlot(new ReadOnlySlot(craftingInventory, 10, 44 + 18, 77));
      this.addSlot(new ReadOnlySlot(craftingInventory, 11, 44 + 2 * 18, 77));
      this.addSlot(new ReadOnlySlot(craftingInventory, 12, 44 + 3 * 18, 77));
      this.addSlot(new ReadOnlySlot(craftingInventory, 13, 44 + 4 * 18, 77));

      // preview slot
      lockableCraftingResultSlot = new LockableCraftingResultSlot(playerInventory.player, craftingInventory, craftingResultInventory, 0, 127, 37);
      this.addSlot(lockableCraftingResultSlot);

      // player inventory
      int l;
      for(l = 0; l < 3; ++l) {
         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 112 + l * 18));
         }
      }

      // player hotbar
      for(l = 0; l < 9; ++l) {
         this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 170));
      }

   }

   protected void updateResult(ScreenHandler handler, World world, PlayerEntity player, CraftingTabletInventory inventory) {
      if (!world.isClient) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;

         inventory.setStack(9, new ItemStack(SpectrumItems.TOPAZ_POWDER, 64));
         inventory.setStack(10, new ItemStack(SpectrumItems.AMETHYST_POWDER, 64));
         inventory.setStack(11, new ItemStack(SpectrumItems.CITRINE_POWDER, 64));
         inventory.setStack(12, new ItemStack(SpectrumItems.ONYX_POWDER, 64));
         inventory.setStack(13, new ItemStack(SpectrumItems.MOONSTONE_POWDER, 64));
         Optional<AltarCraftingRecipe> optionalAltarCraftingRecipe = world.getRecipeManager().getFirstMatch(SpectrumRecipeTypes.ALTAR, inventory, world);
         if(optionalAltarCraftingRecipe.isPresent()) {
            lockableCraftingResultSlot.lock();

            AltarCraftingRecipe altarCraftingRecipe = optionalAltarCraftingRecipe.get();
            ItemStack itemStack = altarCraftingRecipe.getOutput().copy();
            craftingResultInventory.setStack(0, itemStack);

            int magenta = altarCraftingRecipe.getSpectrumColor(SpectrumColor.CYAN);
            if(magenta > 0) {
               inventory.setStack(9, new ItemStack(SpectrumItems.TOPAZ_POWDER, magenta));
            } else {
               inventory.setStack(9, ItemStack.EMPTY);
            }
            int yellow = altarCraftingRecipe.getSpectrumColor(SpectrumColor.MAGENTA);
            if(yellow > 0) {
               inventory.setStack(10, new ItemStack(SpectrumItems.AMETHYST_POWDER, yellow));
            } else {
               inventory.setStack(10, ItemStack.EMPTY);
            }
            int cyan = altarCraftingRecipe.getSpectrumColor(SpectrumColor.YELLOW);
            if(cyan > 0) {
               inventory.setStack(11, new ItemStack(SpectrumItems.CITRINE_POWDER, cyan));
            } else {
               inventory.setStack(11, ItemStack.EMPTY);
            }
            int black = altarCraftingRecipe.getSpectrumColor(SpectrumColor.BLACK);
            if(magenta > 0) {
               inventory.setStack(12, new ItemStack(SpectrumItems.ONYX_POWDER, black));
            } else {
               inventory.setStack(12, ItemStack.EMPTY);
            }
            int white = altarCraftingRecipe.getSpectrumColor(SpectrumColor.WHITE);
            if(white > 0) {
               inventory.setStack(13, new ItemStack(SpectrumItems.MOONSTONE_POWDER, white));
            } else {
               inventory.setStack(13, ItemStack.EMPTY);
            }

            handler.setPreviousTrackedSlot(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));

            CraftingTabletItem.setStoredRecipe(craftingTabletItemStack, optionalAltarCraftingRecipe.get());
         } else {
            inventory.setStack(9, ItemStack.EMPTY);
            inventory.setStack(10, ItemStack.EMPTY);
            inventory.setStack(11, ItemStack.EMPTY);
            inventory.setStack(12, ItemStack.EMPTY);
            inventory.setStack(13, ItemStack.EMPTY);

            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optionalCraftingRecipe = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world);
            if (optionalCraftingRecipe.isPresent()) {
               lockableCraftingResultSlot.unlock();

               CraftingRecipe craftingRecipe = optionalCraftingRecipe.get();
               if (craftingResultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe)) {
                  itemStack = craftingRecipe.craft(craftingInventory);
               }

               CraftingTabletItem.setStoredRecipe(craftingTabletItemStack, optionalCraftingRecipe.get());
            } else {
               CraftingTabletItem.clearStoredRecipe(craftingTabletItemStack);
            }

            craftingResultInventory.setStack(0, itemStack);
            handler.setPreviousTrackedSlot(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
         }
      }
   }

   public void onContentChanged(Inventory inventory) {
      this.context.run((world, pos) -> {
         updateResult(this, world, this.player, this.craftingInventory);
      });
   }

   public void populateRecipeFinder(RecipeMatcher recipeMatcher) {
      if (this.craftingInventory != null) {
         this.craftingInventory.provideRecipeInputs(recipeMatcher);
      }
   }

   public void clearCraftingSlots() {
      this.craftingInventory.clear();
   }

   public boolean matches(Recipe<? super Inventory> recipe) {
      return recipe.matches(this.craftingInventory, this.world);
   }

   public void close(PlayerEntity playerEntity) {
      for(int i = 0; i < 9; i++) {
         ItemStack itemStack = this.craftingInventory.getStack(i);

         if(!itemStack.isEmpty()) {
            boolean insertInventorySuccess = playerEntity.getInventory().insertStack(itemStack);
            ItemEntity itemEntity;
            if (insertInventorySuccess && itemStack.isEmpty()) {
               itemStack.setCount(1);
               itemEntity = playerEntity.dropItem(itemStack, false);
               if (itemEntity != null) {
                  itemEntity.setDespawnImmediately();
               }

               playerEntity.world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               playerEntity.currentScreenHandler.sendContentUpdates();
            } else {
               itemEntity = playerEntity.dropItem(itemStack, false);
               if (itemEntity != null) {
                  itemEntity.resetPickupDelay();
                  itemEntity.setOwner(playerEntity.getUuid());
               }
            }
         }
      }
      super.close(player);
   }

   public boolean canUse(PlayerEntity player) {
      return true;
   }

   public ItemStack transferSlot(PlayerEntity player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot.hasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            this.context.run((world, pos) -> {
               itemStack2.getItem().onCraft(itemStack2, world, player);
            });
            if (!this.insertItem(itemStack2, 10, 46, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickTransfer(itemStack2, itemStack);
         } else if (index >= 10 && index < 46) {
            if (!this.insertItem(itemStack2, 1, 10, false)) {
               if (index < 37) {
                  if (!this.insertItem(itemStack2, 37, 46, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.insertItem(itemStack2, 10, 37, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.insertItem(itemStack2, 10, 46, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
         } else {
            slot.markDirty();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTakeItem(player, itemStack2);
         if (index == 0) {
            player.dropItem(itemStack2, false);
         }
      }

      return itemStack;
   }

   public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
      return super.canInsertIntoSlot(stack, slot);
   }

   public int getCraftingResultSlotIndex() {
      return 14;
   }

   public int getCraftingWidth() {
      return 3;
   }

   public int getCraftingHeight() {
      return 3;
   }

   public int getCraftingSlotCount() {
      return 9;
   }

   @Override
   public RecipeBookCategory getCategory() {
      return RecipeBookCategory.CRAFTING;
   }

   public boolean canInsertIntoSlot(int index) {
      return index != this.getCraftingResultSlotIndex();
   }
}
