
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {
	Connection con;
	public Events() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord","root","");
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
			MessageTrack.messageReceived(event, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		chartsCommand.messageReveived(event);
		CoinFlip.messageReveived(event);
	}

	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		try {
			MessageTrack.messageDelete(event, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		try {
			MessageTrack.messageUpdate(event, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onReady(ReadyEvent event) {
		try {
			MessageTrack.onReady(event,con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Timer task = new Timer();
		task.scheduleAtFixedRate(new TimerTask() {
			public void run() {
			}
		}, 0, TimeUnit.SECONDS.toMillis(10));
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
