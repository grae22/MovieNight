/*
  This screen shows while the game is loading (questions and leaderboard info).

  Yep... it's pretty horrible.
*/

package com.graeme.movienight;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Loading extends ActionBarActivity implements Runnable
{
  //---------------------------------------------------------------------------

  private View m_screenRoot = null;
  private TextView m_lblLoading;
  private byte m_loadingCount;
  private Trivia m_trivia = new Trivia();
  private LeaderBoard m_leaderBoard = new LeaderBoard();

  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_loading );

    m_screenRoot = findViewById( R.id.layLoadingScreenRoot );
    m_lblLoading = (TextView)findViewById( R.id.lblLoading );

    m_trivia.execute();
    m_leaderBoard.execute();
  }

  //---------------------------------------------------------------------------

  @Override
  public void onResume()
  {
    super.onResume();

    run();
  }

  //---------------------------------------------------------------------------

  @Override
  public void onPause()
  {
    m_screenRoot.removeCallbacks(this);

    super.onPause();
  }

  //---------------------------------------------------------------------------

  @Override
  public void run()
  {
    try
    {
      if( Trivia.Data.hasTriviaLoaded() && LeaderBoard.Data.hasLoaded() )
      {
        Intent intent = new Intent( this, StartScreen.class );
        startActivity( intent );
      }

      Thread.sleep( 1000 );

      // Animate the loading text.
      m_loadingCount++;
      if( m_loadingCount > 3 )
      {
        m_loadingCount = 0;
      }
      String loading = "Loading";
      for( byte i = 0; i < m_loadingCount; i++ )
      {
        loading += ".";
      }
      m_lblLoading.setText( loading );

      m_screenRoot.postDelayed( this, 1100 );
    }
    catch( Exception ex )
    {

    }
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.menu_loading, menu );
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
