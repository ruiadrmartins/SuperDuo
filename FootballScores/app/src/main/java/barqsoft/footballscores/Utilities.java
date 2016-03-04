package barqsoft.footballscores;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application generic Utilities
 */
public class Utilities {

    /**
     * Extra credits problem #1.1
     * Fixed league IDs, added some extra leagues
     */
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMEIRA_LIGA = 402;
    public static final int Bundesliga3 = 403;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS_LEAGUE  = 405;

    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return context.getString(R.string.seriaa);
            case PREMIER_LEAGUE : return context.getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.primeradivison);
            case BUNDESLIGA1 : return context.getString(R.string.bundesliga);
            case PRIMEIRA_LIGA : return context.getString(R.string.primeiraliga);
            default: return context.getString(R.string.league_not_known);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.group_stage_text)
                        + ", " + context.getString(R.string.matchday_text)
                        + ": 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.semi_final);
            }
            else
            {
                return context.getString(R.string.final_text);
            }
        }
        else
        {
            return context.getString(R.string.matchday_text) + " : " + String.valueOf(match_day);
        }
    }

    public static String getScores(Context context, int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return context.getString(R.string.empty_score);
        }
        else
        {
            return String.valueOf(home_goals)
                    + context.getString(R.string.empty_score)
                    + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (Context context, String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        {
            // Added icons for most Premier League teams
            case "Arsenal FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City FC" : return R.drawable.swansea_city_afc;
            case "Leicester City FC" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion FC" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            case "Aston Villa FC" : return R.drawable.aston_villa;
            case "Chelsea FC" : return R.drawable.chelsea;
            case "Liverpool FC" : return R.drawable.liverpool;
            case "Manchester City FC" : return R.drawable.manchester_city;
            case "Southampton FC" : return R.drawable.southampton_fc;
            case "Newcastle United FC" : return R.drawable.newcastle_united;
            case "Crystal Palace FC" : return R.drawable.crystal_palace_fc;

            default: return R.drawable.no_icon;
        }
    }

    // Gets today's date
    public static String getTodayDate() {
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(fragmentdate);
    }
}
