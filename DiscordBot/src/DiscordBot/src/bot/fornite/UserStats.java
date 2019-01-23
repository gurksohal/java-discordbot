package bot.fornite;

public class UserStats {
	String accountId;
	String platformName;
	String epicUserHandle;
	OverallStats stats;
	Stats[] lifeTimeStats;
	
	public UserStats(String id, String platform, String user, OverallStats stat, Stats[] lfstats) {
		accountId = id;
		platformName = platform;
		epicUserHandle = user;
		stats = stat;
		lifeTimeStats = lfstats;
	}
}

class OverallStats {
	ModeStats p2; //solo 
	ModeStats p10; //duo
	ModeStats p9; //squads
	ModeStats curr_p2;
	ModeStats curr_p10;
	ModeStats curr_p9;
	
	public OverallStats(ModeStats p2, ModeStats p10, ModeStats p9, ModeStats curr_p2, ModeStats curr_p10, ModeStats curr_p9) {
		this.p2 = p2;
		this.p10 = p10;
		this.p9 = p9;
		this.curr_p2 = curr_p2;
		this.curr_p10 = curr_p10;
		this.curr_p9 = curr_p9;
	}
	
}

class ModeStats{
	Statinfo top1;
	Statinfo winRatio;
	Statinfo matches;
	Statinfo kills;
	Statinfo kpg;
	
	public ModeStats(Statinfo win,Statinfo winRate,Statinfo match,Statinfo kills) {
		top1 = win;
		winRatio = winRate;
		matches = match;
		kpg = kills;
	}
}

class Statinfo{
	String label;
	String displayValue;
	
	public Statinfo(String l, String dv) {
		label = l;
		displayValue = dv;
	}
}

class Stats{
	String key;
	String value;
	
	public Stats(String k, String v) {
		key = k;
		value = v;
	}
}