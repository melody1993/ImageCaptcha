package com.imagecaptcha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by w on 2018/2/6.
 */

public class VerifyBlock extends AppCompatImageView {
    private final DefaultCaptchaStrategy mStrategy;
    private Path blockpath;
    private final Paint bitmapPaint;
    int blocksize = Utils.dp2px(getContext(), 50);
    Paint verifyedPaint;
    Bitmap verifyBitmap;
    private PositionInfo shadowInfo;
    private PositionInfo blockPostion;
    public Context mcontext;
    public VerifyBlock(Context context) {
        this(context, null);
    }

    public VerifyBlock(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyBlock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mcontext=context;
        mStrategy = new DefaultCaptchaStrategy(context);
        bitmapPaint = new Paint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, bitmapPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (verifyedPaint == null) {
            Random random = new Random();
            int left = random.nextInt(getWidth() - blocksize + 1);
            int top = random.nextInt(getHeight() - blocksize + 1);
            if (top < 0) {
                top = 0;
            }
            shadowInfo = new PositionInfo(left, top);
            blockpath = invoke();
            verifyedPaint = new Paint();
            verifyedPaint.setColor(Color.parseColor("#000000"));
            verifyedPaint.setAlpha(160);

        }
        canvas.drawPath(blockpath, verifyedPaint);
        if (verifyBitmap == null) {

            verifyBitmap = createBlockBitmap();

            Random random = new Random();
            int left = random.nextInt(getWidth() - blocksize + 10);
            int top = random.nextInt(getHeight() - blocksize + 10);
            if (top < 0) {
                top = 0;
            }
            blockPostion = new PositionInfo(left, top);
        }
        canvas.drawBitmap(verifyBitmap, blockPostion.left, blockPostion.top, bitmapPaint);
    }

    private Bitmap createBlockBitmap() {
        Bitmap tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBitmap);
        //  getDrawable().setBounds(0, 0, getWidth(), getHeight());
        canvas.clipPath(blockpath);
        getDrawable().draw(canvas);
        mStrategy.decoreateSwipeBlockBitmap(canvas, blockpath);
 /*       Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{20,20},10));
        Path path = new Path(blockpath);
        canvas.drawPath(path,paint);*/
        return cropBitmap(tempBitmap);

    }

    private Bitmap cropBitmap(Bitmap bmp) {
        Bitmap result = null;
        result = Bitmap.createBitmap(bmp, shadowInfo.left, shadowInfo.top, blocksize, blocksize);
        bmp.recycle();
        return result;
    }

    public Path invoke() {
        int gap = (int) (blocksize / 5f);
        Path path = new Path();
        path.moveTo(0, gap);
        path.rLineTo(blocksize / 2.5f, 0);
        path.rLineTo(0, -gap);
        path.rLineTo(gap, 0);
        path.rLineTo(0, gap);
        path.rLineTo(2 * gap, 0);
        path.rLineTo(0, 4 * gap);
        path.rLineTo(-5 * gap, 0);
        path.rLineTo(0, -1.5f * gap);
        path.rLineTo(gap, 0);
        path.rLineTo(0, -gap);
        path.rLineTo(-gap, 0);
        path.close();
        path.offset(shadowInfo.left, shadowInfo.top);
        return path;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() < blockPostion.top || event.getY() > blockPostion.top + blocksize || event.getX() < blockPostion.left || event.getX() > blockPostion.left + blocksize) {
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }
    float downX,downY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float offseX=event.getX()-downX;
                float offseY=event.getY()-downY;
                blockPostion.left+=offseX;
                blockPostion.top+=offseY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                captcha();
                break;

        }
        downX=event.getX();
        downY=event.getY();
        return true;
    }

    private void captcha() {
        if (blockPostion.top== blockPostion.top&& blockPostion.left ==blockPostion.left + blocksize ) {
            Toast.makeText(mcontext,"验证成功",Toast.LENGTH_LONG).show();
        }
    }
}
