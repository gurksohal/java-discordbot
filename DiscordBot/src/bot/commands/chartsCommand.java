package bot.commands;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

class Song {
	public String rank;
	public String title;
	public String artist;

	public Song(String line) {
		String a[] = makeString(line);
		rank = a[0] + ") (" + NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(a[3])) + ")";
		title = a[1];
		artist = a[2];
	}

	private String[] makeString(String line) {
		// System.out.println(line);
		String returnString[] = new String[4];
		int index = line.lastIndexOf(",");
		line = line.substring(0, index);
		line = line.replaceAll("\"", "");
		// total streams
		index = line.lastIndexOf(",");
		returnString[3] = line.substring(index + 1);
		line = line.substring(0, index);
		// artist
		index = line.lastIndexOf(",");
		returnString[2] = line.substring(index + 1);
		line = line.substring(0, index);
		// rank
		index = line.indexOf(",");
		returnString[0] = line.substring(0, index);
		line = line.substring(index + 1);
		// name
		returnString[1] = line;
		if ((index = returnString[1].indexOf('(')) != -1) {
			returnString[1] = returnString[1].substring(0, index);
		}
		if ((index = returnString[1].indexOf('-')) != -1) {
			returnString[1] = returnString[1].substring(0, index);
		}
		if ((index = returnString[1].indexOf(',')) != -1) {
			returnString[1] = returnString[1].substring(0, index);
		}
		return returnString;
	}

}

public class chartsCommand extends ListenerAdapter {
	final static int NUM_OF_SONGS = 25;
	final static String REGION = "global";
	final static String DAY_OR_WEEK = "daily";
	final static String DATE = "latest";
	final static String URL = "https://spotifycharts.com/regional/%s/%s/%s/download";

	// !CHARTS [COUNRTY] [DATE] [DAILY OR WEEKLY]

	public static void messageReveived(MessageReceivedEvent event) {
		String link = null;
		String message = event.getMessage().getContentDisplay();
		try {
			if (message.equalsIgnoreCase("!charts")) {
				link = getURL(REGION, DAY_OR_WEEK, DATE);
				event.getChannel().sendMessage(buildMessage(link, REGION, DAY_OR_WEEK, DATE).build()).queue();
			} else if (message.startsWith("!charts")) {
				String[] cmds = message.split(" ");
				if (cmds.length == 2 && cmds[1].equalsIgnoreCase("help")) {
					event.getChannel()
							.sendMessage("```CSS\nCorrect format: !charts [country code] [date] [daily or weekly]\n"
									+ "Example !charts us 2018-09-23 daily\n```")
							.queue();
				} else if (cmds.length <= 4) {
					if (cmds.length == 2) {
						link = getURL(cmds[1], DAY_OR_WEEK, DATE);
						event.getChannel().sendMessage(buildMessage(link, cmds[1], DAY_OR_WEEK, DATE).build()).queue();
					} else if (cmds.length == 3) {
						link = getURL(cmds[1], DAY_OR_WEEK, cmds[2]);
						event.getChannel().sendMessage(buildMessage(link, cmds[1], DAY_OR_WEEK, cmds[2]).build())
								.queue();
					} else if (cmds.length == 4) {
						if (cmds[3].equalsIgnoreCase("weekly") && !cmds[2].equalsIgnoreCase("latest")) {
							cmds[2] = getWeek(cmds[2]);
						}
						link = getURL(cmds[1], cmds[3], cmds[2]);
						event.getChannel().sendMessage(buildMessage(link, cmds[1], cmds[3], cmds[2]).build()).queue();
					}
				}
			}
		} catch (IOException | NumberFormatException | IndexOutOfBoundsException | ParseException e) {
			event.getChannel()
					.sendMessage("```CSS\nCorrect format: !charts [country code] [date] [daily or weekly].\n"
							+ "date format is yyyy/MM/DD. (country code: us = united states, ca = canada) \n"
							+ "To check weekly make sure the day you enter is a thursday.```")
					.queue();
			e.printStackTrace();
			System.out.println("Message send was \"" + event.getMessage().getContentDisplay() + "\" by user \""
					+ event.getAuthor().getName() + "\"");
			System.out.println(link);
		}
	}

	private static String getURL(String country, String chartType, String day) {
		return String.format(URL, country, chartType, day);
	}

	private static EmbedBuilder buildMessage(String link, String country, String chartType, String day)
			throws IOException, ParseException {
		EmbedBuilder builder = new EmbedBuilder();
		System.setProperty("http.agent", "Chrome");
		URL url = new URL(link);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = reader.readLine(); // get rid of junk from the top
		line = reader.readLine(); // junk
		//
		line = reader.readLine();
		int curr = 0;
		Song data[] = new Song[NUM_OF_SONGS];
		while (line != null && curr < NUM_OF_SONGS) {
			data[curr++] = new Song(line);
			line = reader.readLine();
		}
		String rank = "";
		String title = "";
		String artist = "";
		for (Song x : data) {
			rank += x.rank + "\n";
			title += x.title + "\n";
			artist += x.artist + "\n";
		}
		builder.setTitle("Spotify Charts");
		builder.addField("Rank and Streams", rank, true);
		builder.addField("Title", title, true);
		builder.addField("Artist", artist, true);
		builder.setAuthor("Top 25 songs");
		if (!day.equalsIgnoreCase("latest")) {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			c.setTime(sdf.parse(day));
			String[] date = c.getTime().toString().split(" ");
			day = date[0] + " " + date[1] + " " + date[2] + " " + date[5];
		}
		String s = String.format("%s\t| %s\t| %s", day, country, chartType);
		builder.setDescription(s);
		builder.setColor(Color.RED);
		return builder;
	}

	private static String getWeek(String date) throws ParseException {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		c.setTime(sdf.parse(date));
		c.add(Calendar.DATE, 1);
		String date2 = sdf.format(c.getTime());
		c.add(Calendar.DATE, -7);
		String date1 = sdf.format(c.getTime());
		date1 += "--" + date2;
		return date1;
	}

}
