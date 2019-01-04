
import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;

public class MessageDelete {
	final static long allsupDiscord = 158087769846054912L;
	final static long realDiscord = 134401490155601920L;
	static ArrayList<Message> msg = new ArrayList<>();

	public static void messageReceived(MessageReceivedEvent event) {

		if (!event.getAuthor().isBot()
				&& (event.getGuild().getIdLong() == allsupDiscord || event.getGuild().getIdLong() == realDiscord)) {
			msg.add(event.getMessage());

			for (int x = 0; x < msg.size(); x++) {
				if (msg.get(x).getCreationTime().plusHours(1).compareTo(OffsetDateTime.now()) < 0) {
					msg.remove(x);
				} else {
					break;
				}
			}
		}
	}

	public static void messageUpdate(MessageUpdateEvent event) {
		MessageChannel allsupChannel = event.getJDA().getTextChannelById(319425657652117505L);
		MessageChannel realChannel = event.getJDA().getTextChannelById(502584989641080862L);
		long msgID = event.getMessageIdLong();
		long guildID = event.getGuild().getIdLong();
		Message oldMsg = null;
		for (int x = 0; x < msg.size(); x++) {
			if (msgID == msg.get(x).getIdLong() && guildID == msg.get(x).getGuild().getIdLong()) {
				oldMsg = msg.get(x);
				msg.remove(x);
				msg.add(event.getMessage());
				break;
			}
		}

		if (oldMsg != null) {
			MessageEmbed changedMsg = buildMessage(event.getMessage(), true, oldMsg).build();
			event.getJDA().getUserById(134139515156561920L).openPrivateChannel().queue((channel) -> channel.sendMessage(changedMsg).queue()); //send pm
			if (oldMsg.getGuild().getIdLong() == allsupDiscord) {
				allsupChannel.sendMessage(changedMsg).queue();
			} else if (oldMsg.getGuild().getIdLong() == realDiscord) {
				realChannel.sendMessage(changedMsg).queue();
			}
		}
	}

	public static void messageDelete(MessageDeleteEvent event) {
		long msgID = event.getMessageIdLong();
		long guildID = event.getGuild().getIdLong();
		Message m = null;
		MessageChannel allsupChannel = event.getJDA().getTextChannelById(319425657652117505L);
		MessageChannel realChannel = event.getJDA().getTextChannelById(502584989641080862L);
		for (int x = 0; x < msg.size(); x++) {
			if (msgID == msg.get(x).getIdLong() && guildID == msg.get(x).getGuild().getIdLong()) {
				m = msg.get(x);
				msg.remove(x);
				break;
			}
		}

		if (m != null) {
			MessageEmbed changedMsg = buildMessage(m, false, null).build();
			event.getJDA().getUserById(134139515156561920L).openPrivateChannel().queue((channel) -> channel.sendMessage(changedMsg).queue()); //send pm
			if (m.getGuild().getIdLong() == allsupDiscord) {
				allsupChannel.sendMessage(changedMsg).queue();
			} else if (m.getGuild().getIdLong() == realDiscord) {
				realChannel.sendMessage(changedMsg).queue();
			}
		}
	}

	private static EmbedBuilder buildMessage(Message m, boolean edit, Message oldMsg) {
		EmbedBuilder builder = new EmbedBuilder();
		List<Attachment> attach = m.getAttachments();
		String display = m.getContentDisplay();
		User author = m.getAuthor();
		if (!edit) {
			if (attach.size() == 1) {
				display = attach.get(0).getProxyUrl();
			}
			
			builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getAvatarUrl());
			builder.setTitle("Message deleted in #" + m.getChannel().getName());
			builder.addField("Message:", display, false);
		} else {
			List<Attachment> oldAttach = oldMsg.getAttachments();
			String oldDisplay = oldMsg.getContentDisplay();
			if (attach.size() == 1) {
				display = attach.get(0).getProxyUrl();
			}
			if(oldAttach.size() == 1) {
				oldDisplay = oldAttach.get(0).getProxyUrl();
			}
			builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getAvatarUrl());
			builder.setTitle("Message edited in #" + m.getChannel().getName());
			builder.addField("Before:", oldDisplay, false);
			builder.addField("After:", display, false);
		}
		builder.setTimestamp(Instant.now());
		builder.setColor(Color.CYAN);
		return builder;
	}
}