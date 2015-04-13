/*
  All logic and drawing for the spinning-wheel.
*/

package com.graeme.movienight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class SpinningWheel extends View
{
  //-------------------------------------------------------------------------

  private final int c_minAcceleration = 350;    // Min acceleration force when spinning.
  private final int c_maxAcceleration = 500;    // Max acceleration force when spinning.
  private final int c_accelerationTime = 1;     // Time to accelerate for before friction applied.
  private final int c_frictionForce = 200;      // Friction force to slow the wheel.

  private byte m_categoryCount = 0;             // Number of categories this wheel has.
  private short m_angleStep = 0;                // Angle step size for each category.
  private float m_angle = 0.0f;                 // Current wheel angle.
  private float m_speed = 0.0f;                 // Current wheel speed.
  private short m_acceleration = 0;             // Current acceleration value.
  private float m_accelerationTime = 0.0f;      // Tracks how long wheel has been accelerating for.
  private boolean m_stoppedSpinning = false;    // 'true' if the wheel was spinning and has stopped.

  //-------------------------------------------------------------------------

  public SpinningWheel( Context context,
                        byte categoryCount )
  {
    super( context );

    m_categoryCount = categoryCount;

    // Calculate the angle-step.
    m_angleStep = 360;

    if( m_categoryCount > 0 )
    {
      m_angleStep = (short)( 360.0 / m_categoryCount );
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
      m_categoryCount,
      canvas,
      paint );

    drawWheelOutlineAndSpokes( wheelOriginX,
      wheelOriginY,
      radius,
      angle,
      m_angleStep,
      m_categoryCount,
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

    // Draw the wheel's shadow
    paint.setARGB( 255, 0, 0, 0 );
    canvas.drawCircle( x + 2, y + 2, r, paint );

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

    // Draw the wheel outline.
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

  public short getCategoryIndex()
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

        if( m_speed < 1.0f )
        {
          m_stoppedSpinning = true;
          m_speed = 0.0f;
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
