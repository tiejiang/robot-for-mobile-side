package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.webkit.URLUtil;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yinyutech.xiaolerobot.bean.UserInfo;
import com.yinyutech.xiaolerobot.config.AppConfig;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


@SuppressWarnings("deprecation")
public class SoundBoxServiceAction {
	/********************************************************/
	/* 音箱常量定义 */

	public static String BoxService_PLAY_LIST = "PLAY_LIST";
	public static String BoxService_PLAY_ONE_SONG = "PLAY_ONE_SONG";

	// 收藏操作
	public static String BoxService_Get_IndividuationList = "Get_IndividuationList";
	public static String BoxService_Add_IndividuationList = "Add_IndividuationList";
	public static String BoxService_Delete_IndividuationList = "Del_IndividuationList";

	// 音箱播放器状态切换
	public static String BoxService_Media_Idle = "Media_Idle";
	public static String BoxService_Media_Play = "Media_Play";
	public static String BoxService_Media_Pause = "Media_Pause";

	// 歌曲切换
	public static String BoxService_Song_Prev = "Song_Prev";
	public static String BoxService_Song_Next = "Song_Next";

	private static final int InvalidListId = -99999;
	public  static int BoxMaxVolume=15;
	/********************************************************/

	// 音箱播放状态
	private JSONArray musicList = new JSONArray();
	private int currentIndex = 0;
	private int currentMusicDuration = 0;
	private int musicPlayedTime = 0;
	private int currentVolume = 50;
	private String currentMusicState = BoxService_Media_Idle;
	private int queryFailedCount = 0;
	private int listId = InvalidListId;
	private boolean currentMusicFavorite = false;
	private Bitmap currentMusicBitmap;

	public static final String ChecksumSignFavorite = "8500";
	public static final String ChecksumSignHistory = "8900";
	public static final String ChecksumSignLocalMusic = "9100";

	private static final long invalidChecksum = -1;
	private long favoriteChecksum = invalidChecksum;
	private long historyChecksum = invalidChecksum;
	private long localMusicChecksum = invalidChecksum;
	private JSONArray favoriteList;
	private JSONArray historyList;
	private JSONArray localMusicList;

	public Bitmap getCurrentMusicBitmap() {
		return currentMusicBitmap;
	}

	public void setCurrentMusicBitmap(Bitmap currentMusicBitmap) {
		this.currentMusicBitmap = currentMusicBitmap;
	}

	public boolean isCurrentMusicFavorite() {
		return currentMusicFavorite;
	}

	public JSONArray getMusicList() {
		return musicList;
	}

	public int getMusicListCount() {
		if (musicList == null)
			return 0;

		return musicList.length();
	}

	public JSONObject getCurrentMusic() {
		if (musicList == null)
			return new JSONObject();

		try {
			if (0 <= currentIndex && currentIndex < musicList.length()) {
				JSONObject music = (JSONObject) musicList.get(currentIndex);
				return music;
			}
		} catch (JSONException ex) {
			// do nothing
		}

		return new JSONObject();
	}

	public int getMusicPlayedTime() {
		return musicPlayedTime;
	}

	public boolean isPlayingMusic() {
		return currentMusicState.equals(BoxService_Media_Play);
	}

	public int getMusiclistId()
	{
		return this.listId;
	}


	public String getCurrentMusicState() {
		return currentMusicState;
	}

	public void setCurrentMusicState(String currentMusicState) {
		this.currentMusicState = currentMusicState;
	}

	private int getCurrentVolume() {
		if (0 <= currentVolume && currentVolume <= 15)
			return currentVolume;

		return 8;
	}

	public int getCurrentVolumeLevel() {
		//int volume = getCurrentVolume();
		//int maxVolumeLevel = UiUtils.getMaxVolumeLevel(context);
		//int volumeLevel = (int)((float)volume / 100.0 * maxVolumeLevel);
		//return volumeLevel;
		return getCurrentVolume();
	}

	public String getCurrentMusicTitle() {
		String title = getCurrentMusic().optString("musictitle");
		if (title != null && title.length() != 0)
			return title;

		return "无可播放节目";
	}

	public String getCurrentAlbumURLPath() {
		return getCurrentMusic().optString("musicimgurl");
	}

