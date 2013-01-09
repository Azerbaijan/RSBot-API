package org.powerbot.game.api.wrappers;

public interface Locatable {
	@Deprecated
	public RegionOffset getRegionOffset();

	public Tile getLocation();
}
