package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lesley on 4/20/2018.
 * This class is used in WeeklyMenuActivity, where it displays the corresponding dining halls for
 * each meal period and the menu for that particular day
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<CafeteriaModel> cafeData = new ArrayList<>();
    private HashMap<CafeteriaModel, ArrayList<String>> mealMap = new HashMap<>();
    View line;

    public ExpandableListAdapter(Context context, ArrayList<CafeteriaModel> cafeData, HashMap<CafeteriaModel, ArrayList<String>> mealMap) {
        this.context = context;
        this.mealMap = mealMap;

        for (CafeteriaModel m : mealMap.keySet()) {
            this.cafeData.add(m);
        }
    }

    @Override
    public int getGroupCount() {
        return mealMap.size();
    }

    @Override
    public int getChildrenCount(int i) {
        CafeteriaModel m = cafeData.get(i);
        return mealMap.get(m).size();
    }

    @Override
    public Object getGroup(int i) {
        return cafeData.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        CafeteriaModel m = (CafeteriaModel) getGroup(i);
        return mealMap.get(m).get(i1);
    }

    @Override
    public long getGroupId(int i)  {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        // Inflate layout if it does not exist already
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_header, viewGroup, false);
        }

        CafeteriaModel m = (CafeteriaModel) getGroup(i);
        TextView headerText = view.findViewById(R.id.header);
        headerText.setText(m.getNickName());

        // TODO(lesley): Add and fix isOpen() -- mb use color or sort for open eateries to bubble
        // to the top
//        TextView timetext = view.findViewById(R.id.time);
//        timetext.setText(m.isOpen());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        // Inflate layout if it does not exist already
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_body, viewGroup, false);
        }

        // Horizontal line that separates each eatery entry
        line = view.findViewById(R.id.horiline);
        line.setVisibility(View.INVISIBLE);
        String str = (String)getChild(i,i1);

        TextView tv = view.findViewById(R.id.menu_title);
        // If str == 1, then string is a category
        if (str.charAt(0) == '1') {
            str = str.substring(1);
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setAllCaps(true);
            tv.setTextSize(18);
        }
        // If str != 1, then string is a meal item
        else {
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setAllCaps(false);
            tv.setTextColor(Color.parseColor("#808080"));
            tv.setTextSize(14);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        if (getChildrenCount(i) == 1) {
            return true;
        }
        return false;
    }
}
