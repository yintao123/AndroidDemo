package com.github.mikephil.charting.data;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;

import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Created by Philipp Jahoda on 11/07/15.
 */
public abstract class LineScatterCandleRadarDataSet<T extends Entry> extends BarLineScatterCandleBubbleDataSet<T> implements ILineScatterCandleRadarDataSet<T> {

    protected boolean mDrawVerticalHighlightIndicator = true;
    protected boolean mDrawHorizontalHighlightIndicator = true;

    protected boolean mDrawCircularHighlightIndicator = false;

    protected float[] mCircleHighlightRadii = new float[]{Utils.convertDpToPixel(8f)};
    protected int[] mCircleHighlightColors = new int[]{Color.rgb(255, 187, 115)};

    /** the width of the highlight indicator lines */
    protected float mHighlightLineWidth = 0.5f;

    /** the path effect for dashed highlight-lines */
    protected DashPathEffect mHighlightDashPathEffect = null;


    public LineScatterCandleRadarDataSet(List<T> yVals, String label) {
        super(yVals, label);
        mHighlightLineWidth = Utils.convertDpToPixel(0.5f);
    }

    /**
     * Enables / disables the circular highlight-indicator. If disabled, the indicator is not drawn.
     * default: false
     *
     * @param enabled
     */
    public void setDrawCircularHighlightIndicator(boolean enabled) {
        this.mDrawCircularHighlightIndicator = enabled;
    }

    /**
     * Enables / disables the horizontal highlight-indicator. If disabled, the indicator is not drawn.
     * @param enabled
     */
    public void setDrawHorizontalHighlightIndicator(boolean enabled) {
        this.mDrawHorizontalHighlightIndicator = enabled;
    }

    /**
     * Enables / disables the vertical highlight-indicator. If disabled, the indicator is not drawn.
     * @param enabled
     */
    public void setDrawVerticalHighlightIndicator(boolean enabled) {
        this.mDrawVerticalHighlightIndicator = enabled;
    }

    /**
     * Enables / disables both vertical and horizontal highlight-indicators.
     * @param enabled
     */
    public void setDrawHighlightIndicators(boolean enabled) {
        setDrawVerticalHighlightIndicator(enabled);
        setDrawHorizontalHighlightIndicator(enabled);
        setDrawCircularHighlightIndicator(enabled);
    }

    @Override
    public boolean isVerticalHighlightIndicatorEnabled() {
        return mDrawVerticalHighlightIndicator;
    }

    @Override
    public boolean isHorizontalHighlightIndicatorEnabled() {
        return mDrawHorizontalHighlightIndicator;
    }

    /**
     * Sets the width of the highlight line in dp.
     * @param width
     */
    public void setHighlightLineWidth(float width) {
        mHighlightLineWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getHighlightLineWidth() {
        return mHighlightLineWidth;
    }

    /**
     * Enables the highlight-line to be drawn in dashed mode, e.g. like this "- - - - - -"
     *
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the line-pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedHighlightLine(float lineLength, float spaceLength, float phase) {
        mHighlightDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the highlight-line to be drawn in dashed mode.
     */
    public void disableDashedHighlightLine() {
        mHighlightDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled for highlight lines, false if not.
     * Default: disabled
     *
     * @return
     */
    public boolean isDashedHighlightLineEnabled() {
        return mHighlightDashPathEffect == null ? false : true;
    }

    @Override
    public DashPathEffect getDashPathEffectHighlight() {
        return mHighlightDashPathEffect;
    }

    @Override
    public boolean isCircularHighlightIndicatorEnabled() {
        return mDrawCircularHighlightIndicator;
    }

    @Override
    public float[] getCircleHighlightRadii() {
        return mCircleHighlightRadii;
    }

    @Override
    public int[] getCircleHighlightColors() {
        return mCircleHighlightColors;
    }

    /**
     * Used to set up the circle highlight indicators after doing a sanity check
     *
     * @param circleRadii
     * @param circleColors
     */
    public void setupCircularHighlightIndicator(float[] circleRadii, int[] circleColors) {
        if (circleRadii == null || circleColors == null
                || circleRadii.length == 0 || circleColors.length == 0) {
            Log.e(getClass().getSimpleName(),
                    "You must specify at least one circle radius and at least one circle color.");
            return;
        }
        if (circleRadii.length > 4 || circleColors.length > 4) {
            Log.e(getClass().getSimpleName(), "Can only draw a maximum of 4 circles.");
            return;
        }
        if (circleRadii.length != circleColors.length) {
            Log.e(getClass().getSimpleName(), "The circle colors and circle radii must be equal.");
            return;
        }
        for (int i = 0; i < circleRadii.length; i++) {
            if (circleRadii[i] < 0.5f) {
                Log.e(getClass().getSimpleName(), "The circle radius cannot be less than 0.5.");
                return;
            }
            if (i != circleRadii.length - 1 && circleRadii[i] > circleRadii[i+1]) {
                Log.e(getClass().getSimpleName(), "The circle radii must be in ascending order.");
                return;
            }
        }
        if (!isCircularHighlightIndicatorEnabled()) {
            // if the circular highlight indicator is not enabled, enable it
            setDrawCircularHighlightIndicator(true);
        }

        setCircleHighlightRadii(circleRadii);
        setCircleHighlightColors(circleColors);
    }

    private void setCircleHighlightRadii(float[] circleHighlightRadii) {
        this.mCircleHighlightRadii = circleHighlightRadii;
    }

    private void setCircleHighlightColors(int[] circleHighlightColors) {
        this.mCircleHighlightColors = circleHighlightColors;
    }
}
