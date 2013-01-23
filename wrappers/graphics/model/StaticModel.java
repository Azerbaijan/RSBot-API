package org.powerbot.game.api.wrappers.graphics.model;

import org.powerbot.game.api.wrappers.Verifiable;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.client.Model;

public class StaticModel extends CapturedModel {
	private int x, y, f;
	private Verifiable verifiable;

	public StaticModel(final Model model, final int x, final int y, final int f, final Verifiable verifiable) {
		super(model, verifiable);
		this.x = x;
		this.y = y;
		this.f = f;
		this.verifiable = verifiable;
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
	protected int getPlane() {
		return f;
	}

	@Override
	protected void update() {
	}
}