	public String getCurrentMusicLrc() {
		return getCurrentMusic().optString("musiclrcurl");
	}

	public int getMusicTotalTime() {
		return getMusicTotalTime(300);
	}

	public int getMusicTotalTime(int fallbacks) {
		if (musicList == null)
			return 0;

		int duration = 0;
		if (currentMusicDuration > 0) {
			duration = currentMusicDuration;
		} else {
			try {
				if (0 <= currentIndex && currentIndex < musicList.length()) {
					JSONObject musicObj = musicList.getJSONObject(currentIndex);
					duration = musicObj.optInt("musicduration");
				}
			} catch (Exception ex) {
				// do nothing
			}
		}

		if (duration <= 0 || 36000 <= duration)
			duration = fallbacks;

		return duration;
	}

	public float getMusicPlayedProgress() {
		int total = getMusicTotalTime();
		if (musicPlayedTime <= 0 || total <= 0)
			return 0;

		return ((float)musicPlayedTime) / ((float)total);
	}

	public String getMusicTimeString(int seconds) {
		if (seconds < 0) {
			return "--:--";
		} else if (seconds > 60) {
			int minutes = seconds / 60;
			seconds = seconds % 60;
			return String.format("%02d:%02d", minutes, seconds);
		} else {
			return String.format("00:%02d", seconds);
		}
	}

	private final long kQueryTimeInterval = 1000;//3000;  outman 尝试加快试试效果
	private Handler queryInfoHandler = new Handler();
//	private Runnable queryInfoRunnable = new Runnable() {
//		@Override
//		public void run() {
//			// 定时查询810音箱状态，如果列表有变化，再调用900查询播放列表信息
//			if (SoundBoxManager.getInstance().isBoxConnected()) {
//				if (listId == InvalidListId || musicList == null || musicList.length() <= 0) {
//					SoundBoxServiceAction.this.queryPlayingList(new SoundBoxServiceActionListener() {
//						@Override
//						public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//							queryInfoHandler.removeCallbacks(queryInfoRunnable);
//							queryInfoHandler.postDelayed(queryInfoRunnable, kQueryTimeInterval);
//						}
//					});
//				} else {
//					SoundBoxServiceAction.this.queryPlayingInfo(new SoundBoxServiceActionListener() {
//						@Override
//						public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//							queryInfoHandler.removeCallbacks(queryInfoRunnable);
//							queryInfoHandler.postDelayed(queryInfoRunnable, kQueryTimeInterval);
//						}
//					});
//				}
//
//			} else {
//				queryInfoHandler.removeCallbacks(queryInfoRunnable);
//				queryInfoHandler.postDelayed(queryInfoRunnable, kQueryTimeInterval);
//			}
//		}
//	};

	// 轮询音箱状态
//	public void startQueryMusicPlayingInfo() {
//		queryInfoHandler.removeCallbacks(queryInfoRunnable);
//		queryInfoHandler.post(queryInfoRunnable);
//	}

