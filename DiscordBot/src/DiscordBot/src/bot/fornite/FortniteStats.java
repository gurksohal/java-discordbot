package bot.fornite;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import bot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//!fortnite <platform> <user>
public class FortniteStats {

	public static void messageReceived(MessageReceivedEvent event) throws IOException, URISyntaxException {
		String[] cmds = event.getMessage().getContentDisplay().split(" ");
		if (cmds[0].equalsIgnoreCase("!fortnite")) {
			ArrayList<String> platforms = new ArrayList<String>();
			platforms.add("pc");
			platforms.add("xbox");
			platforms.add("psn");
			if (cmds.length >= 3 && platforms.contains(cmds[1].toLowerCase())) {
				String username = "";
				for (int x = 2; x < cmds.length; x++) {
					if (x != 2) {
						username += " ";
					}
					username += cmds[x];
				}
				UserStats data = getData(username, cmds[1].toLowerCase());
				if (data != null && data.lifeTimeStats != null && data.stats != null) {
					event.getChannel().sendMessage(buildMsg(data).build()).queue();
				} else {
					event.getChannel().sendMessage(username + " not found on " + cmds[1]).queue();
				}
			} else {
				event.getChannel().sendMessage("Correct usage: !fortnite <pc/psn/xbox> <epic games username>").queue();
			}
		}
	}

	private static UserStats getData(String username, String platform) throws IOException, URISyntaxException {
		System.setProperty("http.agent", "Chrome");
		String link = "https://api.fortnitetracker.com/v1/profile/" + platform + "/" + username + "/";
		URL url = new URL(new URI(link).toASCIIString());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("TRN-Api-Key", Main.fortKey);
		JsonReader jsonReader = new JsonReader(new InputStreamReader(con.getInputStream()));
		return new Gson().fromJson(jsonReader, UserStats.class);
	}

	private static EmbedBuilder buildMsg(UserStats data) throws IOException {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor("Fortnite Stats");
		builder.setTitle(data.epicUserHandle);
		String line = "Total Matches: " + data.lifeTimeStats[7].value + "\nTotal Wins: " + data.lifeTimeStats[8].value
				+ "\nTotal Win%: " + data.lifeTimeStats[9].value + "\nTotal Kills: " + data.lifeTimeStats[10].value +
				"\nTotal KD: " + data.lifeTimeStats[11].value;
		builder.setDescription(line);
		if (data.stats.p2 != null && data.stats.p9 != null && data.stats.p10 != null) {
			builder.addField("Solo Kills/Game", data.stats.p2.kpg.displayValue, true);
			builder.addField("Duo Kills/Game", data.stats.p10.kpg.displayValue, true);
			builder.addField("Squad Kills/Game", data.stats.p9.kpg.displayValue, true);

			builder.addField("Solo Wins", data.stats.p2.top1.displayValue, true);
			builder.addField("Duo Wins", data.stats.p10.top1.displayValue, true);
			builder.addField("Squad Wins", data.stats.p9.top1.displayValue, true);

			builder.addField("Solo Kills", data.stats.p2.kills.displayValue, true);
			builder.addField("Duo Kills", data.stats.p10.kills.displayValue, true);
			builder.addField("Squad Kills", data.stats.p9.kills.displayValue, true);

			builder.addField("Solo Win %", data.stats.p2.winRatio.displayValue, true);
			builder.addField("Duo Win %", data.stats.p10.winRatio.displayValue, true);
			builder.addField("Squad Win %", data.stats.p9.winRatio.displayValue, true);

			builder.addField("Solo Matches", data.stats.p2.matches.displayValue, true);
			builder.addField("Duo Matches", data.stats.p10.matches.displayValue, true);
			builder.addField("Squad Matches", data.stats.p9.matches.displayValue, true);
		}
		builder.addField("Current Season:", "", false);
		if (data.stats.curr_p2 != null && data.stats.curr_p9 != null && data.stats.curr_p10 != null) {
			builder.addField("Solo Kills/Game", data.stats.curr_p2.kpg.displayValue, true);
			builder.addField("Duo Kills/Game", data.stats.curr_p10.kpg.displayValue, true);
			builder.addField("Squad Kills/Game", data.stats.curr_p9.kpg.displayValue, true);

			builder.addField("Solo Wins", data.stats.curr_p2.top1.displayValue, true);
			builder.addField("Duo Wins", data.stats.curr_p10.top1.displayValue, true);
			builder.addField("Squad Wins", data.stats.curr_p9.top1.displayValue, true);

			builder.addField("Solo Matches", data.stats.curr_p2.matches.displayValue, true);
			builder.addField("Duo Matches", data.stats.curr_p10.matches.displayValue, true);
			builder.addField("Squad Matches", data.stats.curr_p9.matches.displayValue, true);
		}
		builder.setFooter(data.platformName.toUpperCase(), null);
		builder.setTimestamp(Instant.now());
		return builder;
	}
}
