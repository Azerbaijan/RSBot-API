package org.powerbot.game.api.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Hiscore related-operations.
 *
 * @author Aion
 */
public final class Highscores {
	public interface Activity {
		int DUEL_TOURNAMENT = 0;
		int BOUNTY_HUNTERS = 1;
		int BOUNTY_HUNTER_ROGUES = 2;
		int FIST_OF_GUTHIX = 3;
		int MOBILISING_ARMIES = 4;
		int BA_ATTACKERS = 5;
		int BA_DEFENDERS = 6;
		int BA_COLLECTORS = 7;
		int BA_HEALERS = 8;
		int CASTLE_WARS_GAMES = 9;
		int CONQUEST = 10;
		int DOMINION_TOWER = 11;
		int THE_CRUCIBLE = 12;
		int GG_ATHLETICS = 13;
		int GG_RESOURCE_RACE = 14;
	}

	public static final String HOST = "http://hiscore.runescape.com";
	public static final String QUERY = "/index_lite.ws?player=";

	private static final int ACTIVITY_INDICES = 15;
	private static final int SKILL_INDICES = 26;

	public final String playerName;
	public final int[][][] skillStats;
	public final int[][][] activityStats;

	private Highscores(final String playerName, final int[][][] skillStats, final int[][][] activityStats) {
		this.playerName = playerName;
		this.skillStats = skillStats;
		this.activityStats = activityStats;
	}

	/**
	 * Gets the rank for the provided activity index.
	 *
	 * @param index the activity index
	 * @return the rank or -1
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public int getActivityRank(final int index) {
		if (index < 0 || index > ACTIVITY_INDICES) {
			throw new IllegalArgumentException("invalid activity index: " + index);
		}
		return activityStats[0][0][index];
	}

	/**
	 * Gets the score for the provided activity index.
	 *
	 * @param index the activity index
	 * @return the score or -1
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public int getActivityScore(final int index) {
		if (index < 0 || index > ACTIVITY_INDICES) {
			throw new IllegalArgumentException("invalid activity index: " + index);
		}
		return activityStats[1][0][index];
	}

	/**
	 * Gets the experience for the provided skill index.
	 *
	 * @param index the skill index
	 * @return the experience or -1
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public int getExperience(final int index) {
		if (index < 0 || index > SKILL_INDICES) {
			throw new IllegalArgumentException("invalid skill index: " + index);
		}
		return skillStats[2][0][index - 1];
	}

	/**
	 * Gets the level for the provided skill index.
	 *
	 * @param index the skill index
	 * @return the level or -1
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public int getLevel(final int index) {
		if (index < 0 || index > SKILL_INDICES) {
			throw new IllegalArgumentException("invalid skill index: " + index);
		}
		return skillStats[1][0][index - 1];
	}

	/**
	 * Gets the rank for the provided skill index.
	 *
	 * @param index the skill index
	 * @return the rank or -1
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public int getRank(final int index) {
		if (index < 0 || index > SKILL_INDICES) {
			throw new IllegalArgumentException("invalid skill index: " + index);
		}
		return skillStats[0][0][index - 1];
	}

	/**
	 * Gets the overall experience.
	 *
	 * @return the overall experience or -1
	 */
	public int getOverallExperience() {
		return skillStats[2][0][0];
	}

	/**
	 * Gets the overall level (also known as the total level).
	 *
	 * @return the overall level or -1
	 */
	public int getOverallLevel() {
		return skillStats[1][0][0];
	}

	/**
	 * Gets the overall rank.
	 *
	 * @return the overall rank or -1
	 */
	public int getOverallRank() {
		return skillStats[0][0][0];
	}

	/**
	 * Gets the name of the player whose hiscores were fetched.
	 *
	 * @return the player name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Fetches the skill and activity stats of the given player name from the RuneScape website.
	 *
	 * @param playerName the name of the player to look up
	 * @return an instance of <code>Hiscore</code>; otherwise <code>null</code> if unable to fetch data
	 */
	public static Highscores lookup(final String playerName) {
		if (playerName != null && !playerName.isEmpty()) {
			try {
				final URL url = new URL(HOST + QUERY + playerName);
				final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				final int[][][] skillStats = new int[3][1][SKILL_INDICES];
				final int[][][] activityStats = new int[2][1][ACTIVITY_INDICES];
				for (int i = 0; i < SKILL_INDICES + ACTIVITY_INDICES; i++) {
					final String[] stats = br.readLine().split(",");
					for (int j = 0; j < stats.length; j++) {
						int stat = Integer.parseInt(stats[j]);
						if (i < SKILL_INDICES) {
							skillStats[j][0][i] = stat;
						} else {
							activityStats[j][0][i - 26] = stat;
						}
					}
				}
				br.close();
				return new Highscores(playerName, skillStats, activityStats);
			} catch (final IOException ignored) {
			}
		}
		return null;
	}
}