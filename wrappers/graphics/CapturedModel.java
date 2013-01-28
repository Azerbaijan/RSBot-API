package org.powerbot.game.api.wrappers.graphics;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.ModelCapture;

public abstract class CapturedModel {
	public static Filter<CapturedModel> newVertexFilter(final short[] faceA) {
		return new Filter<CapturedModel>() {
			public boolean accept(final CapturedModel m) {
				return Arrays.equals(m.faceA, faceA);
			}
		};
	}

	protected int[] xPoints;
	protected int[] yPoints;
	protected int[] zPoints;

	protected short[] faceA;
	protected short[] faceB;
	protected short[] faceC;

	protected int numVertices;
	protected int numFaces;

	public CapturedModel(final org.powerbot.game.client.Model model) {
		xPoints = model.getXPoints();
		yPoints = model.getYPoints();
		zPoints = model.getZPoints();
		faceA = model.getIndices1();
		faceB = model.getIndices2();
		faceC = model.getIndices3();

		if (model instanceof ModelCapture) {
			numVertices = ((ModelCapture) model).getNumVertices();
			numFaces = ((ModelCapture) model).getNumFaces();
		} else {
			numVertices = Math.min(xPoints.length, Math.min(yPoints.length, zPoints.length));
			numFaces = Math.min(faceA.length, Math.min(faceB.length, faceC.length));
		}
	}

	protected abstract int getLocalX();

	protected abstract int getLocalY();

	protected abstract void update();

	@Deprecated
	public boolean isOnScreen() {
		return nextTriangle() != -1;
	}

	public int nextTriangle() {
		update();
		final int mark = Random.nextInt(0, numFaces);
		int index = firstOnScreenIndex(mark, numFaces);
		return index != -1 ? index : firstOnScreenIndex(0, mark);
	}

	public Point getCentroid(final int index) {
		if (index < 0 || index >= numFaces) return null;
		update();
		final int x = getLocalX();
		final int y = getLocalY();
		final int height = Calculations.calculateTileHeight(x, y);
		final Point localPoint = Calculations.worldToScreen(
				x + (this.xPoints[this.faceA[index]] + this.xPoints[this.faceB[index]] + this.xPoints[this.faceC[index]]) / 3,
				height + (this.yPoints[this.faceA[index]] + this.yPoints[this.faceB[index]] + this.yPoints[this.faceC[index]]) / 3,
				y + (this.zPoints[this.faceA[index]] + this.zPoints[this.faceB[index]] + this.zPoints[this.faceC[index]]) / 3
		);
		return Calculations.isOnScreen(localPoint) ? localPoint : null;
	}

	@Deprecated
	public Point getNextViewportPoint() {
		return getNextPoint();
	}

	public Point getNextPoint() {
		update();
		final int mark = Random.nextInt(0, numFaces);
		Point point = firstOnScreenCentroid(mark, numFaces);
		return point != null ? point : (point = firstOnScreenCentroid(0, mark)) != null ? point : new Point(-1, -1);
	}

	@Deprecated
	public Point getCentralPoint() {
		return getCenterPoint();
	}

	public Point getCenterPoint() {
		if (numFaces < 1) {
			return new Point(-1, -1);
		}
		update();

		int totalXAverage = 0;
		int totalYAverage = 0;
		int totalHeightAverage = 0;
		int index = 0;

		final int x = getLocalX();
		final int y = getLocalY();
		final int height = Calculations.calculateTileHeight(x, y);

		while (index < numFaces) {
			totalXAverage += (xPoints[faceA[index]] + xPoints[faceB[index]] + xPoints[faceC[index]]) / 3;
			totalYAverage += (zPoints[faceA[index]] + zPoints[faceB[index]] + zPoints[faceC[index]]) / 3;
			totalHeightAverage += (yPoints[faceA[index]] + yPoints[faceB[index]] + yPoints[faceC[index]]) / 3;
			index++;
		}

		final Point averagePoint = Calculations.worldToScreen(
				x + totalXAverage / numFaces,
				height + totalHeightAverage / numFaces,
				y + totalYAverage / numFaces
		);

		if (Calculations.isOnScreen(averagePoint)) {
			return averagePoint;
		}
		return new Point(-1, -1);
	}

	@Deprecated
	public Polygon[] getBounds() {
		return getTriangles();
	}

