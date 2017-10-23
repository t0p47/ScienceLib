package com.t0p47.mendeley.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.t0p47.mendeley.model.Folder;
import com.t0p47.mendeley.model.JournalArticle;

import java.util.ArrayList;
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
    private static final String KEY_ARTICLE_MID = "mid";
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
                +KEY_ARTICLE_MID+" INTEGER DEFAULT 0,"+KEY_ARTICLE_TITLE+" TEXT,"
                +KEY_ARTICLE_AUTHORS+" TEXT,"+KEY_ARTICLE_ABSTRACT+" TEXT DEFAULT NULL,"
                +KEY_ARTICLE_JOURNAL_ID +" INTEGER,"+KEY_ARTICLE_VOLUME+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_ISSUE+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_YEAR+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_PAGES+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_ARXIVID+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_DOI+" INTEGER DEFAULT NULL,"+KEY_ARTICLE_PMID+" INTEGER DEFAULT NULL,"
                +KEY_ARTICLE_CREATED_AT+" TEXT,"+KEY_ARTICLE_FAVORITE+" INTEGER DEFAULT 0,"
                +KEY_ARTICLE_FOLDER+" INTEGER DEFAULT 0,"+KEY_ARTICLE_FILEPATH+" TEXT DEFAULT NULL,"
                +KEY_IS_NEW +" INTEGER DEFAULT 0,"+KEY_IS_CHANGE+" INTEGER DEFAULT 0,"
                +KEY_IS_DELETE+" INTEGER DEFAULT 0"+")";
        db.execSQL(CREATE_ARTICLES_TABLE);

        String CREATE_FOLDERS_TABLE = "CREATE TABLE "+TABLE_FOLDERS+"("
                +KEY_LOCAL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_GLOBAL_ID+" INTEGER DEFAULT 0,"+KEY_FOLDER_TITLE+" TEXT,"
                +KEY_FOLDER_PARENT_ID+" INTEGER DEFAULT 0,"
                +KEY_IS_NEW +" INTEGER DEFAULT 0,"+KEY_IS_CHANGE+" INTEGER DEFAULT 0,"
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

        //public Folder(int local_id, int global_id, String title, int parent_id)

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
            if(folder.getParent_id()!=0){
                int oldParent_id = folder.getParent_id();
                String searchNewParent = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_GLOBAL_ID+"="+oldParent_id;
                Cursor cursor = db.rawQuery(searchNewParent,null);

                int newParentId = 0;

                if(cursor.moveToFirst()){
                    newParentId = cursor.getInt(0);
                }

                values.put(KEY_FOLDER_PARENT_ID, newParentId);
            }else{
                values.put(KEY_FOLDER_PARENT_ID, 0);
            }
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
            if(article.getFolder()!= 0){
                String SELECT_Query = "SELECT "+KEY_LOCAL_ID+" FROM "+TABLE_FOLDERS+" WHERE "+KEY_GLOBAL_ID+"="+article.getFolder();
                Cursor cursor = db.rawQuery(SELECT_Query,null);
                if(cursor.moveToFirst()){
                    article.setFolder(cursor.getInt(0));
                }
                cursor.close();
            }
            values.put(KEY_ARTICLE_MID, article.getGlobal_id());
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
            values.put(KEY_ARTICLE_FAVORITE,article.getFavorite());
            values.put(KEY_ARTICLE_FOLDER, article.getFolder());
            values.put(KEY_ARTICLE_FILEPATH, article.getFilepath());

            db.insert(TABLE_ARTICLES,null,values);
            Log.d(TAG,"DatabaseHandler: article inserted. Title: "+article.getTitle());
        }
    }

    public void testAddFolders(){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_TITLE,"FirstFolder");
        db.insert(TABLE_FOLDERS,null,values);
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
}
