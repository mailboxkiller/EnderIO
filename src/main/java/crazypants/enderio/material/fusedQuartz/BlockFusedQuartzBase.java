package crazypants.enderio.material.fusedQuartz;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.machine.painter.blocks.BlockItemPaintedBlock;
import crazypants.enderio.machine.painter.blocks.BlockItemPaintedBlock.INamedSubBlocks;
import crazypants.enderio.render.ISmartRenderAwareBlock;

public abstract class BlockFusedQuartzBase<T extends TileEntityEio> extends BlockEio<T> implements ISmartRenderAwareBlock, INamedSubBlocks {

  public BlockFusedQuartzBase(String name, Class<T> teClass) {
    super(name, teClass, Material.GLASS);
    setSoundType(SoundType.GLASS);
  }
  
  protected ItemBlock createItemBlock() {
    return new BlockItemFusedQuartzBase(this, getName());
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(FusedQuartzType.KIND, FusedQuartzType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return FusedQuartzType.getMetaFromType(state.getValue(FusedQuartzType.KIND));
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity par1Entity, Explosion explosion) {
    if (world.getBlockState(pos).getValue(FusedQuartzType.KIND).isBlastResistant()) {
      return 2000;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    if(bs.getBlock() != this) {
      return super.getLightOpacity(state, world, pos);
    }
    return bs.getValue(FusedQuartzType.KIND).getLightOpacity();
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
    return state.getValue(FusedQuartzType.KIND).isEnlightened() ? 15 : super.getLightValue(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    if (par2CreativeTabs != null) {
      for (FusedQuartzType fqt : FusedQuartzType.values()) {
        par3List.add(new ItemStack(par1, 1, fqt.ordinal()));
      }
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {  
    return true;
  }

  @Override
  public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {  
    if(side == EnumFacing.UP) { //stop drips
      return false;
    }
    return true;
  }

  @Override
  public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Override
  protected boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public String getUnlocalizedName(int meta) {
    FusedQuartzType type = FusedQuartzType.getTypeFromMeta(meta);
    return "enderio.blockFusedQuartz." + type.getUnlocalisedName();
  }

  public static class BlockItemFusedQuartzBase extends BlockItemPaintedBlock {

    public BlockItemFusedQuartzBase(Block block, String name) {
      super(block, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
      super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
      int meta = par1ItemStack.getItemDamage();
      FusedQuartzType type = FusedQuartzType.getTypeFromMeta(meta);
      if (type.isBlastResistant()) {
        par3List.add(EnderIO.lang.localize("blastResistant"));
      }
      if (type.isEnlightened()) {
        par3List.add(EnderIO.lang.localize("lightEmitter"));
      }
      if (type.getLightOpacity() > 0) {
        par3List.add(EnderIO.lang.localize("lightBlocker"));
      }
    }

  }
}