package eu.pb4.sgui.virtual;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class VirtualInventory implements Inventory {
    private final SimpleGui gui;

    VirtualInventory(SimpleGui gui) {
        this.gui = gui;
    }

    @Override
    public int size() {
        return this.gui.getSize();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int index) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.getStack();
        } else {
            GuiElementInterface element = this.gui.getSlot(index);
            if (element == null) {
                return ItemStack.EMPTY;
            }
            return element.getItemStackInternalUseOnly();
        }
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.inventory.removeStack(index, count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int index) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            return slot.inventory.removeStack(index);
        }
        return ItemStack.EMPTY;
    }


    @Override
    public void setStack(int index, ItemStack stack) {
        Slot slot = this.gui.getSlotRedirect(index);
        if (slot != null) {
            slot.inventory.setStack(index, stack);
        }
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
    }
}
