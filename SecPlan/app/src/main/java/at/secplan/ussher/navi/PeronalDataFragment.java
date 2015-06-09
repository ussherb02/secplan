package at.secplan.ussher.navi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import at.secplan.ussher.secplan.MapActivity;
import at.secplan.ussher.secplan.R;

public class PeronalDataFragment extends Fragment {

    private TextView name;
    private TextView company_name;
    private TextView personal_id;
    private TextView date;
    private JSONObject data;
    private String[] list;
    private ExpandableListAdapter list_adapter;
    private ExpandableListView expandable_list_view;
    private ArrayList<String> list_headers;
    private HashMap<String, ArrayList<String>> list_items;
    private Context context;

    public PeronalDataFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String json = getArguments().getString("json");

        View V = inflater.inflate(R.layout.activity_peronal_data, container, false);

        this.context = getActivity().getApplicationContext();

        name = (TextView) V.findViewById(R.id.name);
        company_name = (TextView) V.findViewById(R.id.company_name);
        personal_id =  (TextView) V.findViewById(R.id.personal_id);
        date = (TextView) V.findViewById(R.id.date);
        // get the listview
        expandable_list_view = (ExpandableListView) V.findViewById(R.id.expandable_list_view);

        data = getData(json);

        presentData();
        // preparing list data
        prepareListData();

        list_adapter = new ExpandableView(context, list_headers, list_items);

        // setting list adapter
        expandable_list_view.setAdapter(list_adapter);
        callEvents();

        return V;
    }

    private JSONObject getData(String json){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            return jsonObject;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void presentData(){
        try {
            company_name.setText(data.getString("company_name"));
            name.setText("Name: " + data.getString("name"));
            personal_id.setText("ID: " + data.getString("personalID"));
            date.setText(DateFormat.getDateTimeInstance().format(new Date()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepareListData() {
        list_headers = new ArrayList<String>();
        list_items = new HashMap<String, ArrayList<String>>();

        //Getting json object
        JSONObject object = null;
        try {
            object = data.getJSONObject("work");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < object.length(); i++){
            JSONArray temp = null;
            try {
                String header = object.names().getString(i);
                temp = object.getJSONArray(header);
                list_headers.add(header);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> children = new ArrayList<String>();
            for (int j = 0; j < temp.length(); j++){
                try {
                    children.add(temp.getString(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                list_items.put(object.names().getString(i),children);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void callEvents() {
        expandable_list_view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("currentStation", list_items.get(list_headers.get(groupPosition)).get(childPosition));
                intent.putStringArrayListExtra("stations", list_items.get(list_headers.get(groupPosition)));

                Toast.makeText(context, list_headers.get(groupPosition) + " : " + list_items.get(list_headers.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT)
                        .show();
                startActivity(intent);
                return false;
            }
        });
    }
}
