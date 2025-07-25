/*
 * Copyright (c) 2024, Zoinkwiz
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.whileguthixsleeps;

import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.microbot.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.*;

public class HerblorePuzzle extends ConditionalStep
{
	private final ObjectStep[] steps = new ObjectStep[9];
	private final Conditions[] conditions = new Conditions[9];

	private ObjectStep placeDolmens;

	private static final int BASE_VARBIT = VarbitID.WGS_TEMPLE_STATUE_1;
	private static final int BASE_USED_ITEM_VARBIT = VarbitID.LEAGUE_DAG_SUPREME_NO_LEAVE_CHECK;
	// Used attack potion guam, 10868 0->1
	// hunter, 10924 0->1->3

	// Placed guam:
	// 10868 0->1
	// Placed eye of newt:
	// 10868 1->35
	// 1 -> 100011
	// newt/???/???/guam???

	private static final String START_TEXT = "Use the ";
	private static final String MIDDLE_TEXT = " potion ingredients on the statue up the ";
	private static final String END_TEXT = " You can get ingredients by using your druid pouch on the druid spirits around the area.";

	private boolean haveSetupSteps;

	public HerblorePuzzle(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);
		setupSteps();
	}

	private void setupSteps()
	{
		ItemRequirement dolmens = new ItemRequirement("Dolmen", ItemID.WGS_HENGE_PIECE_AGILITY);
		dolmens.addAlternates(ItemID.WGS_HENGE_PIECE_ENERGY, ItemID.WGS_HENGE_PIECE_RESTORE, ItemID.WGS_HENGE_PIECE_ATTACK, ItemID.WGS_HENGE_PIECE_STRENGTH, ItemID.WGS_HENGE_PIECE_DEFENCE, ItemID.WGS_HENGE_PIECE_COMBAT,
			ItemID.WGS_HENGE_PIECE_RANGED, ItemID.WGS_HENGE_PIECE_PRAYER, ItemID.WGS_HENGE_PIECE_HUNTING, ItemID.WGS_HENGE_PIECE_FISHING, ItemID.WGS_HENGE_PIECE_MAGIC, ItemID.WGS_HENGE_PIECE_BALANCE);

		placeDolmens = new ObjectStep(questHelper, ObjectID.LUC2_GT_HENGE_TABLE_MULTI, new WorldPoint(4076, 4437, 0), "Use all the dolmens on the stone table in the middle of the area.", dolmens.highlighted());
		steps[8] = placeDolmens;

		int[] HERB_ORDER = new int[]{3, 0, 6, 1, 7, 4, 2, 5};
		for (int i : HERB_ORDER)
		{
			conditions[i] = not(createBaseRequirement(i));
		}

		Requirement placedAllIngredients = and(
			// is 3 or 35
			not(conditions[0]),
			not(conditions[1]),
			not(conditions[2]),
			not(conditions[3]),
			not(conditions[4]),
			not(conditions[5]),
			not(conditions[6]),
			not(conditions[7])
		);

		this.addStep(placedAllIngredients, placeDolmens);

		for (int i : HERB_ORDER)
		{
			steps[i] = new ObjectStep(questHelper, 0, "Unknown state.");
			this.addStep(conditions[i], steps[i]);
		}
	}

	private Conditions createBaseRequirement(int i)
	{
		return or(new VarbitRequirement(StatueLocation.values()[i].getVarbitID(), 3),
			new VarbitRequirement(StatueLocation.values()[i].getVarbitID(), 35));
	}

	@Override
	protected void updateSteps()
	{
		if (!haveSetupSteps) setup();
		super.updateSteps();
	}

	private void setup()
	{
		if (client.getVarbitValue(BASE_VARBIT) == 0) return;
		haveSetupSteps = true;

		for (int i = 0; i < 8; i++)
		{
			int statueTypeIndex = client.getVarbitValue(BASE_VARBIT + i) - 1;
			DolmenType dolmenType = DolmenType.values()[statueTypeIndex];
			StatueLocation statueLocation = StatueLocation.values()[i];

			String text = START_TEXT + dolmenType.name().toLowerCase() + MIDDLE_TEXT + statueLocation.getDirectionText() + END_TEXT;

			if (dolmenType.getCompleteState() != -1)
			{
				conditions[i].getConditions().clear();
				conditions[i].getConditions().add(
					new VarbitRequirement(StatueLocation.values()[i].getVarbitID(), dolmenType.getCompleteState()));
			}
			steps[i].setLinePoints(statueLocation.getPath());
			steps[i].setWorldPoint(statueLocation.getLocation());
			steps[i].addAlternateObjects(dolmenType.getObjectID(), dolmenType.getObjectID() + 1);
			steps[i].addRequirement(dolmenType.getItemRequirements());
			steps[i].setText(text);
		}
	}

	public QuestStep[] getSidebarSteps()
	{
		return steps;

	}
}
