package org.powerbot.game.api.wrappers.graphics.model;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.interactive.Character;
import org.powerbot.game.client.Model;

public class CharacterModel extends CapturedModel {
	private final Character character;
	private final int[] x_base, z_base;

	public CharacterModel(final Model model, final org.powerbot.game.api.wrappers.interactive.Character character) {
		super(model);
		this.character = character;
		x_base = xPoints;
		z_base = zPoints;
		xPoints = new int[numVertices];
		zPoints = new int[numVertices];
	}

	@Override
	protected int getLocalX() {
		final RegionOffset ref = character.getRegionOffset();
		return ref.getX();
	}

	@Override
	protected int getLocalY() {
		final RegionOffset ref = character.getRegionOffset();
		return ref.getY();
	}

	@Override
	protected void update() {
		final int theta = character.getRotation() & 0x3fff;
		final int sin = Calculations.SIN_TABLE[theta];
		final int cos = Calculations.COS_TABLE[theta];
		for (int i = 0; i < numVertices; ++i) {
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 15;
			zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 15;
		}
	}
}
