package com.t0p47.mendeley.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.t0p47.library.model.TreeNode;
import com.t0p47.library.view.AndroidTreeView;
import com.t0p47.mendeley.R;
import com.t0p47.mendeley.app.AppConfig;
import com.t0p47.mendeley.app.AppController;
import com.t0p47.mendeley.db.DatabaseHandler;
import com.t0p47.mendeley.helper.Helper;
import com.t0p47.mendeley.helper.SessionManager;
import com.t0p47.mendeley.holder.ArrowExpandSelectableHeaderHolder;
import com.t0p47.mendeley.holder.IconTreeItemHolder;
import com.t0p47.mendeley.model.Folder;
import com.t0p47.mendeley.model.JournalArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TreeNode.TreeNodeClickListener {


    //Coments changed(Now from laptop)
    private static final String TAG = "LOG_TAG";

    private static final String NAME = "Very long name for forlder";
    private AndroidTreeView treeView;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ProgressDialog pDialog;

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



        getFolders();


        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //Navigation view header
        navHeader = navigationView.getHeaderView(0);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });

        setupNavigationView();

        if(savedInstanceState == null){
            navItemIndex=0;
            loadFolder();
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
