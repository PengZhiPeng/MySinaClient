package com.example.acer.myrecycleviewtext.utils;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by acer on 2016/2/7.
 */
public class TextViewFixTouchConsume extends TextView {

    private boolean dontConsumeNonUrlClick = true;
    private boolean linkHit;

    public TextViewFixTouchConsume(Context context) {
        super(context);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);
        if (dontConsumeNonUrlClick) {
            return linkHit;
        }
        return res;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

    public static class localLinkMovementMethod extends LinkMovementMethod {
        private static localLinkMovementMethod sInstance;

        public static localLinkMovementMethod getsInstance(){
            if (sInstance == null)
                sInstance = new localLinkMovementMethod();
            return sInstance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN){
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    if (widget instanceof TextViewFixTouchConsume){
                        ((TextViewFixTouchConsume) widget).linkHit = true;
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }
    }
}
