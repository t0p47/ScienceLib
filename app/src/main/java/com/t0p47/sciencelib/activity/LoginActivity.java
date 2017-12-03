package com.t0p47.sciencelib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.t0p47.sciencelib.R;
import com.t0p47.sciencelib.app.AppConfig;
import com.t0p47.sciencelib.app.AppController;
import com.t0p47.sciencelib.db.DatabaseHandler;
import com.t0p47.sciencelib.helper.SessionManager;
import com.t0p47.sciencelib.interfaces.ResponseDataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOG_TAG";
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private DatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        inputEmail = (EditText) findViewById(R.id.etEmail);
        inputPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        //Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        dbh = new DatabaseHandler(this);

        session = new SessionManager(getApplicationContext());

        //Check if user already logged in or not
        if(session.isLoggedIn()){

            //Log.d(TAG,"LoginActivity: starting token refresh");
            refreshToken(new ResponseDataCallback() {
                @Override
                public void onSuccessString(String response) {
                    //Log.d(TAG,"LoginActivity: token refresh maybe sync "+response);
                    if(!response.isEmpty()){
                        //Log.d(TAG,"LoginActivity: response is NOT empty");
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d(TAG,"LoginActivity: response is empty");
                    }
                }
            });


        }else{
            Toast.makeText(this, "Need to login",Toast.LENGTH_SHORT).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                //Check for empty data in form
                if(!email.isEmpty() && !password.isEmpty()){
                    //login user
                    checkLogin(email,password);
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void checkLogin(final String email, final String password){
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "LoginActivity: response: " + response.toString());
                hideDialog();

                //TODO: Распарсить ответ
                try{
                    JSONObject jObj = new JSONObject(response);

                    session.setLogin(true);

                    String token = jObj.getString("token");

                    session.setAuthToken(token);

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Json error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null && error.getMessage()!=null){
                    Log.e(TAG,"LoginActivity: checkLogin onErrorResponse "+error.getMessage()+", status code "+error.networkResponse.statusCode
                            +", networkResponseData "+error.toString());
                }else{
                    Log.e(TAG,"LoginActivity: checkLogin onErrorResponse "+error.getMessage());
                }


                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){

            @Override
            protected Map<String,String> getParams(){
                //Posting parameters to login url
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void refreshToken(final ResponseDataCallback callback){

        String tag_string_req = "req_token_refresh";

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_REFRESH_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "LoginActivity: refreshToken response " + response);

                try {
                    JSONObject jObj = new JSONObject(response);

                    String token = jObj.getString("token");

                    //Log.d(TAG,"LoginActivity: refreshed token: "+token);

                    session.setAuthToken(token);
                    callback.onSuccessString(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"LoginActivity: refreshToken onErrorResponse "+error.getMessage()+", status code "+error.networkResponse.statusCode
                    +", networkResponseData "+error.toString());
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                HashMap<String,String> headers = new HashMap<String,String>();
                String token = "Bearer "+session.getAuthToken();
                //Log.d(TAG,"LoginActivity: token to refresh(expired): "+token);
                headers.put("Authorization",token);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
