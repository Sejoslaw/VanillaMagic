package com.github.sejoslaw.vanillamagic2.common.tileentities.machines;

import com.github.sejoslaw.vanillamagic2.common.registries.MachineModuleRegistry;
import com.github.sejoslaw.vanillamagic2.common.registries.TileEntityRegistry;
import com.github.sejoslaw.vanillamagic2.common.tileentities.VMTileEntity;
import com.github.sejoslaw.vanillamagic2.common.utils.NbtUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class VMTileMachine extends VMTileEntity implements IVMTileMachine {
    private String moduleKey;

    public VMTileMachine() {
        super(TileEntityRegistry.MACHINE.get());
    }

    public boolean setModuleKey(String key) {
        this.moduleKey = key;

         return MachineModuleRegistry.getDefaultModulesStream().allMatch(module -> module.setup(this)) &&
                MachineModuleRegistry.getModulesStream(this.moduleKey).allMatch(machine -> machine.setup(this));
    }

    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.putString(NbtUtils.NBT_MACHINE_MODULE_KEY, this.moduleKey);
        return nbt;
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.setModuleKey(nbt.getString(NbtUtils.NBT_MACHINE_MODULE_KEY));
    }

    public void tickTileEntity() {
        if (MachineModuleRegistry.getDefaultModulesStream().allMatch(module -> module.canExecute(this))) {
            MachineModuleRegistry.forEachDefaultModule(module -> module.execute(this));
        } else {
            return;
        }

        if (MachineModuleRegistry.getModulesStream(this.moduleKey).allMatch(machine -> machine.canExecute(this))) {
            MachineModuleRegistry.forEachModules(this.moduleKey, machine -> machine.execute(this));
        }
    }

    public void addInformation(List<ITextComponent> lines) {
        lines.add(TextUtils.buildServerSideMessage("vm.tooltip.tile.machine.type", "quest." + this.moduleKey));
    }
}
