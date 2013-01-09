package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSHintArrow;

/**
 * @author Timer
 */
public class HintArrow implements Verifiable, Locatable {
	private final RSHintArrow arrow;
	private final Tile base;

	public HintArrow(final RSHintArrow arrow) {
		this.arrow = arrow;
		this.base = Game.getMapBase();
	}

	public int getPlane() {
		return arrow.getPlane();
	}

	public int getType() {
		return arrow.getType();
	}

	public int getTargetId() {
		return arrow.getTargetID();
	}

	@Override
	public RegionOffset getRegionOffset() {
		return new RegionOffset(arrow.getX() >> 9, arrow.getY() >> 9, getPlane());
	}

	@Override
	public Tile getLocation() {
		return new Tile(base.getX() + (arrow.getX() >> 9), base.getY() + (arrow.getY() >> 9), getPlane());
	}

	@Override
	public boolean validate() {
		final RSHintArrow[] arr = Context.client().getRSHintArrows();
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow == this.arrow) return true;
		}
		return false;
	}
}
