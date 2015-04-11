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
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

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
  private boolean m_hasTriviaRetrievalFailed = false;
  private HashMap<String, ArrayList<Question>> m_categories = new HashMap<String, ArrayList<Question>>();

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

  private void retrieveTrivia()
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
          new InputStreamReader( httpResponse.getEntity().getContent() ) );//, "UTF-8" ) );

      String json = new String();

      for( String tmp = reader.readLine(); tmp != null; tmp = reader.readLine() )
      {
        json += tmp;
      }

      reader.close();

      // Instantiate a JSON object from the request response
      JSONObject jsonOb = new JSONObject( json );

      // Get the categories & questions.
      JSONArray jsonCategories = jsonOb.getJSONArray( "categories" );

      for( int i = 0; i < jsonCategories.length(); i++ )
      {
        JSONObject jsonCategory = jsonCategories.getJSONObject( i );

        String categoryName = jsonCategory.getString( "name" );

        // Get category's questions.
        ArrayList<Question> questions = new ArrayList<Question>();

        JSONArray jsonQuestions = jsonCategory.getJSONArray( "questions" );

        for( int j = 0; j < jsonQuestions.length(); j++ )
        {
          JSONObject jsonQuestion = jsonQuestions.getJSONObject( j );

          Question q = new Question();

          try
          {
            q.m_text = jsonQuestion.getString( "question" );
            q.m_correctAnswer = jsonQuestion.getInt( "answerIdx" );

            questions.add( q );
          }
          catch( Exception ex )
          {
            // Ignore - just skip this question.
          }
        }

        m_categories.put( categoryName, questions );
      }

      m_isTriviaRetrieved = true;
    }
    catch( Exception ex )
    {
      m_hasTriviaRetrievalFailed = true;
    }
  }

  //---------------------------------------------------------------------------

  public Question getQuestion( String category )
  {
    try
    {
      ArrayList<Question> questions = m_categories.get( category );

      if( questions.size() > 0 )
      {
        return questions.get( 0 );
      }
    }
    catch( Exception ex )
    {

    }

    return null;
  }

  //---------------------------------------------------------------------------

  public boolean hasTriviaLoaded()
  {
    return m_isTriviaRetrieved;
  }

  public boolean hasTriviaLoadFailed()
  {
    return m_hasTriviaRetrievalFailed;
  }

  //---------------------------------------------------------------------------
}
