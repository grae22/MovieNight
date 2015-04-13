/*
  This class handles logic for the leaderboard screen.
*/

package com.graeme.movienight;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;


public class LeaderBoardScreen extends ActionBarActivity
{
  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_leader_board_screen );

    // Get the text-views.
    TextView[] lblNames = new TextView[ 5 ];
    lblNames[ 0 ] = (TextView)findViewById( R.id.lblName1 );
    lblNames[ 1 ] = (TextView)findViewById( R.id.lblName2 );
    lblNames[ 2 ] = (TextView)findViewById( R.id.lblName3 );
    lblNames[ 3 ] = (TextView)findViewById( R.id.lblName4 );
    lblNames[ 4 ] = (TextView)findViewById( R.id.lblName5 );

    TextView[] lblPoints = new TextView[ 5 ];
    lblPoints[ 0 ] = (TextView)findViewById( R.id.lblPoints1 );
    lblPoints[ 1 ] = (TextView)findViewById( R.id.lblPoints2 );
    lblPoints[ 2 ] = (TextView)findViewById( R.id.lblPoints3 );
    lblPoints[ 3 ] = (TextView)findViewById( R.id.lblPoints4 );
    lblPoints[ 4 ] = (TextView)findViewById( R.id.lblPoints5 );

    // Sort the leaderboard by points.
    Collections.sort( LeaderBoard.Data.m_entries );

    // Update text-views with leaderboard entries.
    for( byte i = 0; i < 5; i++ )
    {
      String name;
      String points;

      if( LeaderBoard.Data.m_entries.size() > i )
      {
        name = LeaderBoard.Data.m_entries.get( i ).m_name;
        points = String.valueOf( LeaderBoard.Data.m_entries.get( i ).m_points );
      }
      else
      {
        name = new String();
        points = new String();
      }

      lblNames[ i ].setText( name );
      lblPoints[ i ].setText( points );
    }
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.menu_leader_board_screen, menu );
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

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent( this, StartScreen.class );
    startActivity( intent );
  }

  //---------------------------------------------------------------------------

  public void onRestartPressed( View view )
  {
    Game.Logic.newGame();

    Intent intent = new Intent( this, CategoryScreen.class );
    startActivity( intent );
  }

  //---------------------------------------------------------------------------
}
