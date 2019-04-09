package bot.msgtrack;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Stats {

	public static void messageReceived(MessageReceivedEvent event, Connection con) throws SQLException {
		String[] cmds = event.getMessage().getContentDisplay().split(" ");
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet rs = dbm.getTables(null, null, Long.toString(event.getGuild().getIdLong()) + "_msg", null); //to check if any messages have been stored
		if (cmds.length >= 2 && cmds[0].equalsIgnoreCase("!msgs") && rs.next()) {
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getIdLong() == 134139515156561920L) {
				Long userID = event.getMessage().getMentionedMembers().get(0).getUser().getIdLong();
				UserMsgs msgs = getData(event.getGuild().getIdLong(), userID, con);
				event.getChannel().sendMessage(msgBuild(event.getJDA().getUserById(userID), msgs).build()).queue();
			}else {
				event.getChannel().sendMessage("only admins may use this command").queue();
			}
		}
	}

	private static UserMsgs getData(long guildID, long userID, Connection con) throws SQLException {
		TreeMap<Long, UserMsgs> totalMsg = new TreeMap<Long, UserMsgs>();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + guildID + "_msg");
		while (rs.next()) {
			UserMsgs val = totalMsg.get(rs.getLong(2)); // current val
			if (val == null) { // if no key create new one
				val = new UserMsgs();
			}
			if (rs.getBoolean(5)) { // add deleted msg to array
				val.msg.add(rs.getString(4));
			}
			val.totalMsgs++;
			totalMsg.put(rs.getLong(2), val); // add the updated object to the right user
		}
		return totalMsg.get(userID);
	}

	private static EmbedBuilder msgBuild(User author, UserMsgs msgs) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getAvatarUrl());
		if (msgs != null) {
			builder.addField("Total messages sent: ", Integer.toString(msgs.totalMsgs), false);
			builder.addField("Total messages deleted: ", Integer.toString(msgs.msg.size()), false);
			if (msgs.msg.size() != 0) {
				String text = "";
				int index = 1;
				for (String m : msgs.msg) {
					text += index++ + ") " + m + "\n";
				}
				builder.addField("Deleted message History:", text, false);
			}
		}else {
			builder.setTitle("No messages sent", null);
		}
		return builder;
	}

}

class UserMsgs {
	public int totalMsgs;
	public ArrayList<String> msg;

	public UserMsgs() {
		totalMsgs = 0;
		msg = new ArrayList<String>();
	}
}
