package com.t0p47.mendeley.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t0p47.mendeley.app.AppConfig;
import com.t0p47.mendeley.model.Folder;
import com.t0p47.mendeley.model.JournalArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 01 on 07.10.2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "LOG_TAG";

    private static final int DATABASE_VERSION = 1;

    //DB name
    private static final String DATABASE_NAME = "01";

    private Context _context;


    private static final String TABLE_FOLDERS = "folders";
    private static final String TABLE_ARTICLES = "journal_articles";

    //Common keys
    private static final String KEY_LOCAL_ID = "local_id";
    private static final String KEY_GLOBAL_ID = "global_id";

    //Folders table
    private static final String KEY_FOLDER_TITLE = "title";
    private static final String KEY_FOLDER_PARENT_ID = "parent_id";

    //Articles(journal_articles) table
    private static final String KEY_ARTICLE_TITLE = "title";
    private static final String KEY_ARTICLE_AUTHORS = "authors";
    private static final String KEY_ARTICLE_ABSTRACT = "abstract";
    private static final String KEY_ARTICLE_JOURNAL_ID = "journal_id";
    private static final String KEY_ARTICLE_VOLUME = "volume";
    private static final String KEY_ARTICLE_ISSUE = "issue";
    private static final String KEY_ARTICLE_YEAR = "year";
    private static final String KEY_ARTICLE_PAGES = "pages";
    private static final String KEY_ARTICLE_ARXIVID = "ArXivID";
    private static final String KEY_ARTICLE_DOI = "DOI";
    private static final String KEY_ARTICLE_PMID = "PMID";
    private static final String KEY_ARTICLE_FOLDER = "folder";
    private static final String KEY_ARTICLE_FILEPATH = "filepath";
    private static final String KEY_ARTICLE_CREATED_AT = "created_at";
    private static final String KEY_ARTICLE_UPDATED_AT = "updated_at";
    private static final String KEY_ARTICLE_FAVORITE = "favorite";

    private static final String KEY_IS_NEW = "is_new";
    private static final String KEY_IS_CHANGE = "is_change";
    private static final String KEY_IS_DELETE = "is_delete";


    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLES_TABLE = "CREATE TABLE "+TABLE_ARTICLES+"("
                +KEY_LOCAL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +KEY_GLOBAL_ID+" INTEGER DEFAULT 0,"+KEY_ARTICLE_TITLE+" TEXT,"
                +KEY_ARTICLE_AUTHORS+" TEXT,"+KEY_ARTICLE_ABSTRACT+" TEXT DEFAULT NULL,"
                +KEY_ARTICLE_JOURNAL_ID +" INTEGER,"+KEY_ARTICLE_VOLUME+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_ISSUE+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_YEAR+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_PAGES+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_ARXIVID+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_DOI+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_PMID+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_CREATED_AT+" TEXT,"+KEY_ARTICLE_UPDATED_AT+" TEXT,"
                +KEY_ARTICLE_FAVORITE+" INTEGER DEFAULT 0,"+KEY_IS_NEW+" INTEGER DEFAULT 0,"
                +KEY_ARTICLE_FOLDER+" INTEGER DEFAULT 0,"+KEY_ARTICLE_FILEPATH+" TEXT DEFAULT NULL,"
                +KEY_IS_CHANGE+" INTEGER DEFAULT 0," +KEY_IS_DELETE+" INTEGER DEFAULT 0"+")";
        db.execSQL(CREATE_ARTICLES_TABLE);

        String CREATE_FOLDERS_TABLE = "CREATE TABLE "+TABLE_FOLDERS+"("
                +KEY_LOCAL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_GLOBAL_ID+" INTEGER DEFAULT 0,"+KEY_FOLDER_TITLE+" TEXT,"
                +KEY_FOLDER_PARENT_ID+" INTEGER DEFAULT 0,"+KEY_IS_NEW+" INTEGER DEFAULT 0,"
                +KEY_IS_CHANGE+" INTEGER DEFAULT 0,"
                +KEY_IS_DELETE+" INTEGER DEFAULT 0"+")";
        db.execSQL(CREATE_FOLDERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void recreateAllTables(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FOLDERS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ARTICLES);

        onCreate(db);

        db.close();
    }



    public List<Folder> getAllFolders(){
        List<Folder> folders = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT "+KEY_LOCAL_ID+","+KEY_GLOBAL_ID+","+KEY_FOLDER_TITLE+","+KEY_FOLDER_PARENT_ID+" FROM "+TABLE_FOLDERS;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Folder folder = new Folder(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cursor.getInt(3));
                folders.add(folder);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return folders;
    }

    public void recreateAllFolders(List<Folder> foldersList){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;

        for(Folder folder : foldersList){
            values = new ContentValues();

            int newParentId = getFolderLocalIdByGlobal(folder.getParent_id());
            values.put(KEY_FOLDER_PARENT_ID,newParentId);

            values.put(KEY_GLOBAL_ID, folder.getGlobal_id());
            values.put(KEY_FOLDER_TITLE, folder.getTitle());

            db.insert(TABLE_FOLDERS, null, values);
        }
    }

    public void recreateAllArticles(List<JournalArticle> articlesList){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;

        for(JournalArticle article : articlesList){
            values = new ContentValues();

            //Меняем глобальные id папок на локальные
            article.setFolder(getFolderGlobalIdByLocal(article.getFolder()));

            values.put(KEY_GLOBAL_ID, article.getGlobal_id());
            values.put(KEY_ARTICLE_TITLE, article.getTitle());
            values.put(KEY_ARTICLE_AUTHORS, article.getAuthors());
            values.put(KEY_ARTICLE_ABSTRACT, article.getAbstractField());
            values.put(KEY_ARTICLE_JOURNAL_ID, article.getJournal());
            values.put(KEY_ARTICLE_VOLUME, article.getVolume());
            values.put(KEY_ARTICLE_ISSUE, article.getIssue());
            values.put(KEY_ARTICLE_YEAR, article.getYear());
            values.put(KEY_ARTICLE_PAGES, article.getPages());
            values.put(KEY_ARTICLE_ARXIVID, article.getArXivID());
            values.put(KEY_ARTICLE_DOI, article.getDOI());
            values.put(KEY_ARTICLE_PMID, article.getPMID());
            values.put(KEY_ARTICLE_CREATED_AT, article.getCreated_at());
            values.put(KEY_ARTICLE_UPDATED_AT, article.getUpdated_at());
            values.put(KEY_ARTICLE_FAVORITE,article.getFavorite());
            values.put(KEY_ARTICLE_FOLDER, article.getFolder());
            values.put(KEY_ARTICLE_FILEPATH, article.getFilepath());

            db.insert(TABLE_ARTICLES,null,values);
            Log.d(TAG,"DatabaseHandler: article inserted. Title: "+article.getTitle());
        }
    }

    public long addLocalFolder(String title, int parentFolderId){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_TITLE, title);
        values.put(KEY_FOLDER_PARENT_ID, parentFolderId);
        values.put(KEY_IS_NEW, 1);

        long newFolderId = db.insert(TABLE_FOLDERS,null,values);

        return newFolderId;
    }

    public long addLocalArtilcle(JournalArticle article){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ARTICLE_TITLE,article.getTitle());
        values.put(KEY_ARTICLE_AUTHORS, article.getAuthors());
        values.put(KEY_ARTICLE_ABSTRACT, article.getAbstractField());
        values.put(KEY_ARTICLE_JOURNAL_ID, article.getJournal());
        values.put(KEY_ARTICLE_VOLUME, article.getVolume());
        values.put(KEY_ARTICLE_ISSUE, article.getIssue());
        values.put(KEY_ARTICLE_YEAR, article.getYear());
        values.put(KEY_ARTICLE_PAGES,article.getPages());
        values.put(KEY_ARTICLE_ARXIVID,article.getArXivID());
        values.put(KEY_ARTICLE_DOI, article.getDOI());
        values.put(KEY_ARTICLE_PMID, article.getPMID());
        values.put(KEY_ARTICLE_CREATED_AT, article.getCreated_at());
        values.put(KEY_ARTICLE_UPDATED_AT, article.getCreated_at());
        values.put(KEY_ARTICLE_FOLDER, article.getFolder());
        values.put(KEY_IS_NEW,1);

        long newArticleId = db.insert(TABLE_ARTICLES,null,values);

        return newArticleId;
    }

    public void changeFolderTitle(int local_id, String folderTitle){

        //TODO: Перенести WordLearner на GitLab
        //TODO: Если папка которую мы переименовываем is_new, то is_change всегда=0
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_TITLE,folderTitle);

        db.update(TABLE_FOLDERS, values, KEY_LOCAL_ID+" = ?",new String[] {String.valueOf(local_id)});

    }

    public void deleteParentFolder(int child_local_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String SelectQuery = "SELECT "+KEY_FOLDER_PARENT_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_LOCAL_ID+"="+child_local_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        if(cursor.moveToFirst()){
            int parent_local_id = cursor.getInt(0);
            deleteFolder(parent_local_id);
        }

    }

    public void deleteFolder(int local_id){

        //TODO::Не показывать папки у которых is_delete=1
        if(checkHaveSubfolders(local_id)){
            deleteSubfolders(local_id);
        }else{
            moveArticlesToHomeDir(local_id);
            deleteFolderQuery(local_id);
            Log.d(TAG,"DatabaseHolder: Папка удалена");
        }


    }

    public void deleteGlobalFolder(int local_id){
        if(checkHaveSubfolders(local_id)){
            deleteGlobalSubfolders(local_id);
        }else{
            moveArticlesToHomeDir(local_id);
            deleteGlobalFolderQuery(local_id);
            Log.d(TAG,"DatabaseHolder: Папка удалена");
        }
    }

    private void deleteGlobalSubfolders(int parent_local_id){
        SQLiteDatabase db = this.getReadableDatabase();

        String SelectQuery = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_FOLDER_PARENT_ID+"="+parent_local_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        //Есть подпапки у удаляемой папки
        if(cursor.getCount()>0){
            cursor.moveToFirst();

            do{
                int subfolderLocalId = cursor.getInt(0);
                //Есть подпапки
                if(checkHaveSubfolders(subfolderLocalId)){
                    deleteGlobalSubfolders(subfolderLocalId);
                    deleteGlobalFolderQuery(subfolderLocalId);
                    //Нет подпапок
                }else{
                    moveArticlesToHomeDir(subfolderLocalId);
                    deleteGlobalFolderQuery(subfolderLocalId);
                    Log.d(TAG,"DatabaseHolder: Папка удалена");
                }
            }
            while(cursor.moveToNext());
            //Нет подпапки у удаляемой папки
        }else{
            moveArticlesToHomeDir(parent_local_id);
            deleteGlobalFolderQuery(parent_local_id);
        }
    }

    private void deleteSubfolders(int parent_local_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String SelectQuery = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_FOLDER_PARENT_ID+"="+parent_local_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        //Есть подпапки у удаляемой папки
        if(cursor.getCount()>0){
            cursor.moveToFirst();

            do{
                int subfolderLocalId = cursor.getInt(0);
                //Есть подпапки
                if(checkHaveSubfolders(subfolderLocalId)){
                    deleteSubfolders(subfolderLocalId);
                    deleteFolderQuery(subfolderLocalId);
                    //Нет подпапок
                }else{
                    moveArticlesToHomeDir(subfolderLocalId);
                    deleteFolderQuery(subfolderLocalId);
                    Log.d(TAG,"DatabaseHolder: Папка удалена");
                }
            }
            while(cursor.moveToNext());
        //Нет подпапки у удаляемой папки
        }else{
            moveArticlesToHomeDir(parent_local_id);
            deleteFolderQuery(parent_local_id);
        }


    }

    private void moveArticlesToHomeDir(int local_id){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ARTICLE_FOLDER,0);

        db.update(TABLE_ARTICLES, values, KEY_LOCAL_ID+" = ?",
                new String[] {String.valueOf(local_id)});
    }

    private void deleteFolderQuery(int local_id){

        SQLiteDatabase db = this.getWritableDatabase();

        //Если папка новая(создана только локально)
        if(checkFolderIsNew(local_id)){
            db.delete(TABLE_FOLDERS, KEY_LOCAL_ID+" = ?",
                    new String[] {String.valueOf(local_id)});
            db.close();
        //Если папка взята с сервера
        }else{
            ContentValues values = new ContentValues();
            values.put(KEY_IS_DELETE,1);

            db.update(TABLE_FOLDERS, values, KEY_LOCAL_ID+" = ?",
                    new String[] {String.valueOf(local_id)});
        }
    }

    private void deleteGlobalFolderQuery(int local_id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FOLDERS, KEY_LOCAL_ID+" = ?",
                new String[] {String.valueOf(local_id)});
    }

    private boolean checkHaveSubfolders(int local_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String SelectQuery = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_FOLDER_PARENT_ID+"="+local_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        if(cursor.getCount()>0){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    private boolean checkFolderIsNew(int local_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String SelectQuery = "SELECT "+KEY_IS_NEW+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_LOCAL_ID+"="+local_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        cursor.moveToFirst();

        if(cursor.getInt(0)==1){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }

    }

    public void disableIsNewFolder(){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IS_NEW,0);

        db.update(TABLE_FOLDERS, values, KEY_IS_NEW+" = ?",
                new String[] {String.valueOf(1)});
    }

    public void disableIsRenameFolder(){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IS_CHANGE,0);

        db.update(TABLE_FOLDERS, values, KEY_IS_CHANGE+" = ?",
                new String[] {String.valueOf(1)});

    }

    public int checkCreateRenameFolder(JSONObject globalFolderData){

        SQLiteDatabase db = this.getReadableDatabase();


        int global_id=0;
        String is_rename = null;
        String folderName = null;
        int globalParentId = 0;
        try {
            global_id = globalFolderData.getInt("id");
            is_rename = globalFolderData.getString("is_rename");
            folderName = globalFolderData.getString("name");
            globalParentId = globalFolderData.getInt("parent_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String SelectQuery = "SELECT "+KEY_LOCAL_ID+","+KEY_FOLDER_TITLE+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_GLOBAL_ID+"="+global_id;

        Cursor cursor = db.rawQuery(SelectQuery,null);

        //Значит папка есть и на локальном устройстве
        if(cursor.moveToFirst()){
            //Изменено название папки
            if(is_rename.equals("server") || is_rename.equals("windows")){
                changeFolderTitle(cursor.getInt(0),folderName);
                return AppConfig.GLOBAL_FOLDER_RENAMED;
            }else{
                return AppConfig.GLOBAL_FOLDER_EXIST;
            }
        //Папки нет на этом локальном устройстве
        }else{
            addGlobalFolder(global_id, folderName, globalParentId);
            return AppConfig.GLOBAL_FOLDER_NEW;
        }

    }



    private void addGlobalFolder(int global_id, String folderName, int globalParentId){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GLOBAL_ID,global_id);
        values.put(KEY_FOLDER_TITLE,folderName);

        //Меняет parent_id на локальный
        int newParentId = getFolderLocalIdByGlobal(globalParentId);
        values.put(KEY_FOLDER_PARENT_ID,newParentId);

        db.insert(TABLE_FOLDERS,null, values);

    }

    private int getFolderGlobalIdByLocal(int local_id){

        if(local_id==0){
            return 0;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT "+KEY_GLOBAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_LOCAL_ID+"="+local_id;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            return cursor.getInt(0);
        }else{
            return -1;
        }
    }

    private int getFolderLocalIdByGlobal(int global_id){

        if(global_id==0){
            return 0;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_GLOBAL_ID+"="+global_id;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            return cursor.getInt(0);
        }else{
            return -1;
        }
    }

    public void addGlobalArticle(JSONObject articleObj){

        SQLiteDatabase db = this.getWritableDatabase();

        int global_id = 0;
        String title = null;
        String authors = null;
        String abstractStr = null;
        String journal_id = null;
        int volume = 0;
        int issue = 0;
        int year = 0;
        int pages = 0;
        int ArXivID = 0;
        int DOI = 0;
        int PMID = 0;
        int folder = 0;
        int favorite = 0;
        String created_at = null;
        String updated_at = null;
        try{
            global_id = articleObj.getInt("id");
            title = articleObj.getString("title");
            authors = articleObj.getString("authors");
            abstractStr = articleObj.getString("abstract");
            journal_id = articleObj.getString("journal_id");

            volume = 0;
            if(!articleObj.get("volume").equals(null)){
                volume = articleObj.getInt("volume");
            }
            issue = 0;
            if (!articleObj.get("issue").equals(null)){
                issue = articleObj.getInt("issue");
            }

            year = 0;
            if(!articleObj.get("year").equals(null)){
                year = articleObj.getInt("year");
            }

            pages = 0;
            if(!articleObj.get("pages").equals(null)){
                pages = articleObj.getInt("pages");
            }

            ArXivID = 0;
            if(!articleObj.get("ArXivID").equals(null)){
                ArXivID = articleObj.getInt("ArXivID");
            }

            DOI = 0;
            if(!articleObj.get("DOI").equals(null)){
                DOI = articleObj.getInt("DOI");
            }

            PMID = 0;
            if(!articleObj.get("PMID").equals(null)){
                PMID = articleObj.getInt("PMID");
            }

            folder = 0;
            if(!articleObj.get("folder").equals(null)){
                folder = articleObj.getInt("folder");
            }

            //TODO:Поле favorite у JournalArticle
            /*favorite = 0;
            if(!articleObj.get("favorite").equals(null)){
                favorite = articleObj.getInt("favorite");
            }*/

            folder = getFolderLocalIdByGlobal(articleObj.getInt("folder"));
            created_at = articleObj.getString("created_at");
            updated_at = articleObj.getString("updated_at");
        }catch(JSONException e){
            e.printStackTrace();

            Log.d(TAG,"DatabaseHandler: addGlobalArticle exception "+e.getMessage());
        }

        ContentValues values = new ContentValues();
        values.put(KEY_GLOBAL_ID,global_id);
        values.put(KEY_ARTICLE_TITLE,title);
        values.put(KEY_ARTICLE_AUTHORS,authors);
        values.put(KEY_ARTICLE_ABSTRACT,abstractStr);
        values.put(KEY_ARTICLE_JOURNAL_ID,journal_id);
        values.put(KEY_ARTICLE_VOLUME, volume);
        values.put(KEY_ARTICLE_ISSUE,issue);
        values.put(KEY_ARTICLE_YEAR,year);
        values.put(KEY_ARTICLE_PAGES,pages);
        values.put(KEY_ARTICLE_ARXIVID,ArXivID);
        values.put(KEY_ARTICLE_DOI,DOI);
        values.put(KEY_ARTICLE_PMID,PMID);
        values.put(KEY_ARTICLE_FOLDER,folder);
        values.put(KEY_ARTICLE_CREATED_AT, created_at);
        values.put(KEY_ARTICLE_UPDATED_AT, updated_at);

        db.insert(TABLE_ARTICLES,null,values);

    }

    public void deleteLocalArticle(int local_id){


        SQLiteDatabase db = this.getReadableDatabase();

        /*
        * db.delete(TABLE_FOLDERS, KEY_LOCAL_ID+" = ?",
                    new String[] {String.valueOf(local_id)});
        * */

        if(isArticleNew(local_id)){
            db.delete(TABLE_ARTICLES,KEY_LOCAL_ID+" = ?",
                    new String[] {String.valueOf(local_id)});
        }else{
            ContentValues values = new ContentValues();

            values.put(KEY_IS_DELETE,1);

            db.update(TABLE_ARTICLES,values, KEY_LOCAL_ID+" = ?",new String[] {String.valueOf(local_id)});
        }

    }

    public void deleteGlobalArticle(int global_id){

        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(TABLE_ARTICLES,KEY_GLOBAL_ID+" = ?", new String[] {String.valueOf(global_id)});

    }

    private boolean isArticleNew(int local_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT "+KEY_IS_NEW+" FROM "+TABLE_ARTICLES+" WHERE "+KEY_LOCAL_ID+"="+local_id;

        Cursor cursor = db.rawQuery(selectQuery,null);

        //Если статья новая, возвращаем true
        if(cursor.moveToFirst()){
            return cursor.getInt(0)==1;
        }else{
            return false;
        }

    }


    public void setGlobalIdToArticle(int local_id, int global_id){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_GLOBAL_ID, global_id);

        db.update(TABLE_ARTICLES, values, KEY_LOCAL_ID+" = ?", new String[] {String.valueOf(local_id)});

        /*
        * ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_TITLE,folderTitle);

        db.update(TABLE_FOLDERS, values, KEY_LOCAL_ID+" = ?",new String[] {String.valueOf(local_id)});
        * */

    }

    public List<JournalArticle> getRootFolderArticles(){

        List<JournalArticle> articlesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_Query = "SELECT "+KEY_LOCAL_ID+","+KEY_ARTICLE_TITLE+","+KEY_ARTICLE_AUTHORS+","+KEY_ARTICLE_JOURNAL_ID
                +","+KEY_ARTICLE_CREATED_AT+","+KEY_ARTICLE_FAVORITE+","+KEY_ARTICLE_FILEPATH+" FROM "+TABLE_ARTICLES+" WHERE "+KEY_ARTICLE_FOLDER+"="+0;

        Cursor cursor = db.rawQuery(SELECT_Query,null);
        if(cursor.moveToFirst()){
            do{
                int local_id = cursor.getInt(0);
                String title = cursor.getString(1);
                String author = cursor.getString(2);
                String journal = cursor.getString(3);
                String created_at = cursor.getString(4);
                int favorite = cursor.getInt(5);
                String filepath = cursor.getString(6);

                JournalArticle article = new JournalArticle(local_id,title,author,journal,created_at,favorite,filepath);
                articlesList.add(article);
            }while (cursor.moveToNext());
        }

        return articlesList;
    }

    public List<JournalArticle> getArticlesInFolder(int folder_local_id){

        List<JournalArticle> articlesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_Query = "SELECT "+KEY_LOCAL_ID+","+KEY_ARTICLE_TITLE+","+KEY_ARTICLE_AUTHORS+","+KEY_ARTICLE_JOURNAL_ID
                +","+KEY_ARTICLE_CREATED_AT+","+KEY_ARTICLE_FAVORITE+","+KEY_ARTICLE_FILEPATH+" FROM "+TABLE_ARTICLES+" WHERE "+KEY_ARTICLE_FOLDER+"="+folder_local_id;

        Cursor cursor = db.rawQuery(SELECT_Query,null);
        if(cursor.moveToFirst()){
            do{
                int local_id = cursor.getInt(0);
                String title = cursor.getString(1);
                String author = cursor.getString(2);
                String journal = cursor.getString(3);
                String created_at = cursor.getString(4);
                int favorite = cursor.getInt(5);
                String filepath = cursor.getString(6);

                JournalArticle article = new JournalArticle(local_id,title,author,journal,created_at,favorite,filepath);
                articlesList.add(article);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return articlesList;

    }

    public int getArticlesCount(){
        int articlesCount = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_Query = "SELECT "+KEY_LOCAL_ID +" FROM "+TABLE_ARTICLES;

        Cursor cursor = db.rawQuery(SELECT_Query,null);

        articlesCount = cursor.getCount();

        return articlesCount;
    }

    public String composeJSONFromFolders(){
        ArrayList<HashMap<String,String>> foldersList;
        foldersList = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+TABLE_FOLDERS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> map = new HashMap<>();
                if(cursor.getInt(cursor.getColumnIndex(KEY_IS_NEW))==1 || cursor.getInt(cursor.getColumnIndex(KEY_IS_CHANGE))==1 ||
                        cursor.getInt(cursor.getColumnIndex(KEY_IS_DELETE))==1){
                    map.put(KEY_IS_NEW, cursor.getString(cursor.getColumnIndex(KEY_IS_NEW)));
                    map.put(KEY_IS_CHANGE, cursor.getString(cursor.getColumnIndex(KEY_IS_CHANGE)));
                    map.put(KEY_IS_DELETE, cursor.getString(cursor.getColumnIndex(KEY_IS_DELETE)));
                    map.put("name",cursor.getString(cursor.getColumnIndex(KEY_FOLDER_TITLE)));
                    map.put("local_id",cursor.getString(cursor.getColumnIndex(KEY_LOCAL_ID)));
                    map.put("global_id",cursor.getString(cursor.getColumnIndex(KEY_GLOBAL_ID)));
                    map.put("parent_id",cursor.getString(cursor.getColumnIndex(KEY_FOLDER_PARENT_ID)));
                    foldersList.add(map);
                }else{
                    map.put("local_id",cursor.getString(cursor.getColumnIndex(KEY_LOCAL_ID)));
                    map.put("global_id",cursor.getString(cursor.getColumnIndex(KEY_GLOBAL_ID)));
                    map.put("parent_id",cursor.getString(cursor.getColumnIndex(KEY_FOLDER_PARENT_ID)));
                    foldersList.add(map);
                }



            }while(cursor.moveToNext());
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(foldersList);
    }

    public String composeJSONFromArticles(){

        ArrayList<HashMap<String,String>> articlesList;
        articlesList = new ArrayList<>();

        //String SelectQuery = "SELECT * FROM "+TABLE_ARTICLES + " WHERE "+KEY_GLOBAL_ID+"=0";
        String SelectQuery = "SELECT * FROM "+TABLE_ARTICLES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SelectQuery,null);
        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> map = new HashMap<>();
                //Созданные локально(не синхронизированные)
                if(cursor.getInt(cursor.getColumnIndex(KEY_IS_NEW))==1){
                    map.put(KEY_LOCAL_ID,cursor.getString(cursor.getColumnIndex(KEY_LOCAL_ID)));
                    int global_folder_parent_id = getFolderGlobalIdByLocal(cursor.getInt(cursor.getColumnIndex(KEY_ARTICLE_FOLDER)));
                    map.put(KEY_ARTICLE_TITLE,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_TITLE)));
                    map.put(KEY_ARTICLE_AUTHORS,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_AUTHORS)));
                    map.put(KEY_ARTICLE_ABSTRACT,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_ABSTRACT)));
                    map.put(KEY_ARTICLE_JOURNAL_ID,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_JOURNAL_ID)));
                    map.put(KEY_ARTICLE_YEAR,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_YEAR)));
                    map.put(KEY_ARTICLE_VOLUME,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_VOLUME)));
                    map.put(KEY_ARTICLE_ISSUE,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_ISSUE)));
                    map.put(KEY_ARTICLE_PAGES,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_PAGES)));
                    map.put(KEY_ARTICLE_ARXIVID,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_ARXIVID)));
                    map.put(KEY_ARTICLE_DOI,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_DOI)));
                    map.put(KEY_ARTICLE_PMID,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_PMID)));
                    map.put(KEY_ARTICLE_FOLDER, String.valueOf(global_folder_parent_id));
                    map.put(KEY_ARTICLE_UPDATED_AT,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_UPDATED_AT)));
                //Сверить глобальные статьи
                }else{
                    //TODO:TMP удалить следующую строку
                    map.put(KEY_ARTICLE_TITLE,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_TITLE)));

                    map.put(KEY_GLOBAL_ID,cursor.getString(cursor.getColumnIndex(KEY_GLOBAL_ID)));
                    map.put(KEY_ARTICLE_UPDATED_AT,cursor.getString(cursor.getColumnIndex(KEY_ARTICLE_UPDATED_AT)));
                    map.put(KEY_IS_DELETE,cursor.getString(cursor.getColumnIndex(KEY_IS_DELETE)));
                }

                articlesList.add(map);
            }while (cursor.moveToNext());
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(articlesList);
    }
}