	// 播放音乐
	public void playMusic(final JSONArray list, final int index, final SoundBoxServiceActionListener listener) {
		final JSONArray playList;
		//outman 在音乐播放界面，音乐列表的dialog，发送的命令改成了  PLAY ONE SONG的方式，减少数据发送的量
		String mode;
		if (list == null) {
			playList = musicList;
			mode=BoxService_PLAY_ONE_SONG;
		}
		else
		{
			playList = list;
			mode=BoxService_PLAY_LIST;
		}

		if (playList == null || playList.length() <= 0)
			return;

		JSONObject json = new JSONObject();
		int tempDuration = 0;
		try {
			json.put("sign", "920");
			json.put("beginindex", index);

			//json.put("mode", playList.length() > 1 ? BoxService_PLAY_LIST : BoxService_PLAY_ONE_SONG);
			json.put("mode",mode);
			if(mode.equals(BoxService_PLAY_ONE_SONG))
				json.put("playmusiclist", "null");
			else
				json.put("playmusiclist", playList);

			JSONObject tempMusic = playList.getJSONObject(index);
			tempDuration = tempMusic.optInt("musicduration");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		final int finalDuration = tempDuration;
		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
				boolean success = false;
				if (status) {
					String respcode = responseJSONObj.optString("respcode");

					if (respcode != null && respcode.equals("921")) {
						success = true;
					}
				}

				if (success) {
					updateLocalMusicStatus(
							playList,
							InvalidListId,
							BoxService_Media_Play,
							false,
							index,
							0,
							finalDuration,
							false);
				}
			}
		}, listener);
	}

	// 获取智能音箱的状态
	public void queryBoxServerStatus(
			final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "830");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
				boolean success = false;
				if (status) {
					String respcode = responseJSONObj.optString("respcode");

					if (respcode != null && respcode.equals("831")) {
						success = true;
					}
				}
			}
		}, listener);
	}

	public void signIn(final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "840");
			json.put("customid", UserInfo.sharedUserInfo().customid);
			json.put("username", UserInfo.sharedUserInfo().username);
			json.put("password", UserInfo.sharedUserInfo().password);
			json.put("mobile", UserInfo.sharedUserInfo().mobile);
			json.put("email", UserInfo.sharedUserInfo().email);
			json.put("registerip", UserInfo.sharedUserInfo().registerip);
			json.put("registerdate", UserInfo.sharedUserInfo().registerdate);
			json.put("lastupdatelogin", UserInfo.sharedUserInfo().lastupdatelogin);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
				boolean success = false;
				if (status) {
					String respcode = responseJSONObj.optString("respcode");

					if (respcode != null && respcode.equals("841")) {
						success = true;
					}
				}

				if (listener != null)
					listener.onFinish(success, responseJSONObj, isNetworkingFailure);
			}
		}, null);
	}

	public void sayHelloToBox(final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "110");
			json.put("verifymsg", "hello");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		soundBoxServiceWapper(json, null, listener);
	}

	// 音箱音量调节
	public void controlVolumeLevel(int volumeLevel,
							  final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();

		int maxVolumeLevel = BoxMaxVolume;//UiUtils.getMaxVolumeLevel(context);
		if (volumeLevel < 0)
			volumeLevel = 0;
		if (maxVolumeLevel < volumeLevel)
			volumeLevel = maxVolumeLevel;

		//int volume = Math.round((float)volumeLevel / maxVolumeLevel * 100);
		try {
			json.put("sign", "860");
			json.put("volumeValue", volumeLevel);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		soundBoxServiceWapper(json, null, listener);
	}

	// 音箱播放器状态切换
	// Media_Idle || Media_Play || Media_Pause
//	public void pauseResumeBoxPlaying(final boolean isPause,
//								   final SoundBoxServiceActionListener listener) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("sign", "870");
//			json.put("playstate", isPause ? BoxService_Media_Pause : BoxService_Media_Play);
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				boolean success = false;
//				if (status) {
//					String respcode = responseJSONObj.optString("respcode");
//
//					if (respcode != null && respcode.equals("871")) {
//						success = true;
//					}
//				}
//
//				if (success) {
//					SoundBoxServiceAction.this.currentMusicState = isPause ? BoxService_Media_Pause : BoxService_Media_Play;
//					EventBus.getDefault().post(new SoundBoxStatusChangedEvent());
//				}
//			}
//		}, listener);
//	}

	public void jumpToSeconds(final int seconds,
							  final SoundBoxServiceActionListener listener) {
		if (seconds < 0)
			return;

		JSONObject json = new JSONObject();
		try {
			json.put("sign", "930");
			json.put("seektopostion", seconds);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
				boolean success = false;
				if (status) {
					String respcode = responseJSONObj.optString("respcode");

					if (respcode != null && respcode.equals("931")) {
						success = true;
					}
				}
			}
		}, listener);
	}

	// 歌曲切换
	// Song_Prev || Song_Next
	public void switchSong(boolean isNext,
						   final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "880");
			json.put("songPrevOrNext", isNext ? BoxService_Song_Next : BoxService_Song_Prev);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
			}
		}, listener);
	}

	// 根据用户id获取当前播放歌曲信息
