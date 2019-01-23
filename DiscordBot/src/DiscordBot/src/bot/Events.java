package bot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import bot.commands.CoinFlip;
import bot.commands.Help;
import bot.commands.chartsCommand;
import bot.fornite.FortniteStats;
import bot.fornite.shop.FortniteShop;
import bot.msgtrack.MessageTrack;
import bot.msgtrack.Stats;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {
	Connection con;

	public Events() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord", "root", "");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().getIdLong() == 327184654232977418L && event.getMessage().getAttachments().size() > 0) {
			event.getMessage().delete().queue();
			System.out.println("Deleted diamonds msg at: " + Calendar.getInstance().getTime());
		}else
		if (event.getPrivateChannel() == null) {
			try {
				MessageTrack.messageReceived(event, con);
				Stats.messageReceived(event, con);
				FortniteShop.messageReceived(event);
				FortniteStats.messageReceived(event);
			} catch (SQLException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}

			chartsCommand.messageReveived(event);
			CoinFlip.messageReveived(event);
			Help.messageReceived(event);
		}
	}

	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		if (event.getPrivateChannel() == null) {
			try {
				MessageTrack.messageDelete(event, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		if (event.getPrivateChannel() == null) {
			try {
				MessageTrack.messageUpdate(event, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onReady(ReadyEvent event) {
		try {
			MessageTrack.onReady(event, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		try {
			MessageTrack.onJoin(event, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
