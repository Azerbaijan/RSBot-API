package org.powerbot.game.api.wrappers.graphics.model;

import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.client.Model;

public class StaticModel extends CapturedModel {
	private int x, y;

	public StaticModel(final Model model, final int x, final int y) {
		super(model);
		this.x = x;
		this.y = y;
	}

	@Override
	protected int getLocalX() {
		return x;
	}

	@Override
	protected int getLocalY() {
		return y;
	}

	@Override
	protected void update() {
	}
}
