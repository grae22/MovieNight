package com.graeme.movienight;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Trivia extends AsyncTask<URL, Integer, Long>
{
  //---------------------------------------------------------------------------

  public static class Question
  {
    final public int c_answerCount = 3;

    public String m_text;
    public String[] m_answers = new String[ c_answerCount ];
    public int m_correctAnswer;
    public String m_imageUrl;
    public Drawable m_image;
  };

  //---------------------------------------------------------------------------

  @Override
  protected Long doInBackground( URL... params )
  {
    Data.retrieveTrivia();

    return null;
  }

  //---------------------------------------------------------------------------

  public static class Data
  {
    //-------------------------------------------------------------------------

    private static boolean m_isTriviaRetrieved = false;
    private static boolean m_hasTriviaRetrievalFailed = false;
    public static HashMap<String, ArrayList<Question>> m_categories = new HashMap<String, ArrayList<Question>>();

    //-------------------------------------------------------------------------

    private static void retrieveTrivia()
    {
      if( m_categories.size() > 0 )
      {
        return;
      }

      try
      {
        // Create a new HTTP Client
        HttpClient client = new DefaultHttpClient();

        // Setup the get request
        HttpGet httpGetRequest = new HttpGet( "http://mobileclients.installprogram.eu/quiz/trivia.json" );

        // Execute the request in the client
        HttpResponse httpResponse = client.execute( httpGetRequest );

        // Grab the response
        BufferedReader reader = new BufferedReader( new InputStreamReader( httpResponse.getEntity().getContent() ) );//, "UTF-8" ) );

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
            // Create the question.
            JSONObject jsonQuestion = jsonQuestions.getJSONObject( j );
            Question q = new Question();

            try
            {
              // Set question's properties.
              q.m_text = jsonQuestion.getString( "question" );
              q.m_correctAnswer = jsonQuestion.getInt( "answerIdx" );
              q.m_imageUrl = jsonQuestion.getString( "imageURL" );
              q.m_image = getImageFromUrl( q.m_imageUrl );

              // Get the answers.
              JSONArray jsonAnswers = jsonQuestion.getJSONArray( "answers" );

              for( int k = 0; k < jsonAnswers.length(); k++ )
              {
                q.m_answers[ k ] = jsonAnswers.getString( k );
              }

              // Add to the current category's questions list.
              questions.add( q );
            }
            catch( Exception ex )
            {
              // Ignore - just skip this question.
            }
          }

          Data.m_categories.put( categoryName, questions );
        }

        m_isTriviaRetrieved = true;
      }
      catch( Exception ex )
      {
        m_hasTriviaRetrievalFailed = true;
      }
    }

    //---------------------------------------------------------------------------

    public static Question getQuestion( String category )
    {
      try
      {
        ArrayList<Question> questions = Data.m_categories.get( category );

        if( questions.size() > 0 )
        {
          // Choose a random question.
          int randomQuestionIndex = (int) ( questions.size() * Math.random() );

          return questions.get( randomQuestionIndex );
        }
      }
      catch( Exception ex )
      {

      }

      return null;
    }

    //---------------------------------------------------------------------------

    public static boolean hasTriviaLoaded()
    {
      return m_isTriviaRetrieved;
    }

    //---------------------------------------------------------------------------

    public static boolean hasTriviaLoadFailed()
    {
      return m_hasTriviaRetrievalFailed;
    }

    //---------------------------------------------------------------------------

    private static Drawable getImageFromUrl( String address )
    {
      try
      {
        URL url = new URL( address );
        InputStream in = (InputStream) url.getContent();

        return Drawable.createFromStream( in, "image" );
      }catch( Exception ex )
      {
        return null;
      }
    }

    //---------------------------------------------------------------------------
  }
}
