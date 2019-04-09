package bot.league.builds;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChampionBuilds {

	public static void messageReceived(MessageReceivedEvent event)
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String[] cmds = event.getMessage().getContentDisplay().split(" ");
		ArrayList<String> role = new ArrayList<String>();
		role.add("top");
		role.add("jungle");
		role.add("jg");
		role.add("mid");
		role.add("adc");
		role.add("support");
		role.add("sup");
		role.add("supp");
		if (cmds.length == 2 && cmds[0].equalsIgnoreCase("!build")) {
			File champFile = new File("DATA/" + cmds[1].toUpperCase() + "/DEFAULT.json");
			event.getChannel().sendMessage(buildMsg(event.getAuthor(), champFile).build()).queue();
		} else if (cmds.length == 3 && cmds[0].equalsIgnoreCase("!build") && role.contains(cmds[2].toLowerCase())) {
			if (cmds[2].equalsIgnoreCase("jg")) {
				cmds[2] = "jungle";
			} else if (cmds[2].equalsIgnoreCase("supp") || cmds[2].equalsIgnoreCase("sup")) {
				cmds[2] = "support";
			}

			File champFile = new File("DATA/" + cmds[1].toUpperCase() + "/" + cmds[2].toUpperCase() + ".json");
			event.getChannel().sendMessage(buildMsg(event.getAuthor(), champFile).build()).queue();
		}
	}

	private static EmbedBuilder buildMsg(User author, File file)
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		EmbedBuilder builder = new EmbedBuilder();
		JSONChampion champData = new Gson().fromJson(new JsonReader(new FileReader(file)), JSONChampion.class);
		String start = getDisplayString(champData.startItems);
		String core = getDisplayString(champData.coreItems);
		String fourth = getDisplayString(champData.fourthOptions);
		String fifth = getDisplayString(champData.fifthOptions);
		String sixth = getDisplayString(champData.sixthOptions);

		builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null,
				author.getAvatarUrl() != null ? author.getAvatarUrl() : author.getDefaultAvatarUrl());

		builder.setTitle("Suggested Items for " + champData.champ.toUpperCase());
		String desc = "Patch: " + champData.patch + "\nRole: " + champData.role.toUpperCase() + "\nWinRate: "
				+ champData.winRate + "\nPickRate: " + champData.pickRate + "\nTotalMatches: " + champData.totalMatches;
		builder.setDescription(desc);
		builder.addField("Starting Items:", start, true);
		builder.addField("Core Items:", core, true);
		builder.addBlankField(true);
		builder.addField("Fourth Item Options:", fourth, true);
		builder.addField("Fifth Item Options:", fifth, true);
		builder.addField("Sixth Item Options:", sixth, true);
		builder.addBlankField(true);
		builder.setFooter("Data from u.gg", null);
		builder.setTimestamp(Instant.now());
		builder.setColor(Color.CYAN);
		return builder;
	}

	private static String getDisplayString(ArrayList<String> array) {
		String returnString = "";
		for (String s : array) {
			returnString += s + "\n";
		}
		return returnString;
	}
}
