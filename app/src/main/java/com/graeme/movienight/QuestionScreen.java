package com.graeme.movienight;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.graeme.movienight.Trivia.Question;

public class QuestionScreen extends ActionBarActivity
{
  //---------------------------------------------------------------------------

  private Question m_question = null;

  //---------------------------------------------------------------------------

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_question_screen );

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
      String.valueOf( Game.Logic.GetRoundCount() )  );

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
}
