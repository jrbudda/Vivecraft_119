package org.vivecraft.utils;

import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import org.vivecraft.api.NetworkHelper;
import org.vivecraft.api.ServerVivePlayer;
import org.vivecraft.api.VRData;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ASMInjections
{
    private static Random random = new Random();

    public static boolean containerCreativeMouseDown(int eatTheStack)
    {
        return false;
    }

    public static void addCreativeItems(CreativeModeTab tab, NonNullList<ItemStack> list)
    {
        if (tab == CreativeModeTab.TAB_FOOD || tab == null)
        {
            ItemStack itemstack = (new ItemStack(Items.PUMPKIN_PIE)).setHoverName(Component.literal("EAT ME"));
            ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER).setHoverName(Component.literal("DRINK ME"));
            itemstack1.getTag().putInt("HideFlags", 32);
            list.add(itemstack);
            list.add(itemstack1);
        }

        if (tab == CreativeModeTab.TAB_TOOLS || tab == null)
        {
            ItemStack itemstack3 = (new ItemStack(Items.LEATHER_BOOTS)).setHoverName(Component.translatable("vivecraft.item.jumpboots"));
            itemstack3.getTag().putBoolean("Unbreakable", true);
            itemstack3.getTag().putInt("HideFlags", 4);
            ItemStack itemstack4 = (new ItemStack(Items.SHEARS)).setHoverName(Component.translatable("vivecraft.item.climbclaws"));
            itemstack4.getTag().putBoolean("Unbreakable", true);
            itemstack4.getTag().putInt("HideFlags", 4);
            list.add(itemstack3);
            list.add(itemstack4);
        }
    }

    public static void addCreativeSearch(String query, NonNullList<ItemStack> list)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        addCreativeItems((CreativeModeTab)null, nonnulllist);

        for (ItemStack itemstack : nonnulllist)
        {
            if (query.isEmpty() || itemstack.getHoverName().toString().toLowerCase().contains(query.toLowerCase()))
            {
                list.add(itemstack);
            }
        }
    }

    public static float itemRayTracePitch(Player player, float orig)
    {
        if (player instanceof LocalPlayer)
        {
            VRData.VRDevicePose vrdata$vrdevicepose = Minecraft.getInstance().vrPlayer.vrdata_world_pre.getController(0);
            Vec3 vec3 = vrdata$vrdevicepose.getDirection();
            return (float)Math.toDegrees(Math.asin(-vec3.y / vec3.length()));
        }
        else
        {
            return orig;
        }
    }

    public static float itemRayTraceYaw(Player player, float orig)
    {
        if (player instanceof LocalPlayer)
        {
            VRData.VRDevicePose vrdata$vrdevicepose = Minecraft.getInstance().vrPlayer.vrdata_world_pre.getController(0);
            Vec3 vec3 = vrdata$vrdevicepose.getDirection();
            return (float)Math.toDegrees(Math.atan2(-vec3.x, vec3.z));
        }
        else
        {
            return orig;
        }
    }

    public static Vec3 itemRayTracePos(Player player, Vec3 orig)
    {
        if (player instanceof LocalPlayer)
        {
            VRData.VRDevicePose vrdata$vrdevicepose = Minecraft.getInstance().vrPlayer.vrdata_world_pre.getController(0);
            return vrdata$vrdevicepose.getPosition();
        }
        else
        {
            return orig;
        }
    }

    public static void activateFun(ServerPlayer serverPlayer) {
        ServerVivePlayer serverVivePlayer = NetworkHelper.vivePlayers.get(serverPlayer.getUUID());

        if (!Minecraft.getInstance().vrSettings.disableFun && serverVivePlayer != null && serverVivePlayer.isVR() && random.nextInt(40) == 3)
        {
            ItemStack itemstack;

            if (random.nextInt(2) == 1)
            {
                itemstack = (new ItemStack(Items.PUMPKIN_PIE)).setHoverName(Component.literal("EAT ME"));
            }
            else
            {
                itemstack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER).setHoverName(Component.literal("DRINK ME"));
            }

            itemstack.getTag().putInt("HideFlags", 32);

            if (serverPlayer.getInventory().add(itemstack))
            {
                serverPlayer.inventoryMenu.broadcastChanges();
            }
        }
    }

    public static void adjustItemThrow(ServerPlayer serverPlayer, ItemEntity itemEntity, boolean pDropAround) {
        ServerVivePlayer serverVivePlayer = NetworkHelper.vivePlayers.get(serverPlayer.getUUID());

        if (serverVivePlayer != null && serverVivePlayer.isVR() && !pDropAround && itemEntity != null)
        {
            Vec3 vec3 = serverVivePlayer.getControllerPos(0, serverPlayer);
            Vec3 vec31 = serverVivePlayer.getControllerDir(0);
            float f = 0.3F;
            itemEntity.setDeltaMovement(vec31.x * (double)f, vec31.y * (double)f, vec31.z * (double)f);
            itemEntity.setPos(vec3.x() + itemEntity.getDeltaMovement().x(), vec3.y() + itemEntity.getDeltaMovement().y(), vec3.z() + itemEntity.getDeltaMovement().z());
        }
    }

    public static void injectItems(Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> map, ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder) {
        //VIVECRAFT - This prolly cant stay here. Move to .json files someday.
        Consumer<Recipe<?>> putRecipe = recipe -> {
            map.computeIfAbsent(recipe.getType(), key -> {
                return ImmutableMap.builder();
            }).put(recipe.getId(), recipe);
            builder.put(recipe.getId(), recipe);
        };
    	ItemStack is = new ItemStack(Items.LEATHER_BOOTS);
        is.setHoverName(Component.translatable("vivecraft.item.jumpboots"));
        is.getOrCreateTag().putBoolean("Unbreakable", true);
        is.getOrCreateTag().putInt("HideFlags",4);

        ItemStack is2 = new ItemStack(Items.SHEARS);
        is2.setHoverName(Component.translatable("vivecraft.item.climbclaws"));
        is2.getOrCreateTag().putBoolean("Unbreakable", true);
        is2.getOrCreateTag().putInt("HideFlags",4);

        ShapedRecipe boot = new ShapedRecipe(new ResourceLocation("jumpboots"),"Vivecraft", 1, 2, NonNullList.a(Ingredient.EMPTY,Ingredient.a(Items.LEATHER_BOOTS), Ingredient.a(new ItemStack(Blocks.SLIME_BLOCK))), is);
        ShapedRecipe claw = new ShapedRecipe(new ResourceLocation("climbclaws"),"Vivecraft", 3, 2, NonNullList.a(Ingredient.EMPTY,Ingredient.a(Items.SPIDER_EYE),Ingredient.EMPTY,Ingredient.a(Items.SPIDER_EYE),Ingredient.a(Items.SHEARS),Ingredient.EMPTY,Ingredient.a(Items.SHEARS)), is2);

        putRecipe.accept(boot);
        putRecipe.accept(claw);
    }

    public static boolean checkEatMe(boolean canEat, ItemStack itemStack) {
        return canEat || itemStack.getHoverName().getString().equals("EAT ME");
    }

    public static void dummy(float f)
    {
    }
}
