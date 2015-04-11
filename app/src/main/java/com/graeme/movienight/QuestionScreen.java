package com.graeme.movienight;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class QuestionScreen extends ActionBarActivity
{
  //---------------------------------------------------------------------------

  private Trivia m_trivia = new Trivia();

  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_question_screen );

    m_trivia.execute();

    while( m_trivia.hasTriviaLoaded() == false &&
           m_trivia.hasTriviaLoadFailed() == false );

    if( m_trivia.hasTriviaLoadFailed() )
    {
      return;
    }

    Trivia.Question q = m_trivia.getQuestion( "Action" );
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.menu_question_screen, menu );
    return true;
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onOptionsItemSelected( MenuItem item )
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if( id == R.id.action_settings )
    {
      return true;
    }

    return super.onOptionsItemSelected( item );
  }

  //---------------------------------------------------------------------------
}
