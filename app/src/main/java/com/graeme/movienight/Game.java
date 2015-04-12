package com.graeme.movienight;

public class Game
{
  public static class Logic
  {
    private static final byte c_maxRoundCount = 5;
    private static byte m_roundCount = 1;

    public static byte GetRoundCount()
    {
      return m_roundCount;
    }

    public static void NextRound()
    {
      m_roundCount++;
    }

    public static void NewGame()
    {
      m_roundCount = 1;
    }

    public static boolean AllowAnotherRound()
    {
      return ( m_roundCount < c_maxRoundCount );
    }
  }
}
