/*
  This class manages logic for the question screen, including checking if an answer is correct
  and whether to proceed to another round or to the leaderboard-screen (if the game is over).
*/

package com.graeme.movienight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.graeme.movienight.Trivia.Question;

public class QuestionScreen extends ActionBarActivity implements Runnable
{
  //---------------------------------------------------------------------------

  private View m_questionScreenRoot = null;
  private Question m_question = null;
  private short m_timeRemainingInMs = Game.Logic.c_timePerRoundInSec * 1000;
  private byte m_timeRemainingInSecs = Game.Logic.c_timePerRoundInSec;

  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_question_screen );

    m_questionScreenRoot = findViewById( R.id.layQuestionScreenRoot );

    // Get the category name.
    String category = getIntent().getStringExtra( "Category" );

    // Get a question from the category.
    m_question = Trivia.Data.getQuestion( category );

    if( m_question == null )
    {
      return;
    }

    // Show the movie's poster.
    ImageView imgPoster = (ImageView)findViewById( R.id.imgPoster );
    imgPoster.setImageDrawable( m_question.m_image );

    // Update the round text.
    TextView lbl = (TextView)findViewById( R.id.lblRound );
    lbl.setText( getString( R.string.round ) +
      ' ' +
      String.valueOf( Game.Logic.getRoundCount() )  );

    // Set the question text.
    TextView txtQuestion = (TextView)findViewById( R.id.lblQuestion );
    txtQuestion.setText( m_question.m_text );

    // Set up the answer buttons.
    Button btnAnswer1 = (Button)findViewById( R.id.btnAnswer1 );
    Button btnAnswer2 = (Button)findViewById( R.id.btnAnswer2 );
    Button btnAnswer3 = (Button)findViewById( R.id.btnAnswer3 );
    btnAnswer1.setText( "A : " + m_question.m_answers[ 0 ] );
    btnAnswer2.setText( "B : " + m_question.m_answers[ 1 ] );
    btnAnswer3.setText( "C : " + m_question.m_answers[ 2 ] );
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
    m_questionScreenRoot.removeCallbacks(this);

    super.onPause();
  }

  //---------------------------------------------------------------------------

  public void run()
  {
    try
    {
      if( m_timeRemainingInSecs > 0 )
      {
        m_questionScreenRoot.postDelayed( this, 100 );

        Thread.sleep( 100 );

        m_timeRemainingInMs -= 100;
        m_timeRemainingInSecs = (byte)(m_timeRemainingInMs / 1000);

        ProgressBar pb = (ProgressBar)findViewById( R.id.pbTimer );
        pb.setProgress( m_timeRemainingInSecs );
      }
      else
      {
        testAnswer( -1 );
      }
    }
    catch( Exception ex )
    {
      // Ignore.
    }
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

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent( this, StartScreen.class );
    startActivity( intent );
  }

  //---------------------------------------------------------------------------

  public void btnAnswer_onClick( View view )
  {
    String tmp = String.valueOf( view.getContentDescription() );

    if( tmp.equals( "A1" ) )
    {
      testAnswer( 0 );
    }
    else if( tmp.equals( "A2" ) )
    {
      testAnswer( 1 );
    }
    else if( tmp.equals( "A3" ) )
    {
      testAnswer( 2 );
    }
  }

  //---------------------------------------------------------------------------

  // Checks if the selected answer was correct and displays the result to
  // the player via a dialog.

  private void testAnswer( int ans )
  {
    // Was the answer correct?
    final boolean isCorrect = ( ans == m_question.m_correctAnswer );

    if( isCorrect )
    {
      Game.Logic.updatePoints( m_timeRemainingInSecs );
    }

    // Show a dialog to the player indicating the outcome.
    String text = ( isCorrect ? "Correct!" : "Incorrect!" );
    String msg = ( Game.Logic.allowAnotherRound() ? "\nClick to start the next Round...\n" : "\nGame Over\n\nClick to see the leader-board...\n");

    AlertDialog.Builder dlg = new AlertDialog.Builder( this );
    dlg.setTitle( text );
    dlg.setMessage( msg );
    dlg.setPositiveButton( "Next Round!", new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick( DialogInterface d, int i )
      {
        proceed();
      }
    } );

    dlg.create().show();
  }

  //---------------------------------------------------------------------------

  // Starts another round or displays the leaderboard if the game is over.

  private void proceed()
  {
    // Start another round?
    if( Game.Logic.allowAnotherRound() )
    {
      Game.Logic.nextRound();

      Intent intent = new Intent( this, CategoryScreen.class );
      startActivity( intent );
    }
    else    // Go to the leaderboard.
    {
      Game.Logic.endRound();

      Intent intent = new Intent( this, LeaderBoardScreen.class );
      startActivity( intent );
    }
  }

  //---------------------------------------------------------------------------
}
