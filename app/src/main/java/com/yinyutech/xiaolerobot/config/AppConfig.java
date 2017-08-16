package com.yinyutech.xiaolerobot.config;

public class AppConfig {
	public enum MenuType {
		MenuTypeQingTing, MenuTypeMiGu, MenuTypeSoundBox, MenuTypeBoxLocalMusic, MenuTypeFavorite, MenuTypeHistory, MenuTypeConfig
	}

	public enum MusicListType {
		Play, Favorite, Local, History
	}

	public static String SecretKey = "123123123";

	public static String QingTingServiceClientID = "MjA3OTJiNDQtZWQ4Ny0xMWU0LTkyM2YtMDAxNjNlMDAyMGFk";
	public static String QingTingServiceClientSecret = "M2YxOTIyY2MtYmJhZi0zZmE2LWJkM2YtMzA5N2ZmNmQ0NTkx";

	public static int QingServiceMaxPageSize = 30;
	
	public static String LogTag ="SoundBoxLogTag";

	public static String miguMusicH5URL = "http://m.12530.com/order/wapExclusive/gjh5/index.html";
}
