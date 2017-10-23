package com.t0p47.mendeley.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener {

    //Laptop changes
    private static final String TAG = "LOG_TAG";

    private static final String NAME = "Very long name for forlder";
    private AndroidTreeView treeView;

    Animation FabOpen, FabClose,FabRClockwise, FabRanticlockwise;
    boolean isFabOpen = false;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private FloatingActionButton fabPlus, fabTwit, fabFb;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private JournalArticleAdapter mAdapter;

    HashMap<Integer, TreeNode> foldersTreeIds;
    List<Folder> foldersList;
    List<JournalArticle> articlesList;

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


        getFolders();
        getArticles();


        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabTwit = (FloatingActionButton) findViewById(R.id.fab_twitter);
        fabFb = (FloatingActionButton) findViewById(R.id.fab_fb);
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




        //Navigation view header
        navHeader = navigationView.getHeaderView(0);

        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);


        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFabOpen){
                    fabFb.startAnimation(FabClose);
                    fabTwit.startAnimation(FabClose);
                    fabPlus.startAnimation(FabRanticlockwise);

                    fabFb.setClickable(false);
                    fabTwit.setClickable(false);
                    isFabOpen=false;
                }else{
                    fabFb.startAnimation(FabOpen);
                    fabTwit.startAnimation(FabOpen);
                    fabPlus.startAnimation(FabRClockwise);

                    fabFb.setClickable(true);
                    fabTwit.setClickable(true);
                    isFabOpen=true;
                }
                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });

        setupNavigationView();

        if(savedInstanceState == null){
            navItemIndex=0;
            loadFolder();
        }

    }

    private void showArticlesInRootFolder(){

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
                        showArticlesInRootFolder();
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
                Log.d(TAG, "MainActivity: response " + response.toString());
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);

                    String token = jObj.getString("refreshedToken");

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
        Log.d(TAG, "MainActivity: treeNode "+node+", value: "+((IconTreeItemHolder.IconTreeItem)value).local_folder_id);
    }

    @Override
    public boolean onLongClick(TreeNode node, Object value){
        Toast.makeText(this, "Long click folder", Toast.LENGTH_SHORT).show();
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
}
