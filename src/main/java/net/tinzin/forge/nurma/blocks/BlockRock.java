package net.tinzin.forge.nurma.blocks;

import net.minecraft.block.material.Material;

public class BlockRock extends BlockBase{

    public BlockRock(String name){
        super(Material.ROCK, name);

        setHardness(3f);
        setResistance(10f);
    }
}
