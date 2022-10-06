package com.oktested.helpSupport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oktested.R;
import com.oktested.helpSupport.model.HelpModel;

import java.util.ArrayList;

public class HelpSupportAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HelpModel> helpData;

    public HelpSupportAdapter(Context context, ArrayList<HelpModel> helpData) {
        this.context = context;
        this.helpData = helpData;
    }

    @Override
    public int getGroupCount() {
        return helpData.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return helpData.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return helpData.get(listPosition).getAnswer();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.question_item, null);
        }
        ImageView arrowIV =  convertView.findViewById(R.id.arrowIV);
        TextView questionTV = convertView.findViewById(R.id.questionTV);
        questionTV.setText(helpData.get(listPosition).getQuestion());

        if (isExpanded) {
            arrowIV.setRotation(0);
        } else {
            arrowIV.setRotation(180);
        }
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.answer_item, null);
        }
        TextView answerTV = convertView.findViewById(R.id.answerTV);
        answerTV.setText(helpData.get(listPosition).getAnswer());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}