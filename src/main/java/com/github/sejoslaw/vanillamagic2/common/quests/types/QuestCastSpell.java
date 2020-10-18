package com.github.sejoslaw.vanillamagic2.common.quests.types;

import com.github.sejoslaw.vanillamagic2.common.json.IJsonService;
import com.github.sejoslaw.vanillamagic2.common.quests.Quest;
import com.github.sejoslaw.vanillamagic2.common.registries.SpellRegistry;
import com.github.sejoslaw.vanillamagic2.common.spells.Spell;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestCastSpell extends Quest {
    protected Spell spell;

    public void readData(IJsonService jsonService) {
        super.readData(jsonService);

        this.spell = SpellRegistry.SPELLS.get(jsonService.getString("uniqueName"));

        if (this.iconStack == null) {
            this.iconStack = this.leftHandStack;
        }
    }

    public Spell getSpell() {
        return this.spell;
    }
}
