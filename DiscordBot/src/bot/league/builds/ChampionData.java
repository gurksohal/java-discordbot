package bot.league.builds;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//load build data from u.gg
public class ChampionData {
	static boolean running = false;

	public static void messageReceived(MessageReceivedEvent event) throws IOException {
		if (event.getMessage().getContentDisplay().equalsIgnoreCase("!updatebuilds")
				&& event.getAuthor().getIdLong() == 134139515156561920L) {
			// run asyn
			new Thread(() -> {
				if (!running) {
					running = true;
					Stopwatch timer = Stopwatch.createStarted();
					event.getChannel().sendMessage("Started updating...").queue();
					try {
						getData();
					} catch (IOException e) {
						e.printStackTrace();
					}
					event.getChannel().sendMessage("Took " + timer.stop() + " to get data.").queue();
					running = false;
				} else {
					event.getChannel().sendMessage("Already working on it").queue();
				}
			}).start();
		}
	}

	private static void getData() throws IOException {
		new File("TEMP").mkdir(); // make dir if it doesnt exist
		File folder = new File("TEMP");
		FileUtils.cleanDirectory(folder);
		JsonReader jsonReader = new JsonReader(new FileReader(new File("ChampionNames.txt")));
		ArrayList<String> champNames = new Gson().fromJson(jsonReader, ArrayList.class);
		String[] roles = { "", "?role=top", "?role=jungle", "?role=mid", "?role=adc", "?role=support" };
		for (String name : champNames) {
			System.out.println("STARTING: " + name.toUpperCase());
			String newFolderPath = folder.getAbsolutePath() + "/" + name.toUpperCase();
			new File(newFolderPath).mkdir();
			File champFolder = new File(newFolderPath);
			for (int x = 0; x < roles.length; x++) {
				JBrowserDriver driver = new JBrowserDriver(
						Settings.builder().timezone(Timezone.AMERICA_CHICAGO).build());
				driver.get("https://u.gg/lol/champions/" + name.toLowerCase() + "/build/" + roles[x]);
				Document doc = Jsoup.parse(driver.getPageSource());
				FileWriter write = new FileWriter(createFile(x, champFolder.getAbsolutePath()));
				new Gson().toJson(new JSONChampion(doc), write);
				write.flush();
				write.close();
				System.out.println("\t" + roles[x] + " done. For " + name.toUpperCase());
				driver.quit();
			}
			System.out.println("DONE: " + name.toUpperCase());
		}
		FileUtils.deleteDirectory(new File("DATA"));
		FileUtils.moveDirectory(folder, new File("DATA"));
	}

	private static File createFile(int x, String path) {
		File returnFile;
		switch (x) {
		case 0:
			returnFile = new File(path + "/DEFAULT.json");
			break;
		case 1:
			returnFile = new File(path + "/TOP.json");
			break;
		case 2:
			returnFile = new File(path + "/JUNGLE.json");
			break;
		case 3:
			returnFile = new File(path + "/MID.json");
			break;
		case 4:
			returnFile = new File(path + "/ADC.json");
			break;
		case 5:
			returnFile = new File(path + "/SUPPORT.json");
			break;
		default:
			returnFile = null;
		}

		return returnFile;
	}
}
