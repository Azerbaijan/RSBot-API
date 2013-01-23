package org.powerbot.game.api.methods.interactive;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;

/**
 * A utility for the access of Npcs.
 *
 * @author Timer
 */
public class NPCs {
	public static final Filter<NPC> ALL_FILTER = new Filter<NPC>() {
		public boolean accept(final NPC player) {
			return true;
		}
	};

	/**
	 * @return An array of the currently loaded Npcs in the game.
	 */
	public static NPC[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	/**
	 * @param ids Npc will be accepted if having one of these Ids.
	 * @return An array of the currently loaded NPCs in the game that have one of the provided Ids.
	 */
	public static NPC[] getLoaded(final int... ids) {
		return getLoaded(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				for (final int id : ids) {
					if (npc.getId() == id) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * @param filter The filtering <code>Filter</code> to accept all the Npcs through.
	 * @return An array of the currently loaded Npcs in the game that are accepted by the provided filter.
	 */
	public static NPC[] getLoaded(final Filter<NPC> filter) {
		final Client client = Context.client();
		final int[] indices = client.getRSNPCIndexArray();
		final Set<NPC> npcs = new HashSet<>();
		for (final int index : indices) {
			final Object n = Nodes.lookup(client.getRSNPCNC(), index);

			if (n != null) {
				NPC npc = null;
				if (n instanceof RSNPCNode) {
					npc = new NPC(((RSNPCNode) n).getRSNPC());
				} else if (n instanceof RSNPC) npc = new NPC((RSNPC) n);
				if (npc != null && filter.accept(npc)) {
					npcs.add(npc);
				}
			}
		}
		return npcs.toArray(new NPC[npcs.size()]);
	}

	/**
	 * @param ids Npc will be accepted if having one of these Ids.
	 * @return The nearest Npc with one of these Ids if any present, else <i>null</i>.
	 */
	public static NPC getNearest(final int... ids) {
		return getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				for (final int id : ids) {
					if (id == npc.getId()) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * @param names Npc will be accepted if having one of these names.
	 * @return The nearest Npc with one of these names if any present, else <i>null</i>.
	 */
	public static NPC getNearest(final String... names) {
		return getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				final String name = npc.getName();
				if (name == null) {
					return false;
				}
				for (final String n : names) {
					if (n.toLowerCase().contains(name.toLowerCase())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * @param filter The filtering <code>Filter</code> NPCs have to pass.
	 * @return The nearest Npc passing the filter if any present, else <i>null</i>.
	 */
	public static NPC getNearest(final Filter<NPC> filter) {
		final Client client = Context.client();
		final int[] indices = client.getRSNPCIndexArray();
		NPC npc = null;
		double distance = Double.MAX_VALUE;
		final RegionOffset position = Players.getLocal().getRegionOffset();
		for (final int index : indices) {
			final Object node = Nodes.lookup(client.getRSNPCNC(), index);
			NPC t_npc = null;
			if (node != null) {
				if (node instanceof RSNPCNode) {
					t_npc = new NPC(((RSNPCNode) node).getRSNPC());
				} else if (node instanceof RSNPC) t_npc = new NPC((RSNPC) node);
			}
			if (t_npc != null) {
				try {
					if (filter.accept(t_npc)) {
						final double dist = Calculations.distance(position, t_npc.getRegionOffset());
						if (dist < distance) {
							distance = dist;
							npc = t_npc;
						}
					}
				} catch (final Exception ignored) {
				}
			}
		}
		return npc;
	}
}
