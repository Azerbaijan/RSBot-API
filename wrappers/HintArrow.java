package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSHintArrow;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSPlayer;

/**
 * @author Timer
 */
public class HintArrow implements Verifiable, Locatable {
	private final RSHintArrow arrow;

	public HintArrow(final RSHintArrow arrow) {
		this.arrow = arrow;
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
		return null;
	}

	@Override
	public Tile getLocation() {
		final Client client = Context.client();
		final int type = getType();
		int target = getTargetId();
		if (type == 0) return null;
		if (type == 1) {
			NPC npc = null;
			final Object node = Nodes.lookup(client.getRSNPCNC(), target);
			if (node != null) {
				if (node instanceof RSNPCNode) {
					npc = new NPC(((RSNPCNode) node).getRSNPC());
				} else if (node instanceof RSNPC) npc = new NPC((RSNPC) node);
			}
			return npc != null ? npc.getLocation() : null;
		} else if (type == 2) {
			return new Tile(Game.getBaseX() + (arrow.getX() >> 9), Game.getBaseY() + (arrow.getY() >> 9), getPlane());
		}
		final RSPlayer[] players = client.getRSPlayerArray();
		if (type != 10 || target < 0 || target >= players.length) return null;
		final RSPlayer localPlayer = players[target];
		if (localPlayer != null) return new Player(localPlayer).getLocation();
		return null;
	}

	@Override
	public boolean validate() {
		final RSHintArrow[] arr = Context.client().getRSHintArrows();
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow == this.arrow) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder(HintArrow.class.getName())
				.append("[x=").append(arrow.getX() >> 9).append(",y=").append(arrow.getY() >> 9).append(",type=").append(getType()).append(",target=").append(getTargetId())
				.append(",location=").append(getLocation())
				.append(']').toString();
	}
}
