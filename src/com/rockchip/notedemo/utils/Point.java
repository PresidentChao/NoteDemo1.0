package com.rockchip.notedemo.utils;

public class Point {
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

    public Point(float x, float y, int penWidth, float press, int eraserEnable, int strokesEnable, int action, long eventTime) {
        this.x = x;
        this.y = y;
        this.penWidth = penWidth;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = eventTime;
    }

    public Point(float x, float y, int penWidth, float press, int eraserEnable, int strokesEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = penWidth;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public Point(float x, float y, float press, int eraserEnable, int strokesEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = strokesEnable;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public Point(float x, float y, float press, int eraserEnable, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = eraserEnable;
        this.strokesEnable = PEN_STROKES_DISABLE;
        this.action = action;
        this.eventTime = System.currentTimeMillis();
    }

    public Point(float x, float y, float press, int action) {
        this.x = x;
        this.y = y;
        this.penWidth = PEN_WIDTH_DEFAULT;
        this.press = press;
        this.eraserEnable = PEN_ERASE_DISABLE;
        this.strokesEnable = PEN_STROKES_DISABLE;
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

    public String toRelativeCoordinates(final Point referencePoint) {
        return (new Point(x - referencePoint.x, y - referencePoint.y,
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
}
