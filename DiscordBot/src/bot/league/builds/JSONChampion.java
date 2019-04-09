package bot.league.builds;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSONChampion {

	String patch;
	String champ;
	String role;
	String totalMatches;
	String winRate;
	String pickRate;
	ArrayList<String> startItems;
	ArrayList<String> coreItems;
	ArrayList<String> fourthOptions;
	ArrayList<String> fifthOptions;
	ArrayList<String> sixthOptions;

	public JSONChampion(Document doc) {
		
		if(doc.getElementsByClass("champion-profile-error").size() >= 1){
			return;
		}
		
		role = doc.getElementsByClass("role-value").get(0).selectFirst("img").attr("alt");
		champ = doc.getElementsByClass("champion-name").get(1).ownText();
		Element stats = doc.getElementsByClass("champion-ranking-stats").get(0);
		patch = stats.getElementsByClass("label").get(0).ownText();
		totalMatches = stats.getElementsByClass("matches").get(0).getElementsByClass("value").get(0).ownText();
		winRate = stats.getElementsByClass("win-rate").get(0).getElementsByClass("value").get(0).ownText();
		pickRate = stats.getElementsByClass("pick-rate").get(0).getElementsByClass("value").get(0).ownText();
		
		String[] items = { "starting-items", "final-items", "item-options-1", "item-options-2", "item-options-3" };
		for (int x = 0; x < items.length; x++) {
			Elements e = doc.getElementsByClass(items[x]).get(0).getElementsByClass("image-wrapper");
			switch (x) {
			case 0:
				startItems = getItems(e);
				;
			case 1:
				coreItems = getItems(e);
			case 2:
				fourthOptions = getItems(e);
			case 3:
				fifthOptions = getItems(e);
			case 4:
				sixthOptions = getItems(e);
			default:
				break;
			}

		}
	}
	
	private static ArrayList<String> getItems(Elements items) {
		ArrayList<String> returnArray = new ArrayList<String>();
		for (Element item : items) {
			returnArray.add(item.selectFirst("img").attr("alt"));
		}
		return returnArray;
	}
}
