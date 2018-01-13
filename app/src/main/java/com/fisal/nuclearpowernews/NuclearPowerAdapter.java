package com.fisal.nuclearpowernews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fisal on 12/01/2018.
 */

class NuclearPowerAdapter extends ArrayAdapter<NuclearPower> {

    private static final String DATE_SEPARATOR = "T";

    public NuclearPowerAdapter(Context context, List<NuclearPower> nuclearPowers) {
        super(context, 0, nuclearPowers);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.nuclear_list_item, parent, false);
        }

            NuclearPower currentNuclearPowerNews = getItem(position);

            TextView title = (TextView) listItemView.findViewById(R.id.webTitle);
            String currentTitle = currentNuclearPowerNews.getWebTitle();
            title.setText(currentTitle);

            TextView section = (TextView) listItemView.findViewById(R.id.sectionName);
            String currentSection = currentNuclearPowerNews.getSectionName();
            section.setText(currentSection);

            GradientDrawable sectionCircle = (GradientDrawable) section.getBackground();
            int sectionColor = getSectionColor(currentNuclearPowerNews.getSectionName());
            sectionCircle.setColor(sectionColor);

            TextView date = (TextView) listItemView.findViewById(R.id.date);
            String currentDate = currentNuclearPowerNews.getWebPublicationDate();
            String[] dateWithoutTime = currentDate.split(DATE_SEPARATOR);
            String publicationDate = dateWithoutTime[0];
            date.setText(publicationDate);


        return listItemView;
    }

    private int getSectionColor(String section) {
        int sectionColorResourceId;
        switch (section) {
            case "UK news":
                sectionColorResourceId = R.color.section1;
                break;
            case "Environment":
                sectionColorResourceId = R.color.section2;
                break;
            case "Technology":
                sectionColorResourceId = R.color.section3;
                break;
            case "World news":
                sectionColorResourceId = R.color.section4;
                break;
            case "Books":
                sectionColorResourceId = R.color.section5;
                break;
            case "Business":
                sectionColorResourceId = R.color.section6;
                break;
            default:
                sectionColorResourceId = R.color.section7;
                break;
        }
        return ContextCompat.getColor(getContext(), sectionColorResourceId);
    }

}
