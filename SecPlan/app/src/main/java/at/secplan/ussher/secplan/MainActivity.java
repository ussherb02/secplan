package at.secplan.ussher.secplan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import at.secplan.ussher.environment.Parameters;
import at.secplan.ussher.model.OnSuccessListener;
import at.secplan.ussher.model.ServletPostAsyncTask;

public class MainActivity extends FragmentActivity {

    private EditText personal_number;
    private Button login;

    public static int personalID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personal_number = (EditText) findViewById(R.id.personal_number);
        personal_number.setGravity(Gravity.CENTER);
        login = (Button) findViewById(R.id.login);

        connectionHandler();

        attachWatcher();
        login();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attachWatcher(){
        personal_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                activateLogin();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void activateLogin() {
        boolean is_ready = personal_number.getText().toString().length() > 3;
        if(is_ready){
            login.setTextColor(Color.parseColor("#777777"));
        }
        else{
            login.setTextColor(Color.parseColor("#999999"));
        }
        login.setEnabled(is_ready);
    }


    /*
    * Login with credentials to get data from database
    * and transfer the to the next activity for display
    * */
    private void login(){
        login.setTextColor(Color.parseColor("#999999"));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.valueOf(personal_number.getText().toString());
                try {
                    getData(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUp(JSONObject object, int id){
        try {
            if(Integer.valueOf(object.getString("personalID")) == id){
                personalID = id;
                Intent intent = new Intent(MainActivity.this, AccessDrawer.class);
                intent.putExtra("object", object.toString());
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Access Denied", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //JSONObject obj = new JSONObject();
    private void getData(int id) throws JSONException {
        final int ID = id;
        ServletPostAsyncTask task = new ServletPostAsyncTask(new OnSuccessListener() {
            @Override
            public void onSuccess(JSONObject json) {
                setUp(json , ID);
            }
        });
        task.execute(new Pair<String, Integer>("login", id));
    }

    private JSONArray arrayListParser(ArrayList<String> list){
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i  = 0; i < list.size(); i++){
            String temp = "a"+i;
            try {
                object.put(temp,list.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int j = 0; j < object.length(); j++){
            String temp = "a"+j;
            try {
                array.put(object.get(temp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public void connectionHandler() {
        if(isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Internet connection available",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"No internet connection available",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
