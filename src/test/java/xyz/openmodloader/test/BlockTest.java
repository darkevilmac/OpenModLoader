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
import net.minecraft.world.World;
import xyz.openmodloader.gui.GUIContext;

public class BlockTest extends Block {
    public BlockTest() {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabs.BREWING);
        this.setUnlocalizedName("test");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        player.openGUI("omltest", "test", new GUIContext().set("pos", pos).set("inv", player.inventory));
        return true;
    }
}
