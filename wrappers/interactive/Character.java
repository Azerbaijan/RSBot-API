package org.powerbot.game.api.wrappers.interactive;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.Rotatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.graphics.model.CharacterModel;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.CombatStatus;
import org.powerbot.game.client.CombatStatusData;
import org.powerbot.game.client.LinkedListNode;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.RSAnimator;
import org.powerbot.game.client.RSCharacter;
import org.powerbot.game.client.RSInteractable;
import org.powerbot.game.client.RSInteractableData;
import org.powerbot.game.client.RSInteractableLocation;
import org.powerbot.game.client.RSMessageData;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSPlayer;
import org.powerbot.game.client.Sequence;

/**
 * @author Timer
 */
public abstract class Character implements Entity, Locatable, Rotatable, Identifiable {
	private final Client client;

	public Character() {
		this.client = Context.client();
	}

	public abstract int getLevel();

	public abstract String getName();

	public RegionOffset getRegionOffset() {
		final RSInteractable location = get();
		final RSInteractableData data = location.getData();
		return new RegionOffset((int) data.getLocation().getX() >> 9, (int) data.getLocation().getY() >> 9, getPlane());
	}

	public RSInteractableLocation getRenderableLocation() {
		final RSInteractable interactable = get();
		return get() != null ? interactable.getData().getLocation() : null;
	}

	public Tile getLocation() {
		final RegionOffset regionTile = getRegionOffset();
		return new Tile(Game.getBaseX() + regionTile.getX(), Game.getBaseY() + regionTile.getY(), regionTile.getPlane());
	}

	public int getPlane() {
		final RSCharacter character = get();
		return character != null ? character.getPlane() : -1;
	}

	public Character getInteracting() {
		final RSCharacter character = get();
		final int index = character != null ? character.getInteracting() : -1;
		if (index == -1) {
			return null;
		}
		if (index < 32768) {
			final Object npcNode = Nodes.lookup(client.getRSNPCNC(), index);
			if (npcNode == null) {
				return null;
			}
			if (npcNode instanceof RSNPCNode) {
				return new NPC(((RSNPCNode) npcNode).getRSNPC());
			} else if (npcNode instanceof RSNPC) return new NPC((RSNPC) npcNode);
			return null;
		} else {
			final int pos = index - 32768;
			final RSPlayer[] players = client.getRSPlayerArray();
			return pos >= 0 && pos < players.length ? new Player(players[pos]) : null;
		}
	}

	public int getAnimation() {
		final RSCharacter character = get();
		final RSAnimator animation = character != null ? character.getAnimation() : null;
		if (animation != null) {
			final Sequence sequence = animation.getSequence();
			if (sequence != null) {
				return sequence.getID();
			}
		}
		return -1;
	}

	public int getPassiveAnimation() {
		final RSCharacter character = get();
		final RSAnimator animation = character != null ? character.getPassiveAnimation() : null;
		if (animation != null) {
			final Sequence sequence = animation.getSequence();
			if (sequence != null) {
				return sequence.getID();
			}
		}
		return -1;
	}

	public int getHeight() {
		final RSCharacter character = get();
		return character != null ? character.getHeight() : -1;
	}

	public int getRotation() {
		final RSCharacter character = get();
		return character != null ? character.getOrientation() : -1;
	}

	public int getOrientation() {
		final int r = getRotation();
		return r != -1 ? (630 - r * 45 / 0x800) % 360 : -1;
	}

	private LinkedListNode[] getBarNodes() {
		final RSCharacter accessor = get();
		if (accessor == null) return null;
		final org.powerbot.game.client.LinkedList barList = accessor.getCombatStatusList();
		if (barList == null) return null;
		final LinkedListNode tail = barList.getTail();
		LinkedListNode health, adrenaline, current;
		current = tail.getNext();
		if (current.getNext() != tail) {
			adrenaline = current;
			health = current.getNext();
		} else {
			adrenaline = null;
			health = current;
		}

		return new LinkedListNode[]{adrenaline, health};
	}

