package at.secplan.ussher.navi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.secplan.ussher.model.OnSuccessListener;
import at.secplan.ussher.model.ServletPostAsyncTask;
import at.secplan.ussher.secplan.MainActivity;
import at.secplan.ussher.secplan.R;


/**
 * Created by ussher on 04.05.15.
 */
public class ReportFragment extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener{
    private ArrayList<MyList> list;
    private ListView listView;
    private MyListAdapter adapter;
    private JSONObject data;
    private Button submit;
    private EditText custom_report;
    private boolean[] checked;
    public ReportFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String report = getArguments().getString("reports");

        try {
            data = new JSONObject(report);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        View V = inflater.inflate(R.layout.main_list_view, container, false);

        listView = (ListView) V.findViewById(R.id.listView);

        submit = (Button) V.findViewById(R.id.listViewButtom);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });

        custom_report = (EditText) V.findViewById(R.id.listViewEdittext);

        attachWatcher();

        displayItems();
        checked = new boolean[list.size()];
        return V;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int position = (Integer) buttonView.getTag();

        if(position != listView.INVALID_POSITION){
            MyList l = list.get(position);
            l.setSelected(isChecked);
            checked[position] = l.isSelected();
            checkActivateSubmit();
        }
    }

    public void displayItems(){
        if(data != null){
            try {
                JSONArray temp = data.getJSONArray("reports");
                list = new ArrayList<MyList>();
                for (int i = 0; i < temp.length(); i++){
                    list.add(new MyList(temp.getString(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        adapter = new MyListAdapter(getActivity().getApplicationContext(),list,this);
        listView.setAdapter(adapter);
    }

    private void attachWatcher(){
        custom_report.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                activateSubmit();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void activateSubmit() {
        boolean is_ready = custom_report.getText().toString().length() > 0;
        if(is_ready){
            submit.setTextColor(Color.parseColor("#eeeeee"));
            submit.setEnabled(true);
        }
        else {
            submit.setTextColor(Color.parseColor("#999999"));
            submit.setEnabled(false);
        }
        submit.setEnabled(is_ready);
    }

    private void checkActivateSubmit(){
        if (checked()){
            submit.setEnabled(true);
            submit.setTextColor(Color.parseColor("#eeeeee"));
        }
        else {
            submit.setEnabled(false);
            submit.setTextColor(Color.parseColor("#999999"));
        }
    }

    private boolean checked(){
        for (boolean value : checked) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    private void submitReport(){
        JSONObject object = new JSONObject();
        if(!custom_report.getText().toString().equals("")){
            try {
                object.put("custom_report",custom_report.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(checked()){
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < checked.length; i++){
                if(checked[i]){
                    temp.add(list.get(i).getReport());
                }
            }
            try {
                object.put("daily_report",arrayListParser(temp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("time", DateFormat.getDateTimeInstance().format(new Date()));
            object.put("personalID", MainActivity.personalID);
            ServletPostAsyncTask task = new ServletPostAsyncTask(new OnSuccessListener() {
                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        Toast.makeText(getActivity().getApplicationContext(),json.getString("status"),Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            task.setReport(object.toString());
            task.execute(new Pair<String, Integer>("sendReport", MainActivity.personalID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Refresh fragment
        if (custom_report.length() > 0) {
            custom_report.getText().clear();
        }
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(custom_report.getWindowToken(),0);
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
}
