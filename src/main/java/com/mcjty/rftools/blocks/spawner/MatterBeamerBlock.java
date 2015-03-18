package com.mcjty.rftools.blocks.spawner;

import com.mcjty.container.GenericContainerBlock;
import com.mcjty.container.WrenchUsage;
import com.mcjty.rftools.RFTools;
import com.mcjty.rftools.blocks.Infusable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MatterBeamerBlock extends GenericContainerBlock implements Infusable {

    private IIcon iconSideOn;

    public MatterBeamerBlock() {
        super(Material.iron, MatterBeamerTileEntity.class);
        setBlockName("matterBeamerBlock");
        setCreativeTab(RFTools.tabRfTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
//        NBTTagCompound tagCompound = itemStack.getTagCompound();
//        if (tagCompound != null) {
//            String name = tagCompound.getString("tpName");
//            int id = tagCompound.getInteger("destinationId");
//            list.add(EnumChatFormatting.GREEN + "Name: " + name + (id == -1 ? "" : (", Id: " + id)));
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This block converts matter into a beam");
            list.add(EnumChatFormatting.WHITE + "of energy. It can then send that beam to");
            list.add(EnumChatFormatting.WHITE + "a connected spawner. Connect by using a wrench.");
            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: reduced power usage and speed.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_MATTER_BEAMER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        MatterBeamerTileEntity beamerTileEntity = (MatterBeamerTileEntity) tileEntity;
        MatterBeamerContainer beamerContainer = new MatterBeamerContainer(entityPlayer, beamerTileEntity);
        return new GuiMatterBeamer(beamerTileEntity, beamerContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new MatterBeamerContainer(entityPlayer, (MatterBeamerTileEntity) tileEntity);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sx, float sy, float sz) {
        WrenchUsage wrenchUsed = testWrenchUsage(x, y, z, player);
        if (wrenchUsed == WrenchUsage.NORMAL) {
            if (world.isRemote) {
                MatterBeamerTileEntity matterBeamerTileEntity = (MatterBeamerTileEntity) world.getTileEntity(x, y, z);
                world.playSound(x, y, z, "note.pling", 1.0f, 1.0f, false);
                matterBeamerTileEntity.useWrench(player);
            }
            return true;
        } else if (wrenchUsed == WrenchUsage.SNEAKING) {
            breakAndRemember(world, x, y, z);
            return true;
        } else {
            return openGui(world, x, y, z, player);
        }
    }


    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconSide = iconRegister.registerIcon(RFTools.MODID + ":" + "machineBeamerOff");
        iconSideOn = iconRegister.registerIcon(RFTools.MODID + ":" + "machineBeamer");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (meta > 0) {
            return iconSideOn;
        } else {
            return iconSide;
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return iconSide;
    }

}
