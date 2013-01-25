package org.powerbot.game.api.wrappers.graphics.model;

import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.RSInteractable;

public class RenderableModel extends CapturedModel {
	private RSInteractable interactable;

	public RenderableModel(final Model model, final RSInteractable interactable) {
		super(model);
		this.interactable = interactable;
	}

	@Override
	protected int getLocalX() {
		return (int) interactable.getData().getLocation().getX();
	}

	@Override
	protected int getLocalY() {
		return (int) interactable.getData().getLocation().getY();
	}

	@Override
	protected void update() {
	}
}
