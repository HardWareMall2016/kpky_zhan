package com.zhan.kykp.widget.wheel;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;
    private int stepLength=1;

    // format
    private String format;

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE ,1);
    }

    public NumericWheelAdapter(int minValue, int maxValue) {
        this(minValue, maxValue, null,1);
    }
    
    /**
     * Constructor
     * 
     * @param minValue
     *            the wheel min value
     * @param maxValue
     *            the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue,int stepLength) {
        this(minValue, maxValue, null,stepLength);
    }

    public NumericWheelAdapter(int minValue, int maxValue, String format) {
    	 this(minValue, maxValue, format,1);
    }
    
    /**
     * Constructor
     * 
     * @param minValue
     *            the wheel min value
     * @param maxValue
     *            the wheel max value
     * @param format
     *            the format string
     */
    public NumericWheelAdapter(int minValue, int maxValue, String format ,int stepLength) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
        this.stepLength=stepLength;
    }

    @Override
    public String getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index*stepLength;
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return (maxValue - minValue)/stepLength + 1;
    }

    @Override
    public int getMaximumLength() {
        int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
        int maxLen = Integer.toString(max).length();
        if (minValue < 0) {
            maxLen++;
        }
        return maxLen;
    }
}
