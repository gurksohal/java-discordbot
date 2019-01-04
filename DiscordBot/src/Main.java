
import java.sql.SQLException;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class Main {
	static JDA jda;

	public static void main(String[] args) throws LoginException, SQLException {
		jda = new JDABuilder("key").build();
		jda.addEventListener(new Events());
	}
}