//	public void queryPlayingInfo(final SoundBoxServiceActionListener listener) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("sign", "810");
//			json.put("customid", UserInfo.sharedUserInfo().customid);
//
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		Log.d("810", "start=============================");
//		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				boolean success = false;
//				if (status) {
//					String respcode = responseJSONObj.optString("respcode");
//					Log.d("810 resp code", respcode != null ? respcode : "null");
//
//					if (respcode != null && respcode.equals("811")) {
//						success = true;
//					}
//				}
//
//				if (success) {
//					SoundBoxServiceAction.this.currentVolume = responseJSONObj.optInt("curValueVol");
//					int listId = responseJSONObj.optInt("listid", InvalidListId);
//
//					// 列表有变化，调900查询新的列表
//					if (listId != InvalidListId && SoundBoxServiceAction.this.listId != listId) {
//						SoundBoxServiceAction.this.queryPlayingList(listener);
//					} else {
//						updateLocalMusicStatus(
//								SoundBoxServiceAction.this.musicList,
//								responseJSONObj.optInt("listid", InvalidListId),
//								responseJSONObj.optString("musicstate"),
//								responseJSONObj.optBoolean("isfav", false),
//								responseJSONObj.optInt("index"),
//								responseJSONObj.optInt("seekto"),
//								responseJSONObj.optInt("musicduration"),
//								isNetworkingFailure);
//					}
//				} else {
//					updateLocalMusicStatus(new JSONArray(), InvalidListId, BoxService_Media_Idle, false, 0, 0, 0, isNetworkingFailure);
//				}
//
//				Log.d("810 music state", SoundBoxServiceAction.this.currentMusicState);
//			}
//		}, listener);
//	}

	// 获取歌曲当前播放列表
	public void queryPlayingList(final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "900");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		Log.d("900", "start=============================");
		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
			@Override
			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
				boolean success = false;
				if (status) {
					// 901，获取成功；902，获取失败；903，播放列表为空
					String respcode = responseJSONObj.optString("respcode");
					Log.d("900 resp code", respcode != null ? respcode : "null");

					if (respcode != null && respcode.equals("901")) {
						success = true;
					}
				}

				if (success) {
					SoundBoxServiceAction.this.currentVolume = responseJSONObj.optInt("curValueVol");

					updateLocalMusicStatus(
							responseJSONObj.optJSONArray("playlist"),
							responseJSONObj.optInt("listid", InvalidListId),
							responseJSONObj.optString("musicstate"),
							responseJSONObj.optBoolean("isfav", false),
							responseJSONObj.optInt("index"),
							responseJSONObj.optInt("seekto"),
							responseJSONObj.optInt("musicduration"),
							isNetworkingFailure);
				} else {
					updateLocalMusicStatus(new JSONArray(), InvalidListId, BoxService_Media_Idle, false, 0, 0, 0, isNetworkingFailure);
				}

				Log.d("900 music state", SoundBoxServiceAction.this.currentMusicState);
			}
		}, listener);
	}

	private void updateLocalMusicStatus(JSONArray musicList, int listId, String musicState, boolean isFavorite, int index, int playedTime, int duration, boolean isNetworkingFailure) {


		// 900失败3次才认为音箱连接断开
		if (isNetworkingFailure) {
			queryFailedCount++;
		} else {
			queryFailedCount = 0;
			this.musicList = musicList;//outman 发现有时会超时，到时 failed，歌曲的相关信息只有在成功的时候，才需要更新
			if (this.musicList == null)
				this.musicList = new JSONArray();

			this.listId = listId;
			this.currentMusicState = musicState;
			this.currentMusicFavorite = isFavorite;
			this.currentIndex = index;
			this.musicPlayedTime = playedTime;
			this.currentMusicDuration = duration;
//			EventBus.getDefault().post(new SoundBoxStatusChangedEvent());
		}
		if (queryFailedCount >= 3) {
			SoundBoxManager.getInstance().setBoxConnected(false);
		}
	}

	public interface UICallback {
		void onStart();
		void onFinish();
	}
	public interface MusicListCallback {
		void onQueryListResult(boolean sucess, JSONArray musicList);
	}

	// 获取历史播放列表
