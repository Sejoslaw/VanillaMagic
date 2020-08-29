package com.github.sejoslaw.vanillamagic2.common.quests.types.tileentities;

import com.github.sejoslaw.vanillamagic2.common.quests.Quest;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;

import java.util.Collection;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestQuarry extends Quest {
    public void fillTooltip(Collection<String> lines) {
        super.fillTooltip(lines);

        this.addLine(lines, "quest.tooltip.left", TextUtils.getFormattedText("quest.quarry.desc.left"));
        this.addLine(lines, "quest.tooltip.back", TextUtils.getFormattedText("quest.quarry.desc.back"));
        this.addLine(lines, "quest.tooltip.top", TextUtils.getFormattedText("quest.quarry.desc.top"));
        this.addLine(lines, "quest.tooltip.right", TextUtils.getFormattedText("quest.quarry.desc.right"));
    }
}
