package de.passwordvault.view.utils.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.LinkedList;


/**
 * Class implements a segmented progress bar.
 *
 * @author  Christian-2003
 * @version 3.6.1
 */
public class SegmentedProgressBar extends View {

    /**
     * Class models a segment of the segmented progress bar.
     */
    public static class Segment {

        /**
         * Attribute stores the percentage for the segment.
         */
        private final float percentage;

        /**
         * Attribute stores the background color for the segment.
         */
        private final int color;


        /**
         * Constructor instantiates a new segment.
         *
         * @param percentage    Percentage for the segment.
         * @param color         Color for the segment.
         */
        public Segment(float percentage, int color) {
            if (percentage < 0) {
                this.percentage = percentage * -1;
            }
            else {
                this.percentage = percentage;
            }
            this.color = color;
        }


        /**
         * Method returns the percentage of the segment.
         *
         * @return  Percentage of the segment.
         */
        public float getPercentage() {
            return percentage;
        }

        /**
         * Method returns the color of the segment.
         *
         * @return  Color of the segment.
         */
        public int getColor() {
            return color;
        }

    }


    /**
     * Attribute stores the list of segments of the progress bar.
     */
    private final LinkedList<Segment> segments;

    /**
     * Attribute stores the paint used for drawing.
     */
    private final Paint paint;


    /**
     * Constructor instantiates a new segmented progress bar.
     *
     * @param context   Context for the view.
     */
    public SegmentedProgressBar(Context context) {
        super(context);
        segments = new LinkedList<>();
        paint = new Paint();
    }

    /**
     * Constructor instantiates a new segmented progress bar
     *
     * @param context   Context for the view.
     * @param attrs     Attributes for the view.
     */
    public SegmentedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        segments = new LinkedList<>();
        paint = new Paint();
    }

    /**
     * Constructor instantiates a new segmented progress bar.
     *
     * @param context       Context for the view.
     * @param attrs         Attributes for the view.
     * @param defStyleAttr  Style for the view.
     */
    public SegmentedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        segments = new LinkedList<>();
        paint = new Paint();
    }


    /**
     * Method adds the segment to the progress bar.
     *
     * @param segment   Segment to add to the progress bar.
     */
    public void addSegment(@NonNull Segment segment) {
        segments.add(segment);
    }


    /**
     * Method removes all segments from the progress bar.
     */
    public void clearSegments() {
        segments.clear();
    }


    /**
     * Method draws the segmented progress bar.
     *
     * @param canvas    Canvas onto which to draw.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int segmentWidth = 0;
        int left = 0;
        for (Segment segment : segments) {
            paint.setColor(segment.getColor());
            segmentWidth = (int)(width * segment.getPercentage());
            canvas.drawRect(left, 0, left + segmentWidth, height, paint);
            left += segmentWidth;
        }
    }

}
