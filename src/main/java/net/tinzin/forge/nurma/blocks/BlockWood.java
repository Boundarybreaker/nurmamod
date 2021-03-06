package net.tinzin.forge.nurma.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.tinzin.forge.nurma.Nurma;
import net.tinzin.forge.nurma.blocks.fluids.BlockCrystalWater;
import net.tinzin.forge.nurma.items.ModItems;

import java.util.Random;

public class BlockWood extends BlockRotatedPillar implements IBlockBase {
    protected String name;

    private EnumFacing NO = EnumFacing.DOWN; // this represents the inability to grow a branch, obviously

    private int maxHeight = 11;
    private int minBranchHeight = 4;
    private int maxBranchHeight = 10;

    public BlockWood(String name){
        super(Material.IRON);

        this.name = name;

        setUnlocalizedName(Nurma.modId+"."+name);
        setRegistryName(name);

        this.setLightLevel(3/15f);
        this.setLightOpacity(6);
        this.setHardness(3f);
        this.setResistance(25f);
        this.setSoundType(SoundType.GLASS);
        this.setCreativeTab(Nurma.creativeTab);
    }

    public void registerItemModel(Item itemBlock) {
        Nurma.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    public Block toBlock(){
        return this;
    }

    public boolean canGrowUp(IBlockState state, IBlockAccess world, BlockPos pos){
        if (state.getValue(AXIS).isHorizontal()){ return false; }

        boolean reachedBottom = false;
        BlockPos looking = pos;
        int height = 0;
        while (!reachedBottom){
            height++;
            looking = looking.down();
            if(!(world.getBlockState(looking).getBlock() instanceof BlockWood)){
                looking = looking.up();
                reachedBottom = true;
            }
        }

        BlockPos[] surrounding = {
                looking.north(),
                looking.north().east(),
                looking.east(),
                looking.east().south(),
                looking.south(),
                looking.south().west(),
                looking.west(),
                looking.west().north()
        };
        for(BlockPos p : surrounding) {
            if(!(world.getBlockState(p).getBlock() instanceof BlockCrystalWater)){
                return false;
            }
        }

        if(height < maxHeight){ return true; }
        return false;
    }

    public EnumFacing branchDir(IBlockState state, IBlockAccess world, BlockPos pos){
        return NO;
    }



    protected boolean isValidPos(IBlockAccess world, BlockPos pos, IBlockState state){
        if(state.getValue(AXIS).isHorizontal()){
            //TODO actually check for validity of branches, for now just say they're all fine
            return true;
        }
        BlockPos below = pos.down();
        IBlockState stateBelow = world.getBlockState(below);
        if(stateBelow.getBlock() instanceof  BlockWood){
            BlockWood woodBelow = (BlockWood)stateBelow.getBlock();
            return woodBelow.canGrowUp(stateBelow,world,below);
        } else if(stateBelow.getBlock() instanceof BlockAir){
            return false;
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.isValidPos(worldIn, pos, state))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public int quantityDropped(Random random)
    {
        return random.nextInt(4)+1;  //drops 1-4 crystals
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.crystal;
    }
}
