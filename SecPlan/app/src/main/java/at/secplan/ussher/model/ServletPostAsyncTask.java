package at.secplan.ussher.model;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.secplan.ussher.environment.Parameters;

public class ServletPostAsyncTask extends AsyncTask<Pair<String, Integer>, Void, JSONObject> {
    private String command;
    private OnSuccessListener listener;
    private String report;

    public ServletPostAsyncTask(OnSuccessListener listener){
        this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(Pair<String, Integer>... params) {
        command = params[0].first;
        int id = params[0].second;
        JSONObject json = new JSONObject();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Parameters.server);

        try {
            //Add id data to request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "" + id));
            nameValuePairs.add(new BasicNameValuePair("command", command));
            if(command.equals("sendReport")){
                nameValuePairs.add(new BasicNameValuePair("json", report));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF_8"));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {

                String temp =  EntityUtils.toString(response.getEntity());
                try {
                    json = new JSONObject(temp);
                    return json;
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            try {
                json = new JSONObject("Error: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                return json;
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (IOException e) {
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            if (listener != null) {
                listener.onSuccess(result);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setReport(String report){
        this.report = report;
    }

}