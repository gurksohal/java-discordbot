package bot.fornite.shop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FortniteShop {
	static File shopFile;
	static String lastDate = "";
	static String url;
	public static void messageReceived(MessageReceivedEvent event) throws IOException {
		
		if(event.getMessage().getContentDisplay().equalsIgnoreCase("!shop")) {
			if(!lastDate.equals(getCurrentData())) {
				ShopData data = getData();
				shopFile = createImage(data);
				lastDate = data.date;
				Message msg = event.getChannel().sendFile(shopFile).complete();
				url = msg.getAttachments().get(0).getUrl();
			}else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor("Fortnite Shop");
				builder.setColor(Color.ORANGE);
				builder.setImage(url);
				event.getChannel().sendMessage(builder.build()).queue();
			}
		}
	}
	
	private static ShopData getData() throws IOException {
		System.setProperty("http.agent", "Chrome");
		URL url = new URL("https://fortnite-public-api.theapinetwork.com/prod09/store/get?language=en");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		JsonReader jsonReader = new JsonReader(new InputStreamReader(con.getInputStream()));
		ShopData returnData = new Gson().fromJson(jsonReader, ShopData.class);
		returnData.makeArray();
		return returnData;
	}
	
	private static File createImage(ShopData data) throws IOException {
		int width = 1400; //x
		int height = 1450; //y
		int spacing = 25;
		int title_height = 50; 
		int item_width = 250; //x
		int item_height = 300; //y
		System.setProperty("http.agent", "Chrome");
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(ImageIO.read(new File("background.png")), 0, 0, null);
		
		Image featureTag = ImageIO.read(new File("featuredItems.png")).getScaledInstance(width, title_height, Image.SCALE_DEFAULT);
		Image dailyTag = ImageIO.read(new File("dailyItems.png")).getScaledInstance(width, title_height, Image.SCALE_DEFAULT);
		g2d.drawImage(featureTag, 0, 0, null);
		
		if(data.feature.size() > 5) {
			int y = title_height + (3*spacing) + (2*item_height);
			g2d.drawImage(dailyTag, 0, y, null);
		}else {
			int y = title_height + (2*spacing) + item_height;
			g2d.drawImage(dailyTag, 0, y, null);
		}
		int drawX = spacing;
		int drawY = title_height + spacing;
		for(int x = 0; x < data.feature.size(); x++) {
			ShopItem feature = data.feature.get(x);
			Image img = ImageIO.read(new URL(feature.item.images.information)).getScaledInstance(item_width, item_height, Image.SCALE_SMOOTH);
			if(x % 5 == 0 && x != 0) {
				drawY += item_height + spacing;
				drawX = spacing;
			}
			g2d.drawImage(img, drawX, drawY, null);
			drawX += item_width + spacing;
		}
		drawY += spacing + title_height + spacing + item_height;
		drawX = spacing;
		for(int x = 0; x < data.daily.size(); x++) {
			ShopItem daily = data.daily.get(x);
			Image img = ImageIO.read(new URL(daily.item.images.information)).getScaledInstance(item_width, item_height, Image.SCALE_DEFAULT);
			if(x % 5 == 0 && x != 0) {
				drawY += item_height + spacing;
				drawX = spacing;
			}
			g2d.drawImage(img, drawX, drawY, null);
			drawX += item_width + spacing;
		}
		
		File file = new File("fortnite_shop.png");
	    ImageIO.write(bufferedImage, "png", file);
	    return file;
	}
	
	private static String getCurrentData() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(cal.getTime());
	}
}


