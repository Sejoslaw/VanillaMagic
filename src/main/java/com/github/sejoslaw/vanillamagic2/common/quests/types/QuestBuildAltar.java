package com.github.sejoslaw.vanillamagic2.common.quests.types;

import com.github.sejoslaw.vanillamagic2.common.json.IJsonService;
import com.github.sejoslaw.vanillamagic2.common.quests.Quest;
import com.github.sejoslaw.vanillamagic2.common.utils.AltarUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.BlockUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.ItemStackUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestBuildAltar extends Quest implements Comparable<QuestBuildAltar> {
    public void readData(IJsonService jsonService) {
        super.readData(jsonService);

        ItemStack altarBlockStack = ItemStackUtils.getItemStackFromJson(jsonService.getItemStack("altarBlock"));
        Block altarBlock = BlockUtils.getBlockFromItem(altarBlockStack.getItem());
        AltarUtils.BLOCKS.put(this.altarTier, altarBlock);
    }

    public void fillTooltip(List<ITextComponent> lines) {
        super.fillTooltip(lines);

        TextUtils.addLine(lines, "quest.tooltip.altarBlock", this.getTooltip(AltarUtils.BLOCKS.get(this.altarTier)));
        TextUtils.addLine(lines, "quest.tooltip.altarBlock.position", this.getAltarBlockPositionTooltip());
        TextUtils.addLine(lines, "quest.tooltip.altarBlock.distance", String.valueOf(this.altarTier));
    }

    public int compareTo(QuestBuildAltar quest) {
        return Integer.compare(this.altarTier, quest.altarTier);
    }

    private String getAltarBlockPositionTooltip() {
        return this.altarTier % 2 == 1 ?
                TextUtils.getFormattedText("quest.tooltip.altarBlock.position.side") :
                TextUtils.getFormattedText("quest.tooltip.altarBlock.position.corner");
    }
}
