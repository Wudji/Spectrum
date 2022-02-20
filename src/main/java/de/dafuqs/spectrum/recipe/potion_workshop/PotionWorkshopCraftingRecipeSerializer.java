package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

public class PotionWorkshopCraftingRecipeSerializer<T extends PotionWorkshopCraftingRecipe> implements RecipeSerializer<T> {

	public final PotionWorkshopCraftingRecipeSerializer.RecipeFactory<T> recipeFactory;

	public PotionWorkshopCraftingRecipeSerializer(PotionWorkshopCraftingRecipeSerializer.RecipeFactory<T> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public T read(Identifier identifier, JsonObject jsonObject) {
		String group = JsonHelper.getString(jsonObject, "group", "");
		
		Ingredient baseIngredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base_ingredient"));
		Ingredient ingredient1 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient1"));
		Ingredient ingredient2;
		if(JsonHelper.hasJsonObject(jsonObject, "ingredient2")) {
			ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient2"));
		} else {
			ingredient2 = Ingredient.EMPTY;
		}
		Ingredient ingredient3;
		if(JsonHelper.hasJsonObject(jsonObject, "ingredient3")) {
			ingredient3 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient3"));
		} else {
			ingredient3 = Ingredient.EMPTY;
		}
		
		int craftingTime = JsonHelper.getInt(jsonObject, "time", 200);
		boolean consumeBaseIngredient = JsonHelper.getBoolean(jsonObject, "use_up_base_ingredient", false);
		ItemStack output = RecipeUtils.outputWithNbtFromJson(JsonHelper.getObject(jsonObject, "result"));
		
		Identifier requiredAdvancementIdentifier;
		if(JsonHelper.hasString(jsonObject, "required_advancement")) {
			requiredAdvancementIdentifier = Identifier.tryParse(JsonHelper.getString(jsonObject, "required_advancement"));
		} else {
			SpectrumCommon.log(Level.WARN, "Potion Workshop Brewing Recipe " + identifier + " has no unlock advancement set. Will be set to the unlock pos of the Potion Workshop itself");
			requiredAdvancementIdentifier = new Identifier(SpectrumCommon.MOD_ID, "progression/unlock_potion_workshop");
		}

		return this.recipeFactory.create(identifier, group, baseIngredient, consumeBaseIngredient, ingredient1, ingredient2, ingredient3, output, craftingTime, requiredAdvancementIdentifier);
	}
	
	@Override
	public void write(PacketByteBuf packetByteBuf, T potionWorkshopCraftingRecipe) {
		packetByteBuf.writeString(potionWorkshopCraftingRecipe.group);
		potionWorkshopCraftingRecipe.baseIngredient.write(packetByteBuf);
		packetByteBuf.writeBoolean(potionWorkshopCraftingRecipe.consumeBaseIngredient);
		potionWorkshopCraftingRecipe.ingredient1.write(packetByteBuf);
		potionWorkshopCraftingRecipe.ingredient2.write(packetByteBuf);
		potionWorkshopCraftingRecipe.ingredient3.write(packetByteBuf);
		packetByteBuf.writeItemStack(potionWorkshopCraftingRecipe.output);
		packetByteBuf.writeInt(potionWorkshopCraftingRecipe.craftingTime);
		packetByteBuf.writeIdentifier(potionWorkshopCraftingRecipe.requiredAdvancementIdentifier);
	}
	
	@Override
	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		String group = packetByteBuf.readString();
		Ingredient baseIngredient = Ingredient.fromPacket(packetByteBuf);
		boolean consumeBaseIngredient = packetByteBuf.readBoolean();
		Ingredient ingredient1 = Ingredient.fromPacket(packetByteBuf);
		Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
		Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
		ItemStack output = packetByteBuf.readItemStack();
		int craftingTime = packetByteBuf.readInt();
		Identifier requiredAdvancementIdentifier = packetByteBuf.readIdentifier();
		return this.recipeFactory.create(identifier, group, baseIngredient, consumeBaseIngredient, ingredient1, ingredient2, ingredient3, output, craftingTime, requiredAdvancementIdentifier);
	}
	
	public interface RecipeFactory<T extends PotionWorkshopCraftingRecipe> {
		T create(Identifier id, String group, Ingredient baseIngredient, boolean consumeBaseIngredient, Ingredient ingredient1, Ingredient ingredient2, Ingredient ingredient3, ItemStack output, int craftingTime, Identifier requiredAdvancementIdentifier);
	}

}