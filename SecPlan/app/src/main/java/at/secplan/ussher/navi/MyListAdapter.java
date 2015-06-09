package at.secplan.ussher.navi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import at.secplan.ussher.secplan.R;

/**
 * Created by ussher on 11.05.15.
 */

public class MyListAdapter extends BaseAdapter {

    private ArrayList<MyList> list;
    private Context context;
    private ReportFragment reportFragment;
    private LayoutInflater inflater;
    //private boolean[] checked;

    public MyListAdapter(Context context, ArrayList<MyList> list, ReportFragment reportFragment) {
        this.context = context;
        this.list = list;
        //checked = new boolean[list.size()];
        //Arrays.fill(checked,Boolean.FALSE);
        this.reportFragment = reportFragment;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class Holder{
        private TextView textview;
        private CheckBox checkbox;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();

        if (convertView == null){
            convertView = inflater.inflate(R.layout.single_list_view,null);

            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.textview = (TextView) convertView.findViewById(R.id.text_list_view);

            holder.checkbox.setOnCheckedChangeListener(reportFragment);

            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        holder.checkbox.setTag(position);
        holder.textview.setText(list.get(position).getReport());
        holder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }
}

class MyList{
    private String report;
    private boolean selected;

   public MyList(String report){
        this.report = report;
    }

    public String getReport(){
        return report;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }
}