//	public void queryHistory(final UICallback uiCallback, final MusicListCallback listCallback) {
//		checkMusicListChecksumStatus(ChecksumSignHistory, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				if (status) {
//					if (uiCallback != null)
//						uiCallback.onStart();
//
//					JSONObject json = new JSONObject();
//					try {
//						json.put("sign", "890");
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//					soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//						@Override
//						public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//							boolean success = false;
//							if (status) {
//								String respcode = responseJSONObj.optString("respcode");
//
//								if (respcode != null && respcode.equals("891")) {
//									success = true;
//								}
//							}
//
//							if (listCallback != null) {
//								historyList = responseJSONObj.optJSONArray("histroylist");
//								listCallback.onQueryListResult(success, historyList);
//							}
//
//							if (uiCallback != null)
//								uiCallback.onFinish();
//						}
//					}, null);
//				} else {
//					if (listCallback != null) {
//						listCallback.onQueryListResult(true, historyList);
//					}
//				}
//			}
//		});
//	}

	// 获取音箱本地音乐列表
//	public void queryBoxLocalMusicList(final UICallback uiCallback, final MusicListCallback listCallback) {
//		checkMusicListChecksumStatus(ChecksumSignLocalMusic, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				if (status) {
//					if (uiCallback != null)
//						uiCallback.onStart();
//
//					JSONObject json = new JSONObject();
//					try {
//						json.put("sign", "910");
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//					soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//						@Override
//						public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//							boolean success = false;
//							if (status) {
//								String respcode = responseJSONObj.optString("respcode");
//
//								// 911，获取成功；912，获取失败；913，本地列表为空
//								if (respcode != null && respcode.equals("911")) {
//									success = true;
//								}
//							}
//
//							if (listCallback != null) {
//								localMusicList = responseJSONObj.optJSONArray("localmusiclist");
//								listCallback.onQueryListResult(success, localMusicList);
//							}
//
//							if (uiCallback != null)
//								uiCallback.onFinish();
//						}
//					}, null);
//				} else {
//					if (listCallback != null) {
//						listCallback.onQueryListResult(true, localMusicList);
//					}
//				}
//			}
//		});
//	}

	// 获取收藏列表
//	public void queryFavouriteList(final UICallback uiCallback, final MusicListCallback listCallback) {
//		checkMusicListChecksumStatus(ChecksumSignFavorite, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				if (status) {
//					if (uiCallback != null)
//						uiCallback.onStart();
//
//					JSONObject json = new JSONObject();
//					try {
//						json.put("sign", "850");
//						json.put("customid", UserInfo.sharedUserInfo().customid);
//						json.put("operatortype", BoxService_Get_IndividuationList);
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//					soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//						@Override
//						public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//							boolean success = false;
//							if (status) {
//								String respcode = responseJSONObj.optString("respcode");
//
//								if (respcode != null && respcode.equals("851")) {
//									success = true;
//								}
//							}
//
//							if (listCallback != null) {
//								favoriteList = responseJSONObj.optJSONArray("favoritesList");
//								listCallback.onQueryListResult(success, favoriteList);
//							}
//
//							if (uiCallback != null)
//								uiCallback.onFinish();
//						}
//					}, null);
//				} else {
//					if (listCallback != null) {
//						listCallback.onQueryListResult(true, favoriteList);
//					}
//				}
//			}
//		});
//	}

//	private void checkMusicListChecksumStatus(final String sign, final SoundBoxServiceActionListener listener) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("sign", sign);
//			json.put("customid", UserInfo.sharedUserInfo().customid);
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				boolean needReload = false;
//				if (status) {
//					long checksum = responseJSONObj.optLong("checksum", invalidChecksum);
//					if (sign.equals(ChecksumSignFavorite) && favoriteChecksum != checksum) {
//						favoriteChecksum = checksum;
//						needReload = true;
//					} else if (sign.equals(ChecksumSignHistory) && historyChecksum != checksum) {
//						historyChecksum = checksum;
//						needReload = true;
//					} else if (sign.equals(ChecksumSignLocalMusic) && localMusicChecksum != checksum) {
//						localMusicChecksum = checksum;
//						needReload = true;
//					}
//				}
//				if ((sign.equals(ChecksumSignFavorite) && favoriteList == null) ||
//						(sign.equals(ChecksumSignHistory) && historyList == null) ||
//						(sign.equals(ChecksumSignLocalMusic) && localMusicList == null)) {
//					needReload = true;
//				}
//
//				if (listener != null) {
//					listener.onFinish(needReload, new JSONObject(), false);
//				}
//			}
//		}, null);
//	}

	// 收藏操作
	// Get_IndividuationList || Add_IndividuationList || Play_IndividuationList
