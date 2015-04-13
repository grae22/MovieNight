/*
  This class handles the logic for the Category-Screen - where categories are chosen
  by spinning the wheel.
*/

package com.graeme.movienight;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.view.View;
import android.content.Intent;
import java.util.Vector;

public class CategoryScreen extends ActionBarActivity implements Runnable
{
  //---------------------------------------------------------------------------

  private View m_categoryScreenRoot;
  private Vector<String> m_categories = new Vector<String>();
  private SpinningWheel m_wheel;
  private int m_wheelSpeed = 0;
  private TextView m_lblCategory;

  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_category_screen );

    m_categoryScreenRoot = findViewById( R.id.layCategoryScreenRoot );

    // Add the categories.
    m_categories.add( "Comedy" );
    m_categories.add( "Action" );
    m_categories.add( "Horror" );
    m_categories.add( "Romance" );
    m_categories.add( "Sci-Fi" );
    m_categories.add( "Thriller" );

    // Init the 'round' label.
    UpdateRoundLabel();

    // Create a spinning-wheel & add it to the ui.
    m_wheel = new SpinningWheel( getApplicationContext(), (byte)m_categories.size() );

    FrameLayout wheelFrame = (FrameLayout)findViewById( R.id.fraWheel );
    wheelFrame.addView( m_wheel );

    // Get the category ui textview so we can set it as the wheel spins.
    m_lblCategory = (TextView)findViewById( R.id.lblCategory );
  }

  //---------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.menu_category_screen, menu );
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

  // Updates the 'round' label to the current 'round' number, e.g. "Round 1".

  private void UpdateRoundLabel()
  {
    TextView lbl = (TextView)findViewById( R.id.lblRound );
    lbl.setText( getString( R.string.round ) +
                 ' ' +
                 String.valueOf( Game.Logic.getRoundCount() )  );
  }

  //---------------------------------------------------------------------------

  public void btnSpin_onClick( View view )
  {
    m_wheel.update( true, 0.0f );
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
    m_categoryScreenRoot.removeCallbacks(this);

    super.onPause();
  }

  //---------------------------------------------------------------------------

  public void run()
  {
    // UI.
    m_categoryScreenRoot.postDelayed( this, 110 );

    // Logic.
    update( 0.1f );

    try
    {
      Thread.sleep( 100 );
    }
    catch( Exception ex )
    {
      // Ignore.
    }
  }

  //---------------------------------------------------------------------------

  private void update( final float deltaTimeInSec )
  {
    // Update the spinning wheel.
    m_wheel.update( false, deltaTimeInSec );

    // Update the text of the selected category.
    short categoryIndex = m_wheel.getCategoryIndex();

    if( categoryIndex >= 0 &&
        categoryIndex < m_categories.size() )
    {
      m_lblCategory.setText( m_categories.elementAt( categoryIndex ) );
    }

    // Stopped spinning?
    if( m_wheel.hasStoppedSpinning() )
    {
      //-- Go to the question screen.
      Intent intent = new Intent( this, QuestionScreen.class );
      intent.putExtra( "Category", String.valueOf( m_lblCategory.getText() ) );
      startActivity( intent );
    }
  }

  //---------------------------------------------------------------------------

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent( this, StartScreen.class );
    startActivity( intent );
  }

  //---------------------------------------------------------------------------
}
