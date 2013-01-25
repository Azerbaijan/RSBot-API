package org.powerbot.game.api.wrappers.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.graphics.model.RenderableModel;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.BaseInfo;
import org.powerbot.game.client.Cache;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.RSGround;
import org.powerbot.game.client.RSGroundInfo;
import org.powerbot.game.client.RSInfo;
import org.powerbot.game.client.RSItemDefLoader;
import org.powerbot.game.client.RSItemPile;

/**
 * @author Timer
 */
public class GroundItem implements Entity, Locatable, Identifiable {
	private final Tile tile;
	private final RegionOffset localTile;
	private final Item groundItem;

	public GroundItem(final Tile tile, final Item groundItem) {
		this.tile = tile;
		this.localTile = new RegionOffset(tile.getX() - Game.getBaseX(), tile.getY() - Game.getBaseY(), tile.getPlane());
		this.groundItem = groundItem;
	}

	public CapturedModel getModel() {
		final Client client = Context.client();
		final RSInfo info = client.getRSGroundInfo();
		final BaseInfo baseInfo = info.getBaseInfo();
		final int x = tile.getX() - baseInfo.getX(), y = tile.getY() - baseInfo.getY();
		final RSGroundInfo groundInfo = info.getRSGroundInfo();
		final RSGround[][][] grounds = groundInfo.getRSGroundArray();
		final int plane = client.getPlane();
		final RSGround ground = grounds[plane][x][y];
		if (ground != null) {
			final RSItemPile itemPile = ground.getRSItemPile();
			if (itemPile != null) {
				final int graphicsIndex = Context.resolve().composite.toolkit.graphicsIndex;
				final int[] ids = {itemPile.getID_1(), itemPile.getID_2(), itemPile.getID_3()};
				final Model[] models = new Model[ids.length];

				final RSItemDefLoader defLoader = client.getRSItemDefLoader();
				final Cache cache = defLoader.getModelCache();
				final HashTable table = cache.getTable();

				int i = 0;
				for (final int id : ids) {
					final Object model = Nodes.lookup(table, (long) id | (long) graphicsIndex << 29);
					if (model != null && model instanceof Model) models[i++] = (Model) model;
				}

				return i > 0 ? new RenderableModel(models[Random.nextInt(0, i)], itemPile) : null;
			}
		}
		return null;
	}

	public RegionOffset getRegionOffset() {
		return localTile;
	}

	public Tile getLocation() {
		return tile;
	}

	public Item getGroundItem() {
		return groundItem;
	}

	public int getId() {
		return groundItem.getId();
	}

	public boolean validate() {
		return tile.validate() && getId() != -1 && GroundItems.getNearest(new Filter<GroundItem>() {
			@Override
			public boolean accept(final GroundItem groundItem) {
				return groundItem.getId() == getId() && groundItem.getLocation().equals(getLocation());
			}
		}) != null;
	}

	public Point getCentralPoint() {
		return tile.getPoint(0.5d, 0.5d, 0);
	}

	public Point getNextViewportPoint() {
		return tile.getPoint(Random.nextDouble(), Random.nextDouble(), 0);
	}

	public boolean contains(final Point point) {
		return getCentralPoint().distance(point) < 3;
	}

	public boolean isOnScreen() {
		return Calculations.isOnScreen(getCentralPoint());
	}

	public Polygon[] getBounds() {
		return tile.getBounds();
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
				Mouse.click(left);
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
		final RegionOffset offset = getRegionOffset();
		final Point p = Calculations.groundToScreen(offset.getX(), offset.getY(), offset.getPlane(), 0);

		render.setColor(Color.magenta);
		render.fillRect(p.x - 3, p.y - 3, 6, 6);
	}
}
