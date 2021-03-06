package com.github.sejoslaw.vanillamagic2.common.quests.types;

import com.github.sejoslaw.vanillamagic2.common.quests.Quest;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestCrystallizedPotion extends Quest {
    public void fillTooltip(List<ITextComponent> lines) {
        super.fillTooltip(lines);

        TextUtils.addLine(lines, "quest.tooltip.ingredients", TextUtils.getFormattedText("quest.crystallizedPotion.desc.ingredients"));
        TextUtils.addLine(lines, "quest.tooltip.results", TextUtils.getFormattedText("quest.crystallizedPotion.desc.results"));
        TextUtils.addLine(lines, "quest.tooltip.usage", TextUtils.getFormattedText("quest.crystallizedPotion.desc.usage"));
    }
}
