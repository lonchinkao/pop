package com.lonchin.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;


public class MainView extends View {

    public final ArrayList<ShapeHolder> balls = new ArrayList<>();
    AnimatorSet animation = null;
    float size, xstart, ystart;
    int numX, numY;


    public MainView(Context context) {
        super(context);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        numX = 10;
        numY = 10;

        float size = width / (numX + 1);
        float sx = size;
        float sy = (height / 2) - (size * numY / 2);

        xstart = sx;
        ystart = sy;
        for (int i = 0; i < numY; i++) {
            sx = xstart;
            for (int j = 0; j < numX; j++) {
                ShapeHolder newBall = addBall(sx, sy, size);
                sx = sx + size;
            }
            sy = sy + size;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN &&
                event.getAction() != MotionEvent.ACTION_MOVE) {
            return false;
        }
        ShapeHolder newBall = findBall(event.getX(), event.getY());
        // Bouncing animation with squash and stretch
        float startY = newBall.getY();
        float endY = getHeight() - 50f;
        float h = (float)getHeight();
        float eventY = event.getY();
        int duration = (int)(500 * ((h - eventY)/h));
        ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
        bounceAnim.setDuration(duration);
        bounceAnim.setInterpolator(new AccelerateInterpolator());
        ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                newBall.getX() - 25f);
        squashAnim1.setDuration(duration/4);
        squashAnim1.setRepeatCount(1);
        squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim1.setInterpolator(new DecelerateInterpolator());
        ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                newBall.getWidth() + 50);
        squashAnim2.setDuration(duration/4);
        squashAnim2.setRepeatCount(1);
        squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
        squashAnim2.setInterpolator(new DecelerateInterpolator());
        ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,
                endY + 25f);
        stretchAnim1.setDuration(duration/4);
        stretchAnim1.setRepeatCount(1);
        stretchAnim1.setInterpolator(new DecelerateInterpolator());
        stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                newBall.getHeight(), newBall.getHeight() - 25);
        stretchAnim2.setDuration(duration/4);
        stretchAnim2.setRepeatCount(1);
        stretchAnim2.setInterpolator(new DecelerateInterpolator());
        stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,
                startY);
        bounceBackAnim.setDuration(duration);
        bounceBackAnim.setInterpolator(new DecelerateInterpolator());
        // Sequence the down/squash&stretch/up animations
        AnimatorSet bouncer = new AnimatorSet();
        bouncer.play(bounceAnim).before(squashAnim1);
        bouncer.play(squashAnim1).with(squashAnim2);
        bouncer.play(squashAnim1).with(stretchAnim1);
        bouncer.play(squashAnim1).with(stretchAnim2);
        bouncer.play(bounceBackAnim).after(stretchAnim2);

        // Fading animation - remove the ball when the animation is done
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
        fadeAnim.setDuration(250);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                balls.remove(((ObjectAnimator)animation).getTarget());

            }
        });

        // Sequence the two animations to play one after the other
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(bouncer).before(fadeAnim);

        // Start the animation
        animatorSet.start();

        return true;
    }

    private ShapeHolder findBall(float x, float y) {

        int x0 = (int)((x - xstart)/ size);
        int y0 = (int)((y - ystart)/ size);
        int index = y0 * (numY - 1) + x0;
        ShapeHolder shapeHolder = balls.get(index);
        return shapeHolder;
    }

    private int getrandomcolor(int numofcolor)
    {
        int c = (int) (Math.random()*numofcolor);
        int c0;
        switch (c) {
            case 0: c0 = getResources().getColor(R.color.gray); break;
            case 1: c0 = getResources().getColor(R.color.blue); break;
            case 2: c0 = getResources().getColor(R.color.green); break;
            case 3: c0 = getResources().getColor(R.color.red); break;
            case 4: c0 = getResources().getColor(R.color.silver); break;
            case 5: c0 = getResources().getColor(R.color.aqua); break;
            case 6: c0 = getResources().getColor(R.color.fuchsia); break;
            case 7: c0 = getResources().getColor(R.color.purple); break;
            case 8: c0 = getResources().getColor(R.color.yellow); break;

            default: c0 = getResources().getColor(R.color.navy); break;
        }
        return c0;
    }
    private ShapeHolder addBall(float x, float y, float size) {
        float s = size;
        OvalShape circle = new OvalShape();
        circle.resize(s, s);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x - s/2);
        shapeHolder.setY(y - s/2);

        int intcolor = getrandomcolor(4);
        int red = intcolor >> 16 & 0xff;
        int green = intcolor >> 8 & 0xff;
        int blue = intcolor & 0xff;
        int color = 0xff000000 | red << 16 | green << 8 | blue;
        Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
        int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                50f, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);
        return shapeHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < balls.size(); ++i) {
            ShapeHolder shapeHolder = balls.get(i);
            canvas.save();
            canvas.translate(shapeHolder.getX(), shapeHolder.getY());
            shapeHolder.getShape().draw(canvas);
            canvas.restore();
        }
    }
}


