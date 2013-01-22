package org.powerbot.game.api.wrappers.interactive;

import java.lang.ref.WeakReference;

import org.powerbot.game.client.RSPlayer;
import org.powerbot.game.client.RSPlayerComposite;

/**
 * @author Timer
 */
public class Player extends Character {
	private final WeakReference<RSPlayer> p;

	public Player(final RSPlayer p) {
		this.p = new WeakReference<>(p);
	}

	public int getLevel() {
		final RSPlayer player = get();
		return player != null ? player.getLevel() : -1;
	}

	public String getName() {
		final RSPlayer player = get();
		return player != null ? player.getName() : null;
	}

	public int getTeam() {
		final RSPlayer player = get();
		return player != null ? player.getTeam() : -1;
	}

	public int getPrayerIcon() {
		final RSPlayer player = get();
		return player != null ? player.getPrayerIcon() : -1;
	}

	public int getSkullIcon() {
		final RSPlayer player = get();
		return player != null ? player.getSkullIcon() : -1;
	}

	public int getNpcId() {
		final RSPlayer player = get();
		final RSPlayerComposite composite;
		return player != null && (composite = player.getComposite()) != null ? composite.getNPCID() : -1;
	}

	public int getId() {
		final RSPlayer player = get();
		return player != null ? player.getName().hashCode() : -1;
	}

	public int[] getAppearance() {
		final RSPlayer player = get();
		final RSPlayerComposite composite = player != null ? player.getComposite() : null;
		if (composite != null) {
			final int[] appearance = composite.getEquipment().clone();
			for (int i = 0; i < appearance.length; i++) {
				if ((appearance[i] & 0x40000000) > 0) {
					appearance[i] &= 0x3fffffff;
				} else {
					appearance[i] = -1;
				}
			}
			return appearance;
		}
		return null;
	}

	public RSPlayer get() {
		return p.get();
	}
}
