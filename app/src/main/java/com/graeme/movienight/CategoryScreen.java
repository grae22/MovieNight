package com.graeme.movienight;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Rect;
import java.util.Vector;

public class CategoryScreen extends ActionBarActivity implements Runnable
{
  //---------------------------------------------------------------------------

  private View m_categoryScreenRoot;
  private byte m_roundCount = 1;
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

    m_categories.add( "Comedy" );
    m_categories.add( "Action" );
    m_categories.add( "Horror" );
    m_categories.add( "Romance" );
    m_categories.add( "Sci-Fi" );
    m_categories.add( "Thriller" );

    m_categoryScreenRoot = findViewById( R.id.layCategoryScreenRoot );

    UpdateRoundLabel();

    m_wheel = new SpinningWheel( getApplicationContext() );

    FrameLayout wheelFrame = (FrameLayout)findViewById( R.id.fraWheel );
    wheelFrame.addView( m_wheel );

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

  private void UpdateRoundLabel()
  {
    TextView lbl = (TextView)findViewById( R.id.lblRound );
    lbl.setText( getString( R.string.round ) +
                 ' ' +
                 String.valueOf( m_roundCount )  );
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
    m_categoryScreenRoot.postDelayed( this, 50 );

    // Logic.
    update( 0.05f );

    try
    {
      Thread.sleep( 50 );
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
      try
      {
        Thread.sleep( 1000 );
      }
      catch( Exception ex )
      {
        // Do nothing.
      }

      //-- Go to the question screen.
      Intent intent = new Intent( this, QuestionScreen.class );
      intent.putExtra(  "Category", String.valueOf( m_lblCategory.getText() ) );
      startActivity( intent );
    }
  }

  //===========================================================================

  public class SpinningWheel extends View
  {
    //-------------------------------------------------------------------------

    private final int c_minAcceleration = 200;    // Min acceleration force when spinning.
    private final int c_maxAcceleration = 350;    // Max acceleration force when spinning.
    private final int c_accelerationTime = 2;     // Time to accelerate for before friction applied.
    private final int c_frictionForce = 150;      // Friction force to slow the wheel.

    private short m_angleStep = 0;                // Angle step size for each category.
    private float m_angle = 0.0f;                 // Current wheel angle.
    private float m_speed = 0.0f;                 // Current wheel speed.
    private short m_acceleration = 0;             // Current acceleration value.
    private float m_accelerationTime = 0.0f;      // Tracks how long wheel has been accelerating for.
    private boolean m_stoppedSpinning = false;    // 'true' if the wheel was spinning and has stopped.

    //-------------------------------------------------------------------------

    public SpinningWheel( Context context )
    {
      super( context );

      // Calculate the angle-step.
      m_angleStep = 360;

      if( m_categories.size() > 0 )
      {
        m_angleStep = (short)( 360.0 / m_categories.size() );
      }

      // We start at 270 since that's the 'north' position where the
      // category indicator is.
      m_angle = 270;
    }

    //-------------------------------------------------------------------------

    @Override
    protected void onDraw( Canvas canvas )
    {
      super.onDraw( canvas );

      // Save the current angle (may be altered in other thread).
      int angle = (int)m_angle;

      // Calc our wheel's position.
      Rect clipBounds = canvas.getClipBounds();
      int wheelOriginX = clipBounds.centerX();
      int wheelOriginY = clipBounds.centerY();

      // Calc our wheel's radius - will be less than half (as it's a radius)
      // the available min width or height (whichever is smaller).
      int radius = 0;
      if( clipBounds.width() > canvas.getHeight() )
      {
        radius = (int) ( clipBounds.height() * 0.35 );
      }
      else
      {
        radius = (int) ( clipBounds.width() * 0.35 );
      }

      // Drawing.
      Paint paint = new Paint();
      paint.setAntiAlias( true );

      drawWheelWedges( wheelOriginX,
                       wheelOriginY,
                       radius,
                       angle,
                       m_angleStep,
                       m_categories.size(),
                       canvas,
                       paint );

      drawWheelOutlineAndSpokes( wheelOriginX,
                                 wheelOriginY,
                                 radius,
                                 angle,
                                 m_angleStep,
                                 m_categories.size(),
                                 canvas,
                                 paint );

      drawChosenCategoryIndicator( wheelOriginX,
                                   wheelOriginY,
                                   radius,
                                   canvas,
                                   paint );
    }

    //-------------------------------------------------------------------------

    private void drawWheelWedges( int x,
                                  int y,
                                  int r,
                                  int angle,
                                  int angleStep,
                                  int wedgeCount,
                                  Canvas canvas,
                                  Paint paint )
    {
      // Wedges must be filled.
      paint.setStyle( Paint.Style.FILL );

      // Iterate through the wedges, drawing them as we go and
      // choosing colours.
      int[] rgb = { 255, 0, 0 };

      for( int i = 0; i < wedgeCount; i++ )
      {
        // Draw the current category's wedge.
        paint.setARGB( 255, rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] );

        drawWedge( x, y, r, angle, angleStep, canvas, paint );

        angle += angleStep;

        // Pick the next wedge's colour, we loop until we get a colour
        // that isn't too dark.
        do
        {
          // Figure out which component to modify.
          int rgbComponentToModify = ( i % 3 );
          rgb[ rgbComponentToModify ] -= 64;

          // Keep the component's value in range.
          if( rgb[ rgbComponentToModify ] < 0 )
          {
            rgb[ rgbComponentToModify ] = 255;
          }
          else if( rgb[ rgbComponentToModify ] > 255 )
          {
            rgb[ rgbComponentToModify ] = 0;
          }

        } while( rgb[ 0 ] + rgb[ 1 ] + rgb[ 2 ] < 100 );
      }
    }

