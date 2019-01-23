package bot.fornite.shop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopData {
	String date;
	ShopItem[] items;
	ArrayList<ShopItem> feature;
	ArrayList<ShopItem> daily;
	public ShopData(String d,ShopItem[] data) {
		date = d;
		items = data;
	}
	
	public void makeArray() {
		feature = new ArrayList<ShopItem>();
		daily = new ArrayList<ShopItem>();
		for(ShopItem item : items) {
			if(item.item.rarity.equals("legendary")) {
				item.sortOrder = 0;
			}else if(item.item.rarity.equals("epic")) {
				item.sortOrder = 1;
			}else if(item.item.rarity.equals("rare")) {
				item.sortOrder = 2;
			}else if(item.item.rarity.equals("uncommon")) {
				item.sortOrder = 3;
			}
			
			if(item.featured == 1) {
				feature.add(item);
			}else {
				daily.add(item);
			}
		}
		Collections.sort(feature, new rareCompare());
		Collections.sort(daily, new rareCompare());
	}

}

class ShopItem {
	int featured;
	ItemData item;
	
	int sortOrder;
	public ShopItem(int b,ItemData i) {
		featured = b;
		item = i;
		
	}
}

class ItemData {
	ItemImageData images;
	String rarity;
	public ItemData(ItemImageData data,String rare) {
		images = data;
		rarity = rare;
	}
}

class ItemImageData{
	String information;
	public ItemImageData(String url) {
		information = url;
	}
}

class rareCompare implements Comparator<ShopItem>{

	@Override
	public int compare(ShopItem o1, ShopItem o2) {
		return o1.sortOrder - o2.sortOrder;
	}
	
}