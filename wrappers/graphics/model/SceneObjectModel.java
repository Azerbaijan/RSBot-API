package org.powerbot.game.api.wrappers.graphics.model;

import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.RSInteractable;
import org.powerbot.game.client.RSInteractableData;
import org.powerbot.game.client.RSObject;

public class SceneObjectModel extends CapturedModel {
	private final RSObject instance;

	public SceneObjectModel(final Model model, final SceneObject location) {
		super(model);
		this.instance = location.getInstance();
	}

	@Override
	protected int getLocalX() {
		final RSInteractableData data = ((RSInteractable) instance).getData();
		return (int) data.getLocation().getX();
	}

	@Override
	protected int getLocalY() {
		final RSInteractableData data = ((RSInteractable) instance).getData();
		return (int) data.getLocation().getY();
	}

	@Override
	protected void update() {
	}
}
