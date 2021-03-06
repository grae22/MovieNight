/*
  Logic for the start-screen.
*/

package com.graeme.movienight;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

public class StartScreen extends ActionBarActivity
{
  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_start_screen );
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.menu_start_screen, menu );
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

  public void btnStartGame_onClick( View view )
  {
    String name;

    //-- Get the name the player entered.

    // Get the textview & value.
    TextView txt = (TextView)findViewById( R.id.txtName );
    name = txt.getText().toString();

    // No name entered?
    if( name.isEmpty() )
    {
      // Ask the player to enter their name.
      AlertDialog.Builder alert =
        new AlertDialog.Builder( this );
      alert.setMessage( "Please enter a name." );
      alert.setTitle( "Your Name" );
      alert.setPositiveButton( "OK", null );
      alert.create().show();

      // Give focus back to the textview.
      txt.requestFocus();

      // We can't proceed until a name is entered.
      return;
    }

    //-- Start the game.
    Game.Logic.m_playerName = name;
    Game.Logic.newGame();

    Intent intent = new Intent( this, CategoryScreen.class );
    startActivity( intent );
  }

  //---------------------------------------------------------------------------

  @Override
  public void onBackPressed()
  {
    // Do nothing.
  }

  //---------------------------------------------------------------------------
}
