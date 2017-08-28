package com.example.zhenqiangli.customview.customized;

import android.view.MotionEvent;

import java.util.LinkedList;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/*
* TouchEvent to action
* */
public class MotionTracker {
    static final int MOTION_TRACKER_ACTION_UNTRACK = 0;
    static final int MOTION_TRACKER_ACTION_CLICK = 0x11;               // click -> show definition
    static final int MOTION_TRACKER_ACTION_DOUBLE_CLICK = 0x12;        // double click ->
    static final int MOTION_TRACKER_ACTION_DRAG = 0x20;                // drag -> read more content?
    static final int MOTION_TRACKER_ACTION_PRESS = 0x21;               // press -> select word
    static final int MOTION_TRACKER_ACTION_PRESS_AND_DRAG = 0x22;      // press and drag -> select more content
    static final int MOTION_TRACKER_ACTION_INIT = 0xff;

    public static final long CLICK_GAP_TIME_THRESHOLD = 200;
    public static final long CLICK_TIME_THRESHOLD = 200;
    public static final float CLICK_MOVE_THRESHOLD = 10.0f;

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) sqrt(pow(x1 - x2, 2.0) + pow(y1 - y2, 2.0));
    }

    public static class EventInfo {
        private float x;
        private float y;
        private long eventTime;
        private int action;
        EventInfo(MotionEvent e) {
            x = e.getX();
            y = e.getY();
            eventTime = e.getEventTime();
            action = e.getAction();
        }

        public EventInfo(float x, float y, long eventTime, int action) {
            this.x = x;
            this.y = y;
            this.eventTime = eventTime;
            this.action = action;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public long getEventTime() {
            return eventTime;
        }

        public int getAction() {
            return action;
        }
    }

    private EventInfo lastEventInfo;
    private int lastAction = MOTION_TRACKER_ACTION_INIT;

    int actionFrom(EventInfo event) {
        int step = 0, res = MOTION_TRACKER_ACTION_INIT;
        switch (event.getAction()) {
            case ACTION_DOWN:
                lastEventInfo = event;
                step = 1;
                res = MOTION_TRACKER_ACTION_UNTRACK;
                break;

            case ACTION_CANCEL:
                lastEventInfo = null;
                step = 2;
                res = MOTION_TRACKER_ACTION_INIT;
                break;

            case ACTION_MOVE: // press OR drag
                if (event.getEventTime() - lastEventInfo.getEventTime() < CLICK_TIME_THRESHOLD
                        && distance(lastEventInfo.getX(), lastEventInfo.getY(), event.getX(), event.getY()) < CLICK_MOVE_THRESHOLD) {
                    step  =3;
                    res = MOTION_TRACKER_ACTION_UNTRACK;
                } else if (distance(lastEventInfo.getX(), lastEventInfo.getY(), event.getX(), event.getY()) >= CLICK_MOVE_THRESHOLD) {
                    lastEventInfo = event;
                    if (lastAction == MOTION_TRACKER_ACTION_INIT) {
                        step = 4;
                        res = MOTION_TRACKER_ACTION_DRAG;
                    } else if (lastAction == MOTION_TRACKER_ACTION_PRESS) {
                        step = 5;
                        res = MOTION_TRACKER_ACTION_PRESS_AND_DRAG;
                    } else {
                        step = 6;
                        res = MOTION_TRACKER_ACTION_UNTRACK;
                    }
                } else {
                    lastEventInfo = event;
                    if (lastAction == MOTION_TRACKER_ACTION_PRESS) {
                        step = 7;
                        res = MOTION_TRACKER_ACTION_UNTRACK;
                    } else {
                        step = 8;
                        res = MOTION_TRACKER_ACTION_PRESS;
                    }
                }
                break;

            case ACTION_UP: // click * x
                if (event.getEventTime() - lastEventInfo.getEventTime() >= CLICK_TIME_THRESHOLD
                        || distance(lastEventInfo.getX(), lastEventInfo.getY(), event.getX(), event.getY()) >= CLICK_MOVE_THRESHOLD) {
                    step = 9;
                    res = MOTION_TRACKER_ACTION_UNTRACK;
                    break;
                }

                if (lastAction == MOTION_TRACKER_ACTION_INIT) {
                    step = 10;
                    res = MOTION_TRACKER_ACTION_CLICK;
                } else if (lastAction == MOTION_TRACKER_ACTION_CLICK){
                    step = 11;
                    res = MOTION_TRACKER_ACTION_DOUBLE_CLICK;
                    return MOTION_TRACKER_ACTION_DOUBLE_CLICK;
                } else {
                    step = 12;
                    res = MOTION_TRACKER_ACTION_UNTRACK;
                }
                break;
            default:
                step = 13;
                res = MOTION_TRACKER_ACTION_UNTRACK;
        }
        return res;
    }
}