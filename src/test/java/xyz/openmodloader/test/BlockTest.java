package xyz.openmodloader.test;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockTest extends Block {
    public BlockTest() {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabs.BREWING);
        this.setUnlocalizedName("test");
    }

    @Override
    public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumHand var5, @Nullable ItemStack var6, EnumFacing var7, float var8, float var9, float var10) {
        var4.addChatMessage(new TextComponentString("You right clicked the block"));
        return true;
    }
}
