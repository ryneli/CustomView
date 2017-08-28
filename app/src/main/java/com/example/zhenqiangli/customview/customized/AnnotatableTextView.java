package com.example.zhenqiangli.customview.customized;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * TextView with features for long text reading.
 * Features:
 * 1) void onWordClicked(String word, int offset)
 * 2) void addAnnotation(int offset, String annotation)
 * 3) Sentence getSentence(int offset)
 * 4) void underline(int startOffset, int endOffset)
 */

public class AnnotatableTextView extends AppCompatTextView implements GestureDetector.OnGestureListener, GestureDetector.OnContextClickListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "AnnotatableTextView";
    private static float annotationHeight = 24.0f;
    private static float annotationSpace = 10.0f;
    private float originalLineHeight;
    private CharSequence text;
    private GestureDetector gestureDetector;

    public AnnotatableTextView(Context context) {
        this(context, null);
    }

    public AnnotatableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnotatableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, this);
        originalLineHeight = getLineHeight();
        text = getText();
        setLineSpacing(getLineSpacingExtra() + annotationHeight + annotationSpace, getLineSpacingMultiplier());
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(2.0f);
        p.setTextSize(annotationHeight);
        // get word width here
        // need to get word index
        for (int i = 0; i < getLineCount(); i++) {
            Rect bound = new Rect();
            int baseline = getLineBounds(i, bound);
            canvas.drawLine(bound.left, baseline, bound.right, baseline, p);
        }

        for (Annotation annotation : annotations) {
            canvas.drawText(annotation.word, annotation.x, annotation.baseline, p);
        }

    }

    public static class Sentence {
        private int start;
        private int end;

        public Sentence(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

    private static class Annotation {
        float x;
        float baseline;
        String word;

        Annotation(float x, float baseline, String word) {
            this.x = x;
            this.baseline = baseline;
            this.word = word;
        }
    }

    private LinkedList<Annotation> annotations = new LinkedList<>();

    private MotionTracker motionTracker = new MotionTracker();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private static void L(String tag, String fmt, Object... args) {
        Log.d(tag, String.format(fmt, args));
    }

    private void onClick(float x, float y) {
        L(TAG, "onCLick (%s, %s)", x, y);
        Rect bound = new Rect();
        int baseline = getLineBounds(getLineAtCoordinate(y), bound);
        annotations.add(new Annotation(
                x,
                baseline - originalLineHeight,
                getWord(getOffsetForPosition(x, y))));
        invalidate();
    }

    private void onDrag(float currentX, float currentY) {
        L(TAG, "onDrag (%s, %s)", currentX, currentY);
    }

    private String getWord(int offset) {
        L(TAG, "offset %s", offset);
        if (offset < 0 || offset >= text.length()) return String.valueOf(offset);
        int start = offset, end = offset;
        while (start != 0 && text.charAt(start) != ' ') start--;
        while (end != text.length() && text.charAt(end) != ' ') end++;
        return new StringBuffer(text.subSequence(start, end)).toString();
    }

    private int getLineAtCoordinate(float y) {
        y -= getTotalPaddingTop();
        // Clamp the position to inside of the view.
        y = Math.max(0.0f, y);
        y = Math.min(getHeight() - getTotalPaddingBottom() - 1, y);
        y += getScrollY();
        return getLayout().getLineForVertical((int) y);
    }

    public void setAnnotatableText(List<String> words) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (String word : words) {
            builder.append(word);
            builder.setSpan(new ClickableWordSpan(word), builder.length() - word.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(" ");
        }
    }

    private class ClickableWordSpan extends ClickableSpan {
        private String word;

        public ClickableWordSpan(String word) {
            this.word = word;
        }

        @Override
        public void onClick(View view) {
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        L(TAG, "onDown %s", motionEvent);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        L(TAG, "onShowPress %s", motionEvent);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        L(TAG, "onSingleTapUp %s", motionEvent);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        L(TAG, "onScroll %s", motionEvent);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        L(TAG, "onLongPress %s", motionEvent);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        L(TAG, "onFling %s", motionEvent);
        return false;
    }

    @Override
    public boolean onContextClick(MotionEvent motionEvent) {
        L(TAG, "onContextClick %s", motionEvent);
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        L(TAG, "onSingleTapConfirmed %s", motionEvent);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        L(TAG, "onDoubleTap %s", motionEvent);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        L(TAG, "onDoubleTapEvent %s", motionEvent);
        return false;
    }
}
