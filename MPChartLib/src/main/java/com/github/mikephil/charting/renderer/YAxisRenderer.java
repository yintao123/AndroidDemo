package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class YAxisRenderer extends AxisRenderer {

    protected YAxis mYAxis;

    protected Paint mZeroLinePaint;

    public YAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, trans, yAxis);

        this.mYAxis = yAxis;

        if(mViewPortHandler != null) {

            mAxisLabelPaint.setColor(Color.BLACK);
            mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

            mZeroLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mZeroLinePaint.setColor(Color.GRAY);
            mZeroLinePaint.setStrokeWidth(1f);
            mZeroLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        float[] positions = getTransformedPositions();

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        float xoffset = mYAxis.getXOffset();
        float yoffset = Utils.calcTextHeight(mAxisLabelPaint, "A") / 2.5f + mYAxis.getYOffset();

        AxisDependency dependency = mYAxis.getAxisDependency();
        YAxisLabelPosition labelPosition = mYAxis.getLabelPosition();

        float xPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.offsetLeft() - xoffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.offsetLeft() + xoffset;
            }

        } else {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.contentRight() + xoffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.contentRight() - xoffset;
            }
        }

        drawYLabels(c, xPos, positions, yoffset);
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
            return;

        mAxisLinePaint.setColor(mYAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mYAxis.getAxisLineWidth());

        if (mYAxis.getAxisDependency() == AxisDependency.LEFT) {
            c.drawLine(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        } else {
            c.drawLine(mViewPortHandler.contentRight(), mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the y-labels on the specified x-position
     *
     * @param fixedPosition
     * @param positions
     */
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        // draw
        for (int i = from; i < to; i++) {

            String text = mYAxis.getFormattedLabel(i);
            float labelHeight = Utils.calcTextHeight(mAxisLabelPaint, text);
            float y = positions[i * 2 + 1] + offset;
            if (i == to - 1) {
                if (y - labelHeight - 5 < mViewPortHandler.contentTop())
                {
                    y = mViewPortHandler.contentTop() + labelHeight + 5;
                }
            }
            if (i == from) {
                if (y >  mViewPortHandler.contentBottom() )
                {
                    y = mViewPortHandler.contentBottom() - 3;
                }
            }
            c.drawText(text, fixedPosition, y, mAxisLabelPaint);
        }
    }

    protected Path mRenderGridLinesPath = new Path();
    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        if (mYAxis.isDrawGridLinesEnabled()) {

            int clipRestoreCount = c.save();
            c.clipRect(getGridClippingRect());

            float[] positions = getTransformedPositions();

            mGridPaint.setColor(mYAxis.getGridColor());
            mGridPaint.setStrokeWidth(mYAxis.getGridLineWidth());
            mGridPaint.setPathEffect(mYAxis.getGridDashPathEffect());

            Path gridLinePath = mRenderGridLinesPath;
            gridLinePath.reset();

            // draw the grid
            for (int i = 0; i < positions.length; i += 2) {

                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(linePath(gridLinePath, i, positions), mGridPaint);
                gridLinePath.reset();
            }

            c.restoreToCount(clipRestoreCount);
        }

        if (mYAxis.isDrawZeroLineEnabled()) {
            drawZeroLine(c);
        }
    }

    protected RectF mGridClippingRect = new RectF();

    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(0.f, -mAxis.getGridLineWidth());
        return mGridClippingRect;
    }

    /**
     * Calculates the path for a grid line.
     *
     * @param p
     * @param i
     * @param positions
     * @return
     */
    protected Path linePath(Path p, int i, float[] positions) {

        p.moveTo(mViewPortHandler.offsetLeft(), positions[i + 1]);
        p.lineTo(mViewPortHandler.contentRight(), positions[i + 1]);

        return p;
    }

    protected float[] mGetTransformedPositionsBuffer = new float[2];
    /**
     * Transforms the values contained in the axis entries to screen pixels and returns them in form of a float array
     * of x- and y-coordinates.
     *
     * @return
     */
    protected float[] getTransformedPositions() {

        if(mGetTransformedPositionsBuffer.length != mYAxis.mEntryCount * 2){
            mGetTransformedPositionsBuffer = new float[mYAxis.mEntryCount * 2];
        }
        float[] positions = mGetTransformedPositionsBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed for y-labels
            positions[i + 1] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);
        return positions;
    }

    protected Path mDrawZeroLinePath = new Path();
    protected RectF mZeroLineClippingRect = new RectF();

    /**
     * Draws the zero line.
     */
    protected void drawZeroLine(Canvas c) {

        int clipRestoreCount = c.save();
        mZeroLineClippingRect.set(mViewPortHandler.getContentRect());
        mZeroLineClippingRect.inset(0.f, -mYAxis.getZeroLineWidth());
        c.clipRect(mZeroLineClippingRect);

        // draw zero line
        MPPointD pos = mTrans.getPixelForValues(0f, 0f);

        mZeroLinePaint.setColor(mYAxis.getZeroLineColor());
        mZeroLinePaint.setStrokeWidth(mYAxis.getZeroLineWidth());

        Path zeroLinePath = mDrawZeroLinePath;
        zeroLinePath.reset();

        zeroLinePath.moveTo(mViewPortHandler.contentLeft(), (float) pos.y);
        zeroLinePath.lineTo(mViewPortHandler.contentRight(), (float) pos.y);

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(zeroLinePath, mZeroLinePaint);

        c.restoreToCount(clipRestoreCount);
    }

    protected Path mRenderLimitLines = new Path();
    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();
    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;
        Path limitLinePath = mRenderLimitLines;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {
            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            float limitValue = l.getLimit();
            if ( limitValue <= mYAxis.getAxisMinimum() || limitValue >= mYAxis.getAxisMaximum()) {
                continue;
            }

            int clipRestoreCount = c.save();
            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());

            pts[1] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();
            c.restoreToCount(clipRestoreCount);

        }
        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            float limitValue = l.getLimit();
            if ( limitValue <= mYAxis.getAxisMinimum() || limitValue >= mYAxis.getAxisMaximum()) {
                continue;
            }

            int clipRestoreCount = c.save();

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label != null && !label.equals("")) {

                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float labelLineWidth = Utils.calcTextWidth(mLimitLinePaint, label);
                float xOffset = l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();
                float x = 0;
                float y = 0;
                float paddingX = l.getBackgroundPaddingX();
                float paddingY = l.getBackgroundPaddingX();
                float left, top, right, bottom;
                xOffset += paddingX;
                yOffset += paddingY;
                if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {
                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    x = mViewPortHandler.contentRight() - xOffset;
                    y = pts[1] - yOffset + labelLineHeight;
                    left = x - labelLineWidth;
                    right = x;
                } else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    x = mViewPortHandler.contentRight() - xOffset;
                    y = pts[1] + yOffset;
                    left = x - labelLineWidth;
                    right = x;
                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    x = mViewPortHandler.contentLeft() + xOffset;
                    y = pts[1] - yOffset + labelLineHeight;
                    left = x;
                    right = x + labelLineWidth;
                } else if (position == LimitLine.LimitLabelPosition.LEFT_OUTER)
                {
                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    x = mViewPortHandler.contentLeft() - xOffset;
                    y = pts[1] + labelLineHeight/2;
                    left = x - labelLineWidth;
                    right = x;

                } else if (position == LimitLine.LimitLabelPosition.RIGHT_OUTER)
                {
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    x = mViewPortHandler.contentRight() + xOffset;
                    y = pts[1] + labelLineHeight/2;
                    left = x;
                    right = x + labelLineWidth;
                }
                 else if (position == LimitLine.LimitLabelPosition.LEFT_INNER)
                {
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    x = mViewPortHandler.contentLeft() + xOffset;
                    y = pts[1] + labelLineHeight/2;
                    left = x;
                    right = x + labelLineWidth;

                } else if (position == LimitLine.LimitLabelPosition.RIGHT_INNER)
                {
                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    x = mViewPortHandler.contentRight() - xOffset;
                    y = pts[1] + labelLineHeight/2;
                    left = x - labelLineWidth;
                    right = x;
                }
                else {
                    // LEFT_BOTTOM
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    x = mViewPortHandler.offsetLeft() + xOffset;
                    y = pts[1] + yOffset;
                    left = x;
                    right = x + labelLineWidth;
                }
                top = y - labelLineHeight;
                bottom = y;
                Drawable drawable = l.getLabelBackground();
                if (drawable != null)
                {
                    drawable.setBounds((int)(left - paddingX),
                            (int)(top - paddingY),
                            (int)(right + paddingX),
                            (int)(bottom + paddingY));
                    drawable.draw(c);
                }
                c.drawText(label, x, y, mLimitLinePaint);
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
