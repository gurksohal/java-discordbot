package bot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help {
	public static void messageReceived(MessageReceivedEvent event) {
		if(event.getMessage().getContentDisplay().equalsIgnoreCase("!nhelp")) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle("List of Commands");
			String text = "!setchannel - to set current channel to list details of deleted messages."
					+ "\n!msgs <@User> - to get stats about a user related to deleted messages.";
			builder.addField("Message Tracking:", text, false);
			builder.addField("!cf", "Flip a coin", false);
			builder.addField("!charts", "List spotify charts.\n!charts help - for command usage.", false);
			builder.addField("!shop", "List current contents of the fortnite shop.", false);
			builder.addField("!fortnite", "Get player stats.\n Usage: !fortnite <pc/xbox/psn> <EpicGames Username>", false);
			builder.addField("!tiers", "View league of legends current tier list", false);
			builder.addField("!build", "Show popular build for a league champion\n Usage: !build <Champ Name> <Role/Lane>", false);
			PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
			channel.sendMessage(builder.build()).queue();
		}
	}
}