    //-------------------------------------------------------------------------

    // Draws a 'wedge' of the wheel.

    private void drawWedge( int x,
                            int y,
                            int r,
                            int angle,
                            int angleStep,
                            Canvas canvas,
                            Paint paint )
    {
      // Calc position on circle radius for the current angle.
      int rx = (int)( x + ( Math.cos( Math.toRadians( angle ) ) * r ) );
      int ry = (int)( y - ( Math.sin( Math.toRadians( angle ) ) * r ) );

      // Create a path from the circle's origin, an arc for the wedge's
      // curved bit and then a line back to the origin.
      Path path = new Path();
      path.moveTo( x, y );
      path.lineTo( rx, ry );

      RectF rect = new RectF( x - r, y - r, x + r, y + r );
      path.arcTo( rect, angle, angleStep, true );

      path.lineTo( x, y );

      // Draw the wedge.
      canvas.drawPath( path, paint );
    }

    //-------------------------------------------------------------------------

    private void drawWheelOutlineAndSpokes( int x,
                                            int y,
                                            int r,
                                            int angle,
                                            int angleStep,
                                            int spokeCount,
                                            Canvas canvas,
                                            Paint paint )
    {
      // Set the colour.
      paint.setColor( Color.argb( 255, 64, 64, 64 ) );
      paint.setStyle( Paint.Style.STROKE );

      // Draw the wheel out^line.
      canvas.drawCircle( x, y, r, paint );

      // Draw spokes.
      for( int i = 0; i < spokeCount; i++ )
      {
        // Calc position on circle radius for the current angle.
        int rx = (int)( x + ( Math.cos( Math.toRadians( 360-angle ) ) * r ) );
        int ry = (int)( y - ( Math.sin( Math.toRadians( 360-angle ) ) * r ) );

        // Draw line.
        canvas.drawLine( x, y, rx, ry, paint );

        // Next angle.
        angle += angleStep;
      }
    }

    //-------------------------------------------------------------------------

    private void drawChosenCategoryIndicator( int x,
                                              int y,
                                              int r,
                                              Canvas canvas,
                                              Paint paint )
    {
      paint.setColor( Color.BLACK );
      paint.setStyle( Paint.Style.FILL );

      drawWedge( x,
                 y - r + 10,
                 40,
                 250,
                 40,
                 canvas,
                 paint );
    }

    //-------------------------------------------------------------------------

    private short getCategoryIndex()
    {
      // We need to add 270 since we started drawing at 0 deg (positive x-axis),
      // but the category chooser indicator is at 270 deg (neg y-axis).
      short angle = (short)( m_angle - 270 );

      while( angle < 0 ) angle += 360;

      // Dividing the angle by the angle-range will give us the category's index,
      // we need to modulus as the angle may now be greater than 360 (since we
      // added 270).
      return (short)( ( angle / m_angleStep ) );// % ( m_categories.size() ) );
    }

    //-------------------------------------------------------------------------

    public boolean hasStoppedSpinning()
    {
      return m_stoppedSpinning;
    }

    //-------------------------------------------------------------------------

    // Called to start spinning the wheel, and also to update it's rotation
    // and cause a redraw.

    public void update( boolean startSpinning,
                        float deltaTimeInSec )
    {
      // Start spinning? Only if the wheel isn't moving already -
      // we don't want the player spinning again while it's moving.
      if( startSpinning && m_speed < 0.01 )
      {
        // Calc a semi-random acceleration for this spin - we don't want to
        // always spin the same amount (this will bias the categories chosen).
        m_acceleration =
          (short)( c_minAcceleration +
            ( Math.random() * ( c_maxAcceleration - c_minAcceleration ) ) );

        // Reset some vars.
        m_accelerationTime = 0.0f;
        m_speed = 0.01f;
      }
      // Wheel is moving? Update the speed and angle.
      else if( m_speed > 0.0f )
      {
        // We're still in the acceleration phase?
        if( m_accelerationTime < c_accelerationTime )
        {
          m_accelerationTime += deltaTimeInSec;
          m_speed = ( m_acceleration * m_accelerationTime );
        }
        else  // No longer accelerating - apply friction to slow the wheel.
        {
          m_speed -= ( c_frictionForce * deltaTimeInSec );

          if( m_speed <= 0.0f )
          {
            m_stoppedSpinning = true;
          }
        }

        // Update the angle based on speed.
        m_angle += ( m_speed * deltaTimeInSec );

        while( m_angle > 359.99f )
        {
          m_angle -= 360.0f;
        }

        // Force a redraw of the wheel.
        postInvalidate();
      }
    }

    //-------------------------------------------------------------------------
  }

  //---------------------------------------------------------------------------
}