//	public void toggleFavourite(final boolean addFavorite, final SoundBoxServiceActionListener listener) {
//		String operator = addFavorite ? BoxService_Add_IndividuationList : BoxService_Delete_IndividuationList;
//		currentMusicFavorite = addFavorite;
//
//		JSONObject json = new JSONObject();
//		try {
//			json.put("sign", "850");
//			json.put("customid", UserInfo.sharedUserInfo().customid);
//			json.put("operatortype", operator);
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		soundBoxServiceWapper(json, new SoundBoxServiceActionListener() {
//			@Override
//			public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
//				boolean success = false;
//				if (status) {
//					String respcode = responseJSONObj.optString("respcode");
//
//					if (respcode != null && respcode.equals("851")) {
//						success = true;
//					}
//				}
//			}
//		}, listener);
//	}

	// 音箱本地歌曲歌词
	public void getLocalMusicLyrics(final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "8100");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		soundBoxServiceWapper(json, null, listener);
	}

	// 音箱本地歌曲封面图片
	public void getLocalMusicAlbum(final SoundBoxServiceActionListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("sign", "8103");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		soundBoxServiceWapper(json, null, listener);
	}

	private SoundBoxServiceAction() {
	}

	private static SoundBoxServiceAction single = null;
	private Context context;

	public synchronized static SoundBoxServiceAction getInstance() {
		if (null == single) {
			single = new SoundBoxServiceAction();
		}
		return single;
	}

	public void setupContext(Context context) {
		this.context = context;
	}

	public interface SoundBoxServiceActionListener {
		void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure);
	}

	// Private methods
	private String encryptData(String origin) {
		String rtnData = null;

		byte[] bData = EncryptUtils.encryptStr(origin, AppConfig.SecretKey);
		rtnData = EncryptUtils.bytesToHexString(bData);
		return rtnData;
	}

	private String decryptData(String data) {
		String rtnData = null;

		byte[] bData = EncryptUtils.hexStringToBytes(data);
		bData = EncryptUtils.decrypt(bData, AppConfig.SecretKey);
		try {
			rtnData = new String(bData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return rtnData;
	}

	private void soundBoxServiceWapper(JSONObject jsonRequest,
									   final SoundBoxServiceActionListener internalHandler,
									   final SoundBoxServiceActionListener listener) {
		SoundBoxManager boxManager = SoundBoxManager.getInstance();
		String boxHost = boxManager.getBoxHost();

		if (boxHost == null || !URLUtil.isValidUrl(boxHost)) {
			if (internalHandler != null) {
				internalHandler.onFinish(false, null, false);
			}
			if (listener != null) {
				listener.onFinish(false, null, false);
			}

			return;
		}

		String reqStr = jsonRequest.toString();
		// 加密 & 数据转换
		String data = encryptData(reqStr);
		Log.e("soundBoxServiceWapper",reqStr);
		SoundBoxHttpClient.post(context, boxHost, data, false,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						Log.v("soundBoxServiceWapper", ":onFailure");

						if (internalHandler != null) {
							internalHandler.onFinish(false, new JSONObject(), true);
						}
						if (listener != null) {
							listener.onFinish(false, new JSONObject(), true);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						Log.v("soundBoxServiceWapper", ":onSuccess");

						String responseData = null;
						try {
							responseData = new String(arg2, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if (responseData != null) {
							// 解密 & 数据转换
							String decryptedData = decryptData(responseData);
							JSONObject responseJSONObj = null;
							try {
								responseJSONObj = new JSONObject(decryptedData);
							} catch (JSONException ex) {
							}

							if (responseJSONObj == null)
								responseJSONObj = new JSONObject();

							boolean success = false;
							if (responseJSONObj.optString("sign").equals("1000")) {
								success = true;
							}

							if (internalHandler != null) {
								internalHandler.onFinish(success, responseJSONObj, false);
							}
							if (listener != null) {
								listener.onFinish(success, responseJSONObj, false);
							}
						} else {
							if (internalHandler != null) {
								internalHandler.onFinish(false, new JSONObject(), false);
							}
							if (listener != null) {
								listener.onFinish(false, new JSONObject(), false);
							}
						}
					}

				});
	}
}
