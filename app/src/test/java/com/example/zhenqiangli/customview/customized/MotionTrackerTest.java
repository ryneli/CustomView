package com.example.zhenqiangli.customview.customized;

import android.support.v4.util.Pair;
import android.view.MotionEvent;

import junit.framework.TestCase;

/**
 * Created by zhenqiangli on 8/27/17.
 */

public class MotionTrackerTest extends TestCase{
    private MotionTracker motionTracker;
    private static final int NULL = -1;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        motionTracker = new MotionTracker();
    }

    public void testUntrackActionDown() {
        MotionTracker.EventInfo e = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);

        assertActionArray(motionTracker, Pair.create(MotionTracker.MOTION_TRACKER_ACTION_UNTRACK, e));
    }

    public void testReturnUntrack_whenMoveQuicklyAndClose() {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, MotionTracker.CLICK_TIME_THRESHOLD / 2, MotionEvent.ACTION_MOVE);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_UNTRACK, e2));
    }


    public void testReturnDrag_whenMoveNotCloseAndNotQuickly() {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(100, 100, MotionTracker.CLICK_TIME_THRESHOLD, MotionEvent.ACTION_MOVE);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_DRAG, e2));
    }

    public void testReturnPress_whenMoveCloseAndNotQuickly() {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, MotionTracker.CLICK_TIME_THRESHOLD, MotionEvent.ACTION_MOVE);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_PRESS, e2));
    }

    public void testReturnDrag_whenMoveNotCloseAndQuickly() {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(100, 100, MotionTracker.CLICK_TIME_THRESHOLD/2, MotionEvent.ACTION_MOVE);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_DRAG, e2));
    }

    public void testReturnUntrack_whenCancel() {
        MotionTracker.EventInfo e = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_CANCEL);

        assertActionArray(motionTracker,
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_UNTRACK, e));
    }

    public void testReturnPressAndDrag_whenActionDownForAWhileThenMoveFar() {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, 0, MotionEvent.ACTION_DOWN);
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, MotionTracker.CLICK_TIME_THRESHOLD, MotionEvent.ACTION_MOVE);
        MotionTracker.EventInfo e3 = new MotionTracker.EventInfo(100, 100, MotionTracker.CLICK_TIME_THRESHOLD+1, MotionEvent.ACTION_MOVE);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_PRESS, e2),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_PRESS_AND_DRAG, e3));
    }

    public void testReturnClick_whenActionDownThenActionUpQuickly() {
        int time = 0;
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, time, MotionEvent.ACTION_DOWN);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2;
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, time, MotionEvent.ACTION_MOVE);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2 - 1;
        MotionTracker.EventInfo e3 = new MotionTracker.EventInfo(3, 3, time, MotionEvent.ACTION_UP);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(NULL, e2),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_CLICK, e3));
    }

    public void testReturnDoubleClick() {
        int time = click(0);
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, time, MotionEvent.ACTION_DOWN);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2;
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, time, MotionEvent.ACTION_MOVE);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2 - 1;
        MotionTracker.EventInfo e3 = new MotionTracker.EventInfo(3, 3, time, MotionEvent.ACTION_UP);

        assertActionArray(motionTracker,
                Pair.create(NULL, e1),
                Pair.create(NULL, e2),
                Pair.create(MotionTracker.MOTION_TRACKER_ACTION_DOUBLE_CLICK, e3));
    }

    private int click(int time) {
        MotionTracker.EventInfo e1 = new MotionTracker.EventInfo(0, 0, time, MotionEvent.ACTION_DOWN);
        motionTracker.actionFrom(e1);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2;
        MotionTracker.EventInfo e2 = new MotionTracker.EventInfo(1, 1, time, MotionEvent.ACTION_MOVE);
        motionTracker.actionFrom(e2);
        time += MotionTracker.CLICK_TIME_THRESHOLD / 2 - 1;
        MotionTracker.EventInfo e3 = new MotionTracker.EventInfo(3, 3, time, MotionEvent.ACTION_UP);
        motionTracker.actionFrom(e3);
        return time;
    }

    @SafeVarargs
    private static void assertActionArray(MotionTracker motionTracker, Pair<Integer, MotionTracker.EventInfo>... conditions) {
        for (Pair<Integer, MotionTracker.EventInfo> condition : conditions) {
            if (condition.first != NULL) {
                assertEquals(condition.first.intValue(), motionTracker.actionFrom(condition.second));
            } else {
                motionTracker.actionFrom(condition.second);
            }
        }
    }
}
