package com.rockchip.notedemo.utils;

public class FlashPoint {

    //Eraser Mode enable
    public static final int PEN_ERASE_DISABLE	= 0;
    public static final int PEN_ERASE_ENABLE	= 1;

    //Strokes Mode enable
    public static final int PEN_STROKES_DISABLE	= 0;
    public static final int PEN_STROKES_ENABLE	= 1;

    //Action Code
    public static final int ACTION_DOWN 	= 0;
    public static final int ACTION_UP 		= 1;
    public static final int ACTION_MOVE 	= 2;
    public static final int ACTION_CLEAR 	= 3;
    public static final int ACTION_UNDO 	= 4;
    public static final int ACTION_REDO 	= 5;
    public static final int ACTION_UNKOWN 	= -1;

    private static final int PEN_WIDTH_DEFAULT = 3;

    public float x;
    public float y;
    public int penWidth;
    public float press;
    public int eraserEnable;
    public int strokesEnable;
    public int action;
    public long eventTime;

    public FlashPoint(float x, float y, int penWidth, float press, int eraserEnable, int strokesEnable, int action, long eventTime) {
        this.x = x;
        this.y = y;
        this.penWidth = penWidth;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = eventTime;
    }

    public FlashPoint(float x, float y, int penWidth, float press, int eraserEnable, int strokesEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = penWidth;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public FlashPoint(float x, float y, float press, int eraserEnable, int strokesEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public FlashPoint(float x, float y, float press, int eraserEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = 0;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public FlashPoint(float x, float y, float press, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = PEN_ERASE_DISABLE;
        this.strokesEnable = 0;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public String toAbsoluteCoordinates() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(x);
        stringBuilder.append(",");
        stringBuilder.append(y);
        stringBuilder.append(",");
        stringBuilder.append(press);
        stringBuilder.append(",");
        stringBuilder.append(penWidth);
        stringBuilder.append(",");
        stringBuilder.append(eraserEnable);
        stringBuilder.append(",");
        stringBuilder.append(strokesEnable);
        stringBuilder.append(",");
        stringBuilder.append(action);
        stringBuilder.append(",");
        stringBuilder.append(eventTime);
        return stringBuilder.toString();
    }

    public String toRelativeCoordinates(final FlashPoint referencePoint) {
        return (new FlashPoint(x - referencePoint.x, y - referencePoint.y,
                penWidth - referencePoint.penWidth,
                press - referencePoint.press,
                eraserEnable - referencePoint.eraserEnable,
                strokesEnable - referencePoint.strokesEnable,
                action - referencePoint.action,
                eventTime - referencePoint.eventTime)).toString();
    }

    @Override
    public String toString() {
        return toAbsoluteCoordinates();
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlashPoint flashPoint = (FlashPoint) o;

        if (!x.equals(flashPoint.x)) return false;
        if (!y.equals(flashPoint.y)) return false;
        if (!press.equals(flashPoint.press)) return false;
        return action.equals(flashPoint.action);

    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }*/
}
