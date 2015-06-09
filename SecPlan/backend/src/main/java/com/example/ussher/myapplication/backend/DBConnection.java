package com.example.ussher.myapplication.backend;

import org.json.JSONObject;

/**
 * Created by ussher on 09.05.15.
 */
public class DBConnection {
    private DBConnection instance = null;

    private DBConnection(){
    }

    public DBConnection getInstance(){
        if(instance == null){
            return this.instance = new DBConnection();
        }
        return instance;
    }

    public String connect(){
        String connect = "";
        //MongoClient mongoClient new

        return connect;
    }

    public JSONObject getData(){
        JSONObject object = new JSONObject();
        return object;
    }
}
