/*
  This class stores info relevant to the game-state and performs logic such as preparing
  for a new game and keeping track of the player's points.
*/

package com.graeme.movienight;

public class Game
{
  public static class Logic
  {
    //-------------------------------------------------------------------------

    public static final byte c_timePerRoundInSec = 10;
    public static String m_playerName;

    private static final byte c_maxRoundCount = 5;
    private static byte m_roundCount = 1;
    private static int m_points = 0;

    //-------------------------------------------------------------------------

    public static byte getRoundCount()
    {
      return m_roundCount;
    }

    //-------------------------------------------------------------------------

    public static void nextRound()
    {
      m_roundCount++;
    }

    //-------------------------------------------------------------------------

    public static void newGame()
    {
      m_roundCount = 1;
      m_points = 0;
    }

    //-------------------------------------------------------------------------

    public static void endRound()
    {
      LeaderBoard.Entry entry = new LeaderBoard.Entry();
      entry.m_name = m_playerName;
      entry.m_points = m_points;
      LeaderBoard.Data.m_entries.add( entry );
    }

    //-------------------------------------------------------------------------

    public static void updatePoints( byte remainingTimeInSec )
    {
      // Calc the % of the total time the player used to answer the question.
      byte percentOfTotalTimeToAnswer =
        (byte)( ( (float)remainingTimeInSec / c_timePerRoundInSec ) * 100.0f );

      if( remainingTimeInSec == 0 )
      {
        // Do nothing.
      }
      else if( percentOfTotalTimeToAnswer > 80 &&
               percentOfTotalTimeToAnswer <= 99 )
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 0.25f );
      }
      else if( percentOfTotalTimeToAnswer > 50 &&
               percentOfTotalTimeToAnswer <= 80 )
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 0.5f );
      }
      else if( percentOfTotalTimeToAnswer > 40 &&
               percentOfTotalTimeToAnswer <= 50 )
      {
        m_points += (int)( remainingTimeInSec * 1000 );
      }
      else if( percentOfTotalTimeToAnswer > 30 &&
               percentOfTotalTimeToAnswer <= 40 )
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 1.2f );
      }
      else if( percentOfTotalTimeToAnswer > 20 &&
               percentOfTotalTimeToAnswer <= 30 )
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 1.3f );
      }
      else if( percentOfTotalTimeToAnswer > 10 &&
               percentOfTotalTimeToAnswer <= 20 )
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 1.4f );
      }
      else
      {
        m_points += (int)( ( remainingTimeInSec * 1000 ) * 1.5f );
      }
    }

    //-------------------------------------------------------------------------

    public static boolean allowAnotherRound()
    {
      return ( m_roundCount < c_maxRoundCount );
    }

    //-------------------------------------------------------------------------

    public static int getPoints()
    {
      return m_points;
    }

    //-------------------------------------------------------------------------
  }
}
