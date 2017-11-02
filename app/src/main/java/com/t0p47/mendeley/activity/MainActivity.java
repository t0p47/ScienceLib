package com.t0p47.mendeley.activity;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.t0p47.library.model.TreeNode;
import com.t0p47.library.view.AndroidTreeView;
import com.t0p47.mendeley.R;
import com.t0p47.mendeley.adapter.JournalArticleAdapter;
import com.t0p47.mendeley.app.AppConfig;
import com.t0p47.mendeley.app.AppController;
import com.t0p47.mendeley.db.DatabaseHandler;
import com.t0p47.mendeley.decor.DividerItemDecoration;
import com.t0p47.mendeley.dialog.NewArticleDialog;
import com.t0p47.mendeley.dialog.NewFolderDialog;
import com.t0p47.mendeley.helper.Helper;
import com.t0p47.mendeley.helper.SessionManager;
import com.t0p47.mendeley.holder.ArrowExpandSelectableHeaderHolder;
import com.t0p47.mendeley.holder.IconTreeItemHolder;
import com.t0p47.mendeley.interfaces.RecyclerTouchListener;
import com.t0p47.mendeley.model.Folder;
import com.t0p47.mendeley.model.JournalArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener
, NewFolderDialog.NewFolderDialogListener, NewArticleDialog.NewArticleDialogListener {

    //Laptop changes
    private static final String TAG = "LOG_TAG";

    private static final String NAME = "Very long name for forlder";
    private AndroidTreeView treeView;

    Animation FabOpen, FabClose,FabRClockwise, FabRanticlockwise, RefreshClockwise;
    boolean isFabOpen = false;
    MenuItem refreshMenuItem;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FloatingActionButton fabPlus, fabAddFolder, fabAddArticle;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private JournalArticleAdapter mAdapter;

    HashMap<Integer, TreeNode> foldersTreeIds;
    List<Folder> foldersList;
    List<JournalArticle> articlesList;


    private int currentFolderId = 0;
    private int changingFolderId = 0;
    public static int navItemIndex = 0;

    //private List<String> activityTitle;

    private Handler mHandler;
    private DatabaseHandler dbh;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG,"MainActivity: static function: "+ Helper.refreshToken());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(this);
        dbh = new DatabaseHandler(this);
        dbh.recreateAllTables();

        foldersList = dbh.getAllFolders();
        articlesList = dbh.getRootFolderArticles();


        //getFolders();
        //getArticles();


        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabAddFolder = (FloatingActionButton) findViewById(R.id.fab_twitter);
        fabAddArticle = (FloatingActionButton) findViewById(R.id.fab_fb);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Log.d(TAG,"MainActivity articles count "+articlesList.size());

        for(JournalArticle article : articlesList){
            Log.d(TAG,"MainActivity: articles titles "+article.getTitle());
        }

        mAdapter = new JournalArticleAdapter(articlesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                JournalArticle article = articlesList.get(position);
                Toast.makeText(getApplicationContext(), article.getTitle()+" is selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.setAdapter(mAdapter);

        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        RefreshClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise_full);


        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFabOpen){
                    fabAddArticle.startAnimation(FabClose);
                    fabAddFolder.startAnimation(FabClose);
                    fabPlus.startAnimation(FabRanticlockwise);

                    fabAddArticle.setClickable(false);
                    fabAddFolder.setClickable(false);
                    isFabOpen=false;
                }else{
                    fabAddArticle.startAnimation(FabOpen);
                    fabAddFolder.startAnimation(FabOpen);
                    fabPlus.startAnimation(FabRClockwise);

                    fabAddArticle.setClickable(true);
                    fabAddFolder.setClickable(true);
                    isFabOpen=true;
                }
                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });
        
        fabAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewArticleDialog();
            }
        });

        fabAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewFolderDialog();
            }
        });

        setupNavigationView();

        if(savedInstanceState == null){
            navItemIndex=0;
            loadFolder();
        }

    }

    private void showNewArticleDialog(){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        NewArticleDialog newArticleDialog = new NewArticleDialog();
        newArticleDialog.setCancelable(true);
        newArticleDialog.setDialogTitle("Article title");
        newArticleDialog.show(fragmentManager, "InputDialog");
    }

    @Override
    public void onFinishNewArticleDialog(JournalArticle article){

        Toast.makeText(this, "New article name: "+article.getTitle(), Toast.LENGTH_SHORT).show();
        article.setFolder(currentFolderId);
        Date date = new Date();
        //PHP: 2017-09-28 06:57:34
        //JAVA: Tue Oct 24 16:41:50 GMT+07:00 2017
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = dateFormat.format(date);
        Log.d(TAG,"MainActivity: current date: "+formattedDate);
        article.setCreated_at(formattedDate);
        articlesList.add(article);
        mAdapter.notifyDataSetChanged();
        dbh.addArtilcle(article);
    }

    private void showNewFolderDialog() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        NewFolderDialog newFolderDialog = new NewFolderDialog();
        newFolderDialog.setCancelable(true);
        newFolderDialog.setDialogTitle("Folder title");
        newFolderDialog.setIsRename(false);
        newFolderDialog.show(fragmentManager, "InputDialog");
    }

    @Override
    public void onFinishNewFolderDialog(String newFolderTitle){
        Toast.makeText(this, "New folder name: "+newFolderTitle, Toast.LENGTH_SHORT).show();
        createNewFolder(newFolderTitle);
    }

    private void showRenameFolderDialog(String oldFolderTitle) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        NewFolderDialog newFolderDialog = new NewFolderDialog();
        newFolderDialog.setCancelable(true);
        newFolderDialog.setDialogTitle("Rename folder");
        newFolderDialog.setFolderTitle(oldFolderTitle);
        newFolderDialog.setIsRename(true);
        newFolderDialog.show(fragmentManager, "InputDialog");
    }

    @Override
    public void onFinishRenameFolderDialog(String newTitleFolder){
        Toast.makeText(this, "Renamed folder name: "+newTitleFolder, Toast.LENGTH_SHORT).show();
        renameFolder(newTitleFolder);
    }

    private void renameFolder(String folderTitle){
        dbh.changeFolderTitle(changingFolderId, folderTitle);
        TreeNode changingNode = foldersTreeIds.get(changingFolderId);
        TextView nameView = (TextView) changingNode.getViewHolder().getView().findViewById(R.id.node_value);
        nameView.setText(folderTitle);
    }

    private void createNewFolder(String newFolderTitle){
        int newFolderId = (int) dbh.addFolder(newFolderTitle,currentFolderId);
        Folder folder = new Folder(newFolderTitle,currentFolderId);
        folder.setLocal_id(newFolderId);
        foldersList.add(folder);

        TreeNode newTreeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, folder.getTitle(), folder.getLocal_id()));

        foldersTreeIds.put(folder.getLocal_id(),newTreeNode);

        if(currentFolderId==0){
            Log.d(TAG,"MainActivity: another root folder created");
            TreeNode root = TreeNode.root();
            root.addChild(newTreeNode);
        }else{
            treeView.addNode(foldersTreeIds.get(currentFolderId),newTreeNode);
        }
    }

    private void showArticles(){

        Log.d(TAG,"MainActivity: notify adapter. Articles count "+articlesList.size());
        mAdapter = new JournalArticleAdapter(articlesList);
        //mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    private void getArticles(){
        if(dbh.getArticlesCount()==0){
            getFirstTimeArticles();
            Log.d(TAG,"MainActivity: first time articles");
            //TODO: Elseif если в настройках стоит галочка синхронизировать при запуске
        }else{
            //loadArticles();
        }
    }

    private void getFolders(){

        if(foldersList.size()==0){
            getFirstTimeFolders();
            //TODO: Elseif если в настройках стоит галочка синхронизировать при запуске
        }else{
            Log.d(TAG,"MainActivity: folders exists");
            loadFolder();
        }
    }

    private void getFirstTimeFolders(){
        String tag_string_req = "req_folders";

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_SYNC_FOLDERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "MainActivity: response " + response);

                //TODO:Parse folders from server
                try{
                    JSONArray jArr = new JSONArray(response);
                    foldersList  = new ArrayList<>();
                    if(jArr.length()!=0){
                        for(int i = 0;i<jArr.length();i++){
                            Folder folder = new Folder(jArr.getJSONObject(i).getInt("id"),jArr.getJSONObject(i).getString("name"),jArr.getJSONObject(i).getInt("parent_id"));
                            foldersList.add(folder);
                        }
                        dbh.recreateAllFolders(foldersList);
                        foldersList = dbh.getAllFolders();
                        loadFolder();
                    }else{
                        Log.d(TAG,"MainActivity: no folders on server");
                    }


                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Json error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG,"MainActivity: json error "+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode==401 && error.toString().equals("com.android.volley.AuthFailureError")){
                    Log.d(TAG,"MainActivity: need to refresh token");
                    //TODO: Возможно нужно сделать так что бы функция
                    // вызывалась при успешном окончании refreshToken(что то, типа Invoke(Unity))
                    refreshToken();

                }
                Log.e(TAG,"MainActivity: onErrorResponse"+error.getMessage()+", status code "+error.networkResponse.statusCode
                        +", networkResponseData: "+error.toString());
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String,String>();
                String token = "Bearer "+session.getAuthToken();
                headers.put("Authorization",token);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getFirstTimeArticles(){
        String tag_string_req = "req_get_articles";

        pDialog.setMessage("Synching articles...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_SYNC_ARTICLES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "MainActivity: response " + response);

                try{
                    JSONArray jArr = new JSONArray(response);
                    articlesList = new ArrayList<>();
                    if(jArr.length()!=0){
                        for(int i = 0;i<jArr.length();i++){
                            JSONObject jObj = jArr.getJSONObject(i);
                            int global_id = jObj.getInt("id");
                            String title = jObj.getString("title");
                            String authors = jObj.getString("authors");
                            String abstractText = jObj.getString("abstract");
                            String journal = jObj.getString("journal_id");
                            int volume = 0;
                            if(!jObj.get("volume").equals(null)){
                                volume = jObj.getInt("volume");
                            }
                            int issue = 0;
                            if (!jObj.get("issue").equals(null)){
                                issue = jObj.getInt("issue");
                            }

                            int year = 0;
                            if(!jObj.get("year").equals(null)){
                                year = jObj.getInt("year");
                            }

                            int pages = 0;
                            if(!jObj.get("pages").equals(null)){
                                pages = jObj.getInt("pages");
                            }

                            int ArXivID = 0;
                            if(!jObj.get("ArXivID").equals(null)){
                                ArXivID = jObj.getInt("ArXivID");
                            }

                            int DOI = 0;
                            if(!jObj.get("DOI").equals(null)){
                                DOI = jObj.getInt("DOI");
                            }

                            int PMID = 0;
                            if(!jObj.get("PMID").equals(null)){
                                PMID = jObj.getInt("PMID");
                            }

                            int folder = 0;
                            if(!jObj.get("folder").equals(null)){
                                folder = jObj.getInt("folder");
                            }

                            String filepath = jObj.getString("filepath");
                            String created_at = jObj.getString("created_at");

                            int favorite = 0;
                            if(!jObj.get("favorite").equals(null)){
                                favorite = jObj.getInt("favorite");
                            }

                            JournalArticle article = new JournalArticle(global_id, title, authors, abstractText,journal,volume,issue,year,pages,ArXivID,DOI,PMID,folder,filepath,created_at,favorite);
                            articlesList.add(article);
                        }
                        dbh.recreateAllArticles(articlesList);
                        articlesList = dbh.getRootFolderArticles();
                        Log.d(TAG,"MainActivity get articles after response: "+articlesList.size());
                        showArticles();
                    }else{
                        Log.d(TAG,"MainActivity: no articles on server");
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG,"MainActivity: getFirstTimeArticles json error: "+e.getMessage());
                }

                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "MainActivity: getFirstTimeArticle error "+error.getMessage());
                Toast.makeText(getApplicationContext(),"Article sync error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                HashMap<String,String> headers = new HashMap<>();
                String token = "Bearer "+session.getAuthToken();
                headers.put("Authorization",token);
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    private void refreshToken(){
        String tag_string_req = "req_refresh_token";

        pDialog.setMessage("Synchronize... ");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_REFRESH_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "MainActivity: response " + response);
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);

                    String token = jObj.getString("token");

                    session.setAuthToken(token);
                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"MainActivity: error "+error.getMessage()+", error String "+error.toString());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                HashMap<String,String> headers = new HashMap<>();
                String token = "Bearer "+session.getAuthToken();
                headers.put("Authorization",token);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void loadArticlesInFolder(int folderId){
        articlesList = dbh.getArticlesInFolder(folderId);
        showArticles();
    }

    private void loadFolder(){
        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);

        TreeNode root = TreeNode.root();

        foldersTreeIds = new HashMap<>();

        for (Folder folder : foldersList) {
            if(folder.getParent_id()==0){
                TreeNode treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, folder.getTitle(),folder.getLocal_id()));
                foldersTreeIds.put(folder.getLocal_id(),treeNode);
                root.addChild(treeNode);
            }else{
                TreeNode child = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder,folder.getTitle(),folder.getLocal_id()));
                TreeNode parent = foldersTreeIds.get(folder.getParent_id());
                Log.d(TAG,"MainActivity: null parent: "+parent+", child: "+child);
                parent.addChild(child);
                foldersTreeIds.put(folder.getLocal_id(),child);
            }
        }

        treeView = new AndroidTreeView(this, root);
        treeView.setDefaultAnimation(true);
        treeView.setUse2dScroll(true);
        //TODO: Custom
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(this);
        treeView.setDefaultNodeLongClickListener(this);
        treeView.setDefaultViewHolder(ArrowExpandSelectableHeaderHolder.class);
        containerView.addView(treeView.getView());
        treeView.setUseAutoToggle(false);
    }

    private void setupNavigationView(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawer.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        Toast.makeText(this, "Click folder", Toast.LENGTH_SHORT).show();
        int localFolderId = ((IconTreeItemHolder.IconTreeItem)value).local_folder_id;
        //Log.d(TAG, "MainActivity: treeNode "+node+", value: "+localFolderId);
        if(currentFolderId!=localFolderId){
            currentFolderId=localFolderId;
            loadArticlesInFolder(currentFolderId);
            setToolbarTilte(((IconTreeItemHolder.IconTreeItem)value).text);
        }
    }

    @Override
    public boolean onLongClick(TreeNode node, Object value){
        Toast.makeText(this, "Long click folder", Toast.LENGTH_SHORT).show();

        changingFolderId = ((IconTreeItemHolder.IconTreeItem)value).local_folder_id;

        String folderTitle = ((IconTreeItemHolder.IconTreeItem)value).text;
        showRenameFolderDialog(folderTitle);

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void showDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hideDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        //TODO: Не могу получить getActionView(все время null) для создания анимации
        MenuItem searchItem = menu.findItem(R.id.action_refresh);
        ImageView searchView =
                (ImageView) MenuItemCompat.getActionView(searchItem);

        Log.d(TAG,"MainActivity: onCreateOptionsMenu "+searchView);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        Log.d(TAG,"MainActivity: onOptionItemSelected "+item.getActionView());

        if(id == R.id.action_refresh){

            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            syncFolders();
            syncArticles();
            //fabPlus.startAnimation(FabRanticlockwise);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncFolders(){

    }

    private void syncArticles(){

    }

    private void setToolbarTilte(String title){
        getSupportActionBar().setTitle(title);
    }
}
