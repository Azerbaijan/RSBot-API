package org.powerbot.game.api.wrappers.interactive;

import java.lang.ref.WeakReference;

import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCDef;

/**
 * @author Timer
 */
public class NPC extends Character {
	private final WeakReference<RSNPC> n;

	public NPC(final RSNPC n) {
		this.n = new WeakReference<>(n);
	}

	public int getLevel() {
		final RSNPC npc = get();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getLevel() : -1;
	}

	public String getName() {
		final RSNPC npc = get();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getName() : null;
	}

	public int getId() {
		final RSNPC npc = get();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getID() : -1;
	}

	public String[] getActions() {
		final RSNPC npc = get();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getActions() : null;
	}

	public int getPrayerIcon() {
		final RSNPC npc = get();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getPrayerIcon() : -1;
	}

	public RSNPC get() {
		return n.get();
	}
}
