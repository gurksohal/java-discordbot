package bot.league;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LeagueTiers {
	private static ArrayList<Champ> top;
	private static ArrayList<Champ> jungle;
	private static ArrayList<Champ> mid;
	private static ArrayList<Champ> adc;
	private static ArrayList<Champ> support;
	private static Instant lastDataGrab = null;
	public static void messageReceived(MessageReceivedEvent event) {
		String msg = event.getMessage().getContentDisplay();
		if (msg.equalsIgnoreCase("!tiers") && lastDataGrab != null) {
			event.getChannel().sendMessage(buildMsg(event.getAuthor()).build()).queue();
		} else if (msg.equalsIgnoreCase("!updatetiers") && event.getAuthor().getIdLong() == 134139515156561920L) {
			getData();
			event.getChannel().sendMessage("Updated. use !tiers to view.").queue();
		}
	}

	public static void onReady() { // only get data when bot first starts
		getData();
		System.out.println("finished getting u.gg");
	}

	private static EmbedBuilder buildMsg(User author) {
		EmbedBuilder builder = new EmbedBuilder();
		String topData = getDisplayString(top);
		String jgData = getDisplayString(jungle);
		String midData = getDisplayString(mid);
		String adcData = getDisplayString(adc);
		String supportData = getDisplayString(support);
		builder.setAuthor(author.getName() + "#" + author.getDiscriminator(), null,
				author.getAvatarUrl() != null ? author.getAvatarUrl() : author.getDefaultAvatarUrl());
		builder.setTitle("LoL Tier List");
		builder.addField("Top:", topData, true);
		builder.addField("Jungle:", jgData, true);
		builder.addField("Mid:", midData, true);
		builder.addField("ADC:", adcData, true);
		builder.addField("Suport:", supportData, true);
		builder.addBlankField(true);
		builder.setFooter("Data from u.gg", null);
		builder.setTimestamp(lastDataGrab);
		builder.setColor(Color.CYAN);
		return builder;
	}

	private static String getDisplayString(ArrayList<Champ> arr) {
		String returnString = "";
		for (Champ champ : arr) {
			returnString += champ.name + " : " + champ.winRate + "\n";
		}
		return returnString;
	}

	private static void getData() {
		lastDataGrab = null;
		top = new ArrayList<Champ>();
		jungle = new ArrayList<Champ>();
		mid = new ArrayList<Champ>();
		adc = new ArrayList<Champ>();
		support = new ArrayList<Champ>();
		Champ spacer = new Champ("---", "");
		top.add(spacer);
		jungle.add(spacer);
		mid.add(spacer);
		adc.add(spacer);
		support.add(spacer);
		
		JBrowserDriver driver = new JBrowserDriver(Settings.builder().timezone(Timezone.AMERICA_CHICAGO).build());
		driver.get("https://u.gg/lol/tier-list/");
		long height = 0;
		long lastHeight = 0;
		do {
			lastHeight = height;
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			height = (long) driver.executeScript("return document.body.scrollHeight;");
		} while (lastHeight != height);
		driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");

		Document doc = Jsoup.parse(driver.getPageSource());
		Elements element = doc.getElementsByClass("rt-tr-group");
		
		for (Element e : element) {
			String tier = e.getElementsByClass("tier").get(0).text();
			if (tier.equals("S+") || tier.equals("S")) {
				String role = e.getElementsByClass("tier-list-role").get(0).attr("alt");
				String name = e.getElementsByClass("champion-name").get(0).text();
				String winRate = e.getElementsByClass("winrate").get(0).text();
				Champ champ = new Champ(name,winRate);
				
				//add to right array, S+ comes first, then S.
				if (role.equals("top")) {
					if (tier.equals("S+")) {
						top.add(0, champ);
					} else {
						top.add(champ);
					}
				} else if (role.equals("jungle")) {
					if (tier.equals("S+")) {
						jungle.add(0, champ);
					} else {
						jungle.add(champ);
					}
				} else if (role.equals("mid")) {
					if (tier.equals("S+")) {
						mid.add(0, champ);
					} else {
						mid.add(champ);
					}
				} else if (role.equals("adc")) {
					if (tier.equals("S+")) {
						adc.add(0, champ);
					} else {
						adc.add(champ);
					}
				} else {
					if (tier.equals("S+")) {
						support.add(0, champ);
					} else {
						support.add(champ);
					}
				}
			}
		}
		driver.quit();
		lastDataGrab = Instant.now();
	}
}

class Champ {
	String name;
	String winRate;
	public Champ(String n, String w) {
		name = n;
		winRate = w;
	}
}
