package com.fitec.crowdfunding.kickstarterscore;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProjectAdapter extends ArrayAdapter<Project> {

    public ProjectAdapter(Activity context, List<Project> projects) {
        super(context, 0, projects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Project currentProject = getItem(position);

        // Get the appropriate background color based on the current project score
        int scoreColor = getScoreColor(currentProject.getScore());

        TextView scoreView = (TextView) listItemView.findViewById(R.id.score);
        GradientDrawable scoreCircle = (GradientDrawable) scoreView.getBackground();

        // Set the color on the score circle
        scoreCircle.setColor(scoreColor);

        String formattedScore = formatScore(currentProject.getScore());
        TextView scoreTextView = (TextView) listItemView.findViewById(R.id.score);
        scoreTextView.setText(formattedScore);

        String name = currentProject.getName();
        TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.name);
        primaryLocationTextView.setText(name);

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    /**
     * Return the formatted score string showing 1 decimal place (i.e. "3.2")
     * from a decimal score value.
     */
    private String formatScore(double score) {
        DecimalFormat scoreFormat = new DecimalFormat("0.0");
        return scoreFormat.format(score);
    }

    private int getScoreColor(double score) {
        int scoreColorResourceId;
        int scoreFloor = (int) Math.floor(score);
        switch (scoreFloor) {
            case 0:
            case 1:
                scoreColorResourceId = R.color.score1;
                break;
            case 2:
                scoreColorResourceId = R.color.score2;
                break;
            case 3:
                scoreColorResourceId = R.color.score3;
                break;
            case 4:
                scoreColorResourceId = R.color.score4;
                break;
            case 5:
                scoreColorResourceId = R.color.score5;
                break;
            case 6:
                scoreColorResourceId = R.color.score6;
                break;
            case 7:
                scoreColorResourceId = R.color.score7;
                break;
            case 8:
                scoreColorResourceId = R.color.score8;
                break;
            case 9:
                scoreColorResourceId = R.color.score9;
                break;
            default:
                scoreColorResourceId = R.color.score10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), scoreColorResourceId);
    }
}
