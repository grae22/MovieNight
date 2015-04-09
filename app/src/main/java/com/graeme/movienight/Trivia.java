package com.graeme.movienight;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;

public class Trivia extends AsyncTask<URL, Integer, Long>
{
  //---------------------------------------------------------------------------

  public class Question
  {
    final public int c_answerCount = 3;

    public String m_text;
    public String[] m_answers = new String[ c_answerCount ];
    public int m_correctAnswer;
  };

  private boolean m_isTriviaRetrieved = false;
  private JSONObject m_jsonOb;

  //---------------------------------------------------------------------------

  @Override
  protected Long doInBackground( URL... params )
  {
    if( m_isTriviaRetrieved == false )
    {
      retrieveTrivia();
    }

    return null;
  }

  //---------------------------------------------------------------------------

  private Uri retrieveTrivia()
  {
    try
    {
      // Create a new HTTP Client
      HttpClient client = new DefaultHttpClient();

      // Setup the get request
      HttpGet httpGetRequest =
        new HttpGet( "http://mobileclients.installprogram.eu/quiz/trivia.json" );

      // Execute the request in the client
      HttpResponse httpResponse = client.execute( httpGetRequest );

      // Grab the response
      BufferedReader reader =
        new BufferedReader(
          new InputStreamReader( httpResponse.getEntity().getContent(), "UTF-8" ) );

      String json = reader.readLine();

      // Instantiate a JSON object from the request response
      m_jsonOb = new JSONObject( json );

      m_isTriviaRetrieved = true;
    }
    catch( Exception ex )
    {

    }

    return null;
  }

  //---------------------------------------------------------------------------

  public Question getQuestion( String category )
  {
    try
    {
      JSONArray a = m_jsonOb.getJSONArray( "categories" );

      int i = 1;
    }
    catch( Exception ex )
    {
      String m = ex.getMessage();
    }

    return null;
  }

  //---------------------------------------------------------------------------
}
