/*
  This class retrieves the leader-board entries and stores them.
*/

package com.graeme.movienight;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

public class LeaderBoard extends AsyncTask<URL, Integer, Long>
{
  //---------------------------------------------------------------------------

  public static class Entry implements Comparable<Entry>
  {
    public String m_name;
    public int m_points;

    @Override
    public int compareTo( Entry another )
    {
      if( another.m_points < m_points )
      {
        return -1;
      }
      else if( another.m_points > m_points )
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
  };

  //---------------------------------------------------------------------------

  @Override
  protected Long doInBackground( URL... params )
  {
    Data.retrieveEntries();

    return null;
  }

  //---------------------------------------------------------------------------

  public static class Data
  {
    //-------------------------------------------------------------------------

    private static boolean m_isRetrieved = false;
    private static boolean m_hasRetrievalFailed = false;
    public static Vector<Entry> m_entries = new Vector<Entry>();

    //-------------------------------------------------------------------------

    private static void retrieveEntries()
    {
      if( m_entries.size() > 0 )
      {
        return;
      }

      try
      {
        // Create a new HTTP Client
        HttpClient client = new DefaultHttpClient();

        // Setup the get request
        HttpGet httpGetRequest =
          new HttpGet( "http://mobileclients.installprogram.eu/quiz/leaderboard.json" );

        // Execute the request in the client
        HttpResponse httpResponse = client.execute( httpGetRequest );

        // Grab the response
        BufferedReader reader =
          new BufferedReader(
            new InputStreamReader( httpResponse.getEntity().getContent() ) );

        String json = new String();

        for( String tmp = reader.readLine(); tmp != null; tmp = reader.readLine() )
        {
          json += tmp;
        }

        reader.close();

        // Instantiate a JSON object from the request response
        JSONObject jsonOb = new JSONObject( json );

        JSONArray jsonLeaderboard = jsonOb.getJSONArray( "leaderboard" );

        for( int i = 0; i < jsonLeaderboard.length(); i++ )
        {
          JSONObject ob = jsonLeaderboard.getJSONObject( i );

          Entry entry = new Entry();
          entry.m_name = ob.getString( "player" );
          entry.m_points = ob.getInt( "score" );

          m_entries.add( entry );
        }

        m_isRetrieved = true;
      }
      catch( Exception ex )
      {
        m_hasRetrievalFailed = true;
      }
    }

    //---------------------------------------------------------------------------

    public static boolean hasLoaded()
    {
      return m_isRetrieved;
    }

    //---------------------------------------------------------------------------

    public static boolean hasLoadFailed()
    {
      return m_hasRetrievalFailed;
    }

    //---------------------------------------------------------------------------
  }
}
