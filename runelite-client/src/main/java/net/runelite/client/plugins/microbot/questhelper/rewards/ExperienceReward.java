/*
 * Copyright (c) 2021, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.rewards;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.client.util.QuantityFormatter;

import javax.annotation.Nonnull;
import java.util.Locale;

public class ExperienceReward implements Reward
{
	@Getter
    private final Skill skill;
    private final int experience;
	/**
	 * Set to true if this experience reward is provided in the form of a single-skill XP lamp
	 */
	private final boolean lamp;

    public ExperienceReward(Skill skill, int experience)
    {
		this(skill, experience, false);
    }

	public ExperienceReward(Skill skill, int experience, boolean lamp)
	{
		this.skill = skill;
		this.experience = experience;
		this.lamp = lamp;
	}

    @Nonnull
    @Override
    public RewardType rewardType()
    {
        return RewardType.EXPERIENCE;
    }

    @Nonnull
    @Override
    public String getDisplayText()
    {
		if (lamp) {
			return  QuantityFormatter.formatNumber(experience) + " " + Character.toUpperCase(skill.name().charAt(0)) + skill.name().toLowerCase(Locale.ROOT).substring(1) + " Experience Lamp";
		} else {
			return  QuantityFormatter.formatNumber(experience) + " " + Character.toUpperCase(skill.name().charAt(0)) + skill.name().toLowerCase(Locale.ROOT).substring(1) + " Experience";
		}
    }
}
