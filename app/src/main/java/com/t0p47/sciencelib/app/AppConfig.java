package com.t0p47.sciencelib.app;

/**
 * Created by 01 on 11.10.2017.
 */

public class AppConfig {

    public static String URL_SYNC_FOLDERS = "http://45.76.158.9/api/android/sync/folders";

    public static String URL_SYNC_ARTICLES = "http://45.76.158.9/api/android/sync/articles";

    public static String URL_LOGIN_USER = "http://45.76.158.9/api/authenticate";

    public static String URL_REGISTER_USER = "http://45.76.158.9/api/register";

    public static String URL_REFRESH_TOKEN = "http://45.76.158.9/api/refresh-token";

    //TODO: Две следующих ссылки должны быть "http://45.76.158.9/api/android/sync/articles"; "http://45.76.186.7/api/android/sync/folders";
    //А сейчас просто временно

    public static String URL_SEND_FOLDERS = "http://45.76.158.9/api/android/send/folders";

    public static String URL_SEND_ARTICLES = "http://45.76.158.9/api/android/send/articles";

    public static String URL_FOLDER_REQUEST_BACK = "http://45.76.158.9/api/android/folder/request";

    public static String URL_ARTICLE_REQUEST_BACK = "http://45.76.158.9/api/android/article/request";

    public static String URL_RECEIVE_FILE = "http://45.76.158.9/api/windows/receive/file";

    public static String URL_RECEIVE_FILE_TEST = "http://45.76.158.9/api/android/send/fileTest";



    public static int GLOBAL_FOLDER_EXIST = 0;

    public static int GLOBAL_FOLDER_NEW = 1;

    public static int GLOBAL_FOLDER_RENAMED = 2;

}