	private CombatStatusData[] getBarData() {
		final LinkedListNode[] nodes = getBarNodes();
		if (nodes == null) return null;
		final CombatStatusData[] data = new CombatStatusData[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == null || !(nodes[i] instanceof CombatStatus)) {
				data[i] = null;
				continue;
			}
			final CombatStatus status = (CombatStatus) nodes[i];
			final org.powerbot.game.client.LinkedList statuses = status.getData();
			if (statuses == null) {
				data[i] = null;
				continue;
			}

			data[i] = (CombatStatusData) statuses.getTail().getNext();
		}
		return data;
	}

	private int toPercent(final int ratio) {
		return (int) Math.ceil((ratio * 100) / 0xff);
	}

	public int getAdrenalineRatio() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) return 0;
		return data[0].getHPRatio();
	}

	public int getHealthRatio() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) return 100;
		return data[1].getHPRatio();
	}

	public int getAdrenalinePercent() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) return 0;
		return toPercent(data[0].getHPRatio());
	}

	public int getHealthPercent() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) return 100;
		return toPercent(data[1].getHPRatio());
	}

	@Deprecated
	/**
	 * @see #getHealthRatio()
	 */
	public int getHpRatio() {
		return getHealthRatio();
	}

	@Deprecated
	/**
	 * @see #getHealthPercent()
	 */
	public int getHpPercent() {
		return getHealthPercent();
	}

	public boolean isInCombat() {
		final CombatStatusData[] data = getBarData();
		return data != null && data[1] != null && data[1].getLoopCycleStatus() < Context.client().getLoopCycle();
	}

	public boolean isIdle() {
		return !isMoving() && !isInCombat() && getAnimation() == -1 && getInteracting() == null;
	}

	public String getMessage() {
		final RSCharacter character = get();
		final RSMessageData message_data = character != null ? character.getMessageData() : null;
		return message_data != null ? message_data.getMessage() : null;
	}

	public int getSpeed() {
		final RSCharacter character = get();
		return character != null ? character.isMoving() : -1;
	}

	public boolean isMoving() {
		return getSpeed() != 0;
	}

	public CapturedModel getModel() {
		final RSCharacter ref = get();
		if (ref != null) {
			final Model model = ref.getModel();
			if (model != null) {
				return new CharacterModel(model, this);
			}
		}
		return null;
	}

	protected abstract RSCharacter get();

	public boolean validate() {
		return get() != null;
	}

	public Point getCentralPoint() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getCentralPoint();
		}
		final RSCharacter character = get();
		final RSInteractableData data = character.getData();
		return Calculations.groundToScreen((int) data.getLocation().getX(), (int) data.getLocation().getY(), character.getPlane(), -getHeight() / 2);
	}

	public Point getNextViewportPoint() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getNextViewportPoint();
		}
		return getCentralPoint();
	}

	public boolean contains(final Point point) {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.contains(point);
		}
		return getLocation().contains(point);
	}

	public boolean isOnScreen() {
		final CapturedModel model = getModel();
		return model != null ? model.isOnScreen() : Calculations.isOnScreen(getCentralPoint());
	}

	public Polygon[] getBounds() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getBounds();
		}
		return getLocation().getBounds();
	}

	public boolean hover() {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public boolean click(final boolean left) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				Mouse.click(true);
				return true;
			}
		});
	}

	public boolean interact(final String action) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action);
			}
		});
	}

	public boolean interact(final String action, final String option) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action, option);
			}
		});
	}

	public void draw(final Graphics render) {
		final RSCharacter character = get();
		if (character != null) {
			final RegionOffset offset = getRegionOffset();
			final Point p = Calculations.groundToScreen(offset.getX(), offset.getY(), offset.getPlane(), getHeight() / 2);

			render.setColor(Color.red);
			render.fillRect(p.x - 3, p.y - 3, 6, 6);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Character) {
			final Character cha = (Character) obj;
			return cha.get() == get();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(get());
	}
}
