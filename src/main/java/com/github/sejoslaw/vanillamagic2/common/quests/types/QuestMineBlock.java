package com.github.sejoslaw.vanillamagic2.common.quests.types;

import com.github.sejoslaw.vanillamagic2.common.json.IJsonService;
import com.github.sejoslaw.vanillamagic2.common.quests.Quest;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;
import net.minecraft.block.Block;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestMineBlock extends Quest {
    public List<Block> blocksToMine;

    public void readData(IJsonService jsonService) {
        super.readData(jsonService);

        String blockName = jsonService.getString("blocksToMine").toLowerCase();

        this.blocksToMine = ForgeRegistries.BLOCKS
                .getEntries()
                .stream()
                .filter(entry -> entry.getKey().getLocation().toString().toLowerCase().contains(blockName))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void fillTooltip(List<ITextComponent> lines) {
        super.fillTooltip(lines);

        TextUtils.addLine(lines, "quest.tooltip.blocksToMine", this.blocksToMine.stream().map(this::getTooltip).collect(Collectors.joining(", ")));
    }
}
