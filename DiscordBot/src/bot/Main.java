package bot;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class Main {
	
	public static String fortKey = "key";
	static String discordKey = "key";
	static JDA jda;
	
	public static void main(String[] args) throws LoginException, SQLException, FileNotFoundException {
		jda = new JDABuilder(discordKey).build();
		jda.addEventListener(new Events());
		jda.getPresence().setGame(Game.playing("!nhelp for list of commands."));
	}
}