package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Adapter to fill up score items inside app
 */
public class scoresAdapter extends CursorAdapter
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    public double detail_match_id = 0;

    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public scoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){

        final ViewHolder mHolder = (ViewHolder) view.getTag();

        String homeTeam = cursor.getString(COL_HOME);
        String awayTeam = cursor.getString(COL_AWAY);
        String matchTime = cursor.getString(COL_MATCHTIME);
        int homeGoals = cursor.getInt(COL_HOME_GOALS);
        int awayGoals = cursor.getInt(COL_AWAY_GOALS);
        double matchId = cursor.getDouble(COL_ID);
        String matchDate = cursor.getString(COL_DATE);
        String score = Utilities.getScores(homeGoals, awayGoals);

        mHolder.home_name.setText(homeTeam);
        mHolder.away_name.setText(awayTeam);
        mHolder.home_name.setContentDescription(homeTeam);
        mHolder.away_name.setContentDescription(awayTeam);

        mHolder.date.setText(matchTime);
        mHolder.date.setContentDescription(matchTime);
        mHolder.score.setText(score);
        mHolder.score.setContentDescription(score);

        mHolder.match_id = matchId;

        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                mContext, homeTeam));
        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                mContext, awayTeam));
        mHolder.home_crest.setContentDescription(homeTeam);
        mHolder.away_crest.setContentDescription(awayTeam);

        // Content description text for match
        if(score.equals(" - ")) {
            mHolder.scores_item.setContentDescription(
                homeTeam + mContext.getString(R.string.vs) + awayTeam
                + mContext.getString(R.string.at) + matchTime
                + mContext.getString(R.string.on) + matchDate
            );
        } else {
            mHolder.scores_item.setContentDescription(
                homeTeam + mContext.getString(R.string.vs) + awayTeam
                + mContext.getString(R.string.score_is)
                + Utilities.getScores(homeGoals, awayGoals)
            );
        }

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if(mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            String matchDay = Utilities.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE));
            match_day.setText(matchDay);
            match_day.setContentDescription(matchDay);

            TextView league = (TextView) v.findViewById(R.id.league_textview);
            String leagueText = Utilities.getLeague(cursor.getInt(COL_LEAGUE));
            league.setText(leagueText);
            league.setContentDescription(leagueText);

            Button share_button = (Button) v.findViewById(R.id.share_button);
            final String shareText = mHolder.home_name.getText() + " " + mHolder.score.getText()
                    + " " + mHolder.away_name.getText() + " ";
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareIntent(shareText));
                }
            });
            share_button.setContentDescription(context.getString(R.string.share_text) + " " + shareText);
        } else {
            container.removeAllViews();
        }
    }

    public Intent createShareIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
