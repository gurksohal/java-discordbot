package bot.msgtrack;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
//channel delete
public class MessageTrack {
	
	public static void onReady(ReadyEvent event, Connection con) throws SQLException {
		List<Guild> guilds = event.getJDA().getGuilds();
		Statement stmt = con.createStatement();
		for(Guild g : guilds) { //add all guilds
			ResultSet rs = stmt.executeQuery("SELECT * FROM guilds WHERE guild_id=" + g.getIdLong()); //select row where guild ids =
			if(!rs.next()) {
				stmt.executeUpdate("INSERT INTO guilds " + "VALUES (" + g.getIdLong() + "," + null + ")");
			}
		}
	}
	
	public static void onJoin(GuildJoinEvent event, Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO guilds VALUES (" + event.getGuild().getIdLong() + "," + null + ")");
	}
	
	public static void messageReceived(MessageReceivedEvent event, Connection con) throws SQLException {
		String msg = event.getMessage().getContentDisplay();
		long cID = event.getChannel().getIdLong();
		long gID = event.getGuild().getIdLong();
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet rs = dbm.getTables(null, null, Long.toString(gID) + "_msg", null); //find tables by this name
		Statement stmt = con.createStatement();
		if(msg.equalsIgnoreCase("!setchannel")) {
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getIdLong() == 134139515156561920L) {
				stmt.executeUpdate("UPDATE guilds SET channel_id=" + cID + " WHERE guild_id=" + gID);
				if(!rs.next()) { //if tables doesn't exist (name table with the guild id)
					stmt.executeUpdate("CREATE TABLE " + gID + "_msg (message_id BIGINT PRIMARY KEY, author_id BIGINT, channel_name VARCHAR(254), msg_text VARCHAR(2000), status BOOL)");
				}
				event.getChannel().sendMessage("Updated channel").queue();
			}else {
				event.getChannel().sendMessage("Only admins can use this command").queue();
			}
		}else if(!event.getAuthor().isBot() && rs.next() && !msg.startsWith("!")) { //if tables exists
			long senderID = event.getAuthor().getIdLong();
			long msgID = event.getMessageIdLong();
			List<Attachment> attach = event.getMessage().getAttachments();
			if (attach.size() == 1) {
				msg = attach.get(0).getProxyUrl();
			}
			PreparedStatement ps = con.prepareStatement("INSERT INTO " + gID + "_msg (message_id, author_id, channel_name, msg_text, status) VALUES (?,?,?,?,?)"); //add msg
			ps.setLong(1, msgID);
			ps.setLong(2, senderID);
			ps.setString(3, event.getChannel().getName());
			ps.setString(4, msg);
			ps.setBoolean(5, false);
			ps.executeUpdate();
		}
	}
	
	public static void messageUpdate(MessageUpdateEvent event, Connection con) throws SQLException {
		long gID = event.getGuild().getIdLong();
		Statement stmt = con.createStatement();
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet rs = dbm.getTables(null, null, Long.toString(gID) + "_msg", null); //check tables
		if(rs.next()) {
			ResultSet channel = stmt.executeQuery("SELECT * FROM guilds WHERE guild_id=" + gID);
			channel.next();
			long cID = channel.getLong(2);
			ResultSet data = stmt.executeQuery("SELECT * FROM " + gID + "_msg WHERE message_id=" + event.getMessageIdLong());
			if(data.next()) {
				String msg = event.getMessage().getContentDisplay(); 
				List<Attachment> attach = event.getMessage().getAttachments();
				if (attach.size() == 1) {
					msg = attach.get(0).getProxyUrl();
				}
				event.getGuild().getTextChannelById(cID).sendMessage(msgBuilder(event.getMessage(), true, data.getLong(2), data.getString(3), data.getString(4), event.getJDA()).build()).queue();
				//update with edited new msg
				PreparedStatement ps = con.prepareStatement("UPDATE " + gID + "_msg SET message_id=?, author_id=?, channel_name=?, msg_text=?, status=? WHERE message_id=" + event.getMessageId());
				ps.setLong(1, event.getMessageIdLong());
				ps.setLong(2, event.getAuthor().getIdLong());
				ps.setString(3, event.getChannel().getName());
				ps.setString(4, msg);
				ps.setBoolean(5, false);
				ps.executeUpdate();
			}
		}
	}
	
	public static void messageDelete(MessageDeleteEvent event, Connection con) throws SQLException {
		long gID = event.getGuild().getIdLong();
		Statement stmt = con.createStatement();
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet rs = dbm.getTables(null, null, Long.toString(gID) + "_msg", null); //check tables
		if(rs.next()) {
			ResultSet channel = stmt.executeQuery("SELECT * FROM guilds WHERE guild_id=" + gID);
			channel.next();
			long cID = channel.getLong(2);
			ResultSet data = stmt.executeQuery("SELECT * FROM " + gID + "_msg WHERE message_id=" + event.getMessageIdLong());
			if(data.next()) { //if msg was found
				EmbedBuilder builder = msgBuilder(null, false, data.getLong(2), data.getString(3), data.getString(4), event.getJDA());
				if(builder != null) { //if user isn't banned and no other servers in common
					event.getGuild().getTextChannelById(cID).sendMessage(builder.build()).queue();
				}
				stmt.executeUpdate("UPDATE " + gID + "_msg SET status=true WHERE message_id=" + event.getMessageId()); //deleted status
			}
		}
	}
	
	public static EmbedBuilder msgBuilder(Message newMsg, boolean edit, long userID, String channel, String display, JDA jda) {
		EmbedBuilder builder = null;
		User author = jda.getUserById(userID);
		if(author != null) {
			builder = new EmbedBuilder();
			if (!edit) {
				builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getAvatarUrl() != null ? author.getAvatarUrl() : author.getDefaultAvatarUrl());
				builder.setTitle("Message deleted in #" + channel);
				builder.setDescription(display);
			} else {
				List<Attachment> attach = newMsg.getAttachments();
				String msg = newMsg.getContentDisplay();
				if (attach.size() == 1) {
					msg = attach.get(0).getUrl();
				}
				builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getAvatarUrl());
				builder.setTitle("Message edited in #" + channel);
				builder.addField("Before:", display, false);
				builder.addField("After:", msg, false);
			}
			builder.setTimestamp(Instant.now());
			builder.setColor(Color.CYAN);
		}
		return builder;
	}
}
