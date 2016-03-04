package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Widget Service
 * adapted from Advanced Android Development Github branch 7.04
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                String today = Utilities.getTodayDate();

                data = getContentResolver().query(
                        DatabaseContract.scores_table.buildScoreWithDate(),
                        null,
                        //DatabaseContract.scores_table.DATE_COL + " = ?" ,
                        null,
                        new String[]{today},
                        //DatabaseContract.scores_table.TIME_COL + " DESC"
                        null
                );

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_score_item);

                String homeTeam = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
                String awayTeam = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
                String homeScore = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
                String awayScore = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

                views.setTextViewText(R.id.score_home_team,homeTeam);
                views.setTextViewText(R.id.score_away_team,awayTeam);

                if(Integer.valueOf(homeScore) < 0 || Integer.valueOf(awayScore) < 0) {
                    String gameTime = data.getString(data.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
                    views.setTextViewText(R.id.game_time, gameTime);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        String description =
                                homeTeam + " " + awayTeam + " " + getString(R.string.at) + " " + gameTime;
                        setRemoteContentDescription(views, description);
                    }
                } else {
                    views.setTextViewText(R.id.score_home, homeScore);
                    views.setTextViewText(R.id.score_away, awayScore);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        String description =
                                homeTeam + " " + homeScore + " " + awayTeam + " " + awayScore;
                        setRemoteContentDescription(views, description);
                    }
                }

                views.setOnClickFillInIntent(R.id.widget_scores, new Intent());

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_score_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.layout.widget_score_item, description);
    }
}
