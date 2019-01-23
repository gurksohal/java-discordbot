package bot.commands;


import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CoinFlip {
	static Random gen = new Random();

	public static void messageReveived(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (message.equalsIgnoreCase("!cf")) {
			int rand = gen.nextInt(100);
			Message msg = event.getChannel().sendMessage("Flipping").complete();
			String coin = rand <= 49 ? "Heads" : "Tails";
			msg.editMessage(coin).queueAfter(3, TimeUnit.SECONDS);
		}
	}
}