	public Polygon[] getTriangles() {
		final int[][] points = projectVertices();
		final ArrayList<Polygon> polygons = new ArrayList<>(numFaces);
		for (int index = 0; index < numFaces; index++) {
			final int index1 = faceA[index];
			final int index2 = faceB[index];
			final int index3 = faceC[index];

			final int xPoints[] = new int[3];
			final int yPoints[] = new int[3];

			xPoints[0] = points[index1][0];
			yPoints[0] = points[index1][1];
			xPoints[1] = points[index2][0];
			yPoints[1] = points[index2][1];
			xPoints[2] = points[index3][0];
			yPoints[2] = points[index3][1];

			if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
				polygons.add(new Polygon(xPoints, yPoints, 3));
			}
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

	public boolean contains(final Point point) {
		final int x = point.x, y = point.y;
		final int[][] points = projectVertices();
		int index = 0;
		while (index < numFaces) {
			final int index1 = faceA[index];
			final int index2 = faceB[index];
			final int index3 = faceC[index];
			if (points[index1][2] + points[index2][2] + points[index3][2] == 3 &&
					isPointInTriangle(x, y, points[index1][0], points[index1][1], points[index2][0], points[index2][1], points[index3][0], points[index3][1])) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public void draw(final Graphics render) {
		drawWireFrame(render);
	}

	public void drawWireFrame(final Graphics render) {
		final int[][] screen = projectVertices();

		for (int index = 0; index < numFaces; index++) {
			int index1 = faceA[index];
			int index2 = faceB[index];
			int index3 = faceC[index];

			int point1X = screen[index1][0];
			int point1Y = screen[index1][1];
			int point2X = screen[index2][0];
			int point2Y = screen[index2][1];
			int point3X = screen[index3][0];
			int point3Y = screen[index3][1];

			if (screen[index1][2] + screen[index2][2] + screen[index3][2] == 3) {
				render.drawLine(point1X, point1Y, point2X, point2Y);
				render.drawLine(point2X, point2Y, point3X, point3Y);
				render.drawLine(point3X, point3Y, point1X, point1Y);
			}
		}
	}

	private int firstOnScreenIndex(final int pos, final int length) {
		final Context context = Context.get();
		final Calculations.Toolkit toolkit = context.getToolkit();
		final Calculations.Viewport viewport = context.getViewport();

		final int x = getLocalX();
		final int y = getLocalY();
		final int h = Calculations.calculateTileHeight(x, y);
		int index = pos;
		while (index < length) {
			final Point point = Calculations.worldToScreen(toolkit, viewport,
					x + (this.xPoints[this.faceA[index]] + this.xPoints[this.faceB[index]] + this.xPoints[this.faceC[index]]) / 3,
					h + (this.yPoints[this.faceA[index]] + this.yPoints[this.faceB[index]] + this.yPoints[this.faceC[index]]) / 3,
					y + (this.zPoints[this.faceA[index]] + this.zPoints[this.faceB[index]] + this.zPoints[this.faceC[index]]) / 3
			);
			if (Calculations.isOnScreen(point)) {
				return index;
			}
			++index;
		}
		return -1;
	}

	private Point firstOnScreenCentroid(final int pos, final int length) {
		final int index = firstOnScreenIndex(pos, length);
		return index != -1 ? getCentroid(index) : null;
	}

	private boolean isPointInTriangle(final int x, final int y, int x1, int y1, int x2, int y2, int x3, int y3) {
		return (x - x2) * (y1 - y2) - (x1 - x2) * (y - y2) < 0 &&
				(x - x3) * (y2 - y3) - (x2 - x3) * (y - y3) < 0 &&
				(x - x1) * (y3 - y1) - (x3 - x1) * (y - y1) < 0;
	}

	private int[][] projectVertices() {
		final Context context = Context.get();
		final Calculations.Toolkit toolkit = context.getToolkit();
		final Calculations.Viewport viewport = context.getViewport();

		update();
		final int locX = getLocalX();
		final int locY = getLocalY();
		final int height = Calculations.calculateTileHeight(locX, locY);

		final int[][] screen = new int[numVertices][3];
		for (int index = 0; index < numVertices; index++) {
			final int x = xPoints[index] + locX;
			final int y = yPoints[index] + height;
			final int z = zPoints[index] + locY;

			final float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
			final float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
			final float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yZ * z));

			if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
				screen[index][0] = Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z);
				screen[index][1] = Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z);
				screen[index][2] = 1;
			} else {
				screen[index][0] = -1;
				screen[index][1] = -1;
				screen[index][2] = 0;
			}
		}
		return screen;
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof CapturedModel) {
			final CapturedModel model = (CapturedModel) o;
			return Arrays.equals(faceA, model.faceA) &&
					Arrays.equals(xPoints, model.xPoints) && Arrays.equals(yPoints, model.yPoints) && Arrays.equals(zPoints, model.zPoints);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[faces=" + numFaces + "vertices=" + numVertices + "] " + Arrays.toString(faceA);
	}
}
