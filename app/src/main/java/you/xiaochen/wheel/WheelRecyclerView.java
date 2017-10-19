package you.xiaochen.wheel;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by you on 2017/10/11.
 */

public class WheelRecyclerView extends RecyclerView {
    /**
     * 阴影遮罩颜色渐变
     */
    private static final int CENTER_COLOR = Color.parseColor("#00ffffff");
    private static final int EDGE_COLOR = Color.parseColor("#ffffffff");

    static final float RIGHTANGLE = 90.F;
    /**
     * 垂直布局时的靠左,居中,靠右立体效果
     */
    static final int GRAVITY_LEFT = 1;
    static final int GRAVITY_CENTER = 2;
    static final int GRAVITY_RIGHT = 3;
    /**
     * 此参数影响左右旋转对齐时的效果,系数越大,越明显,自己体会......(0-1之间)
     */
    static final float DEF_SCALE = 0.75F;
    /**
     * 此参数影响偏离中心item时效果(1-3)
     */
    static final int DEF_COFFICIENT = 3;
    /**
     * 显示的item数量
     */
    final int itemCount;
    /**
     * 每个item大小,  垂直布局时为item的高度, 水平布局时为item的宽度
     */
    final int itemSize;
    /**
     * 对齐方式
     */
    final int gravity;
    /**
     * 每个item平均下来后对应的旋转角度
     * 根据中间分割线上下item和中间总数量计算每个item对应的旋转角度
     */
    final float itemDegree;
    /**
     * 滑动轴的半径
     */
    final float wheelRadio;
    /**
     * 布局方向
     */
    final int orientation;
    /**
     * 分割线宽度
     */
    final int dividerSize;
    /**
     * 自身中心点
     */
    float centerX, centerY;
    /**
     * 3D旋转
     */
    final Camera camera;
    final Matrix matrix;
    /**
     * 分割线画笔
     */
    final Paint dividerPaint;
    final LinearLayoutManager layoutManager;
    /**
     * 渐变画笔
     */
    Paint topOrLeftPaint, rightOrBottomPaint;
    LinearGradient topOrLeftGradient, rightOrBottomGradient;

    public WheelRecyclerView(Context context, int gravity, int itemCount, int itemSize, int orientation, int dividerColor, int dividerSize) {
        super(context);
        this.itemCount = itemCount;
        this.itemSize = itemSize;
        this.orientation = orientation;
        this.gravity = gravity;
        this.itemDegree = 180.f / (itemCount * 2 + 1);
        this.wheelRadio = (float) WheelUtils.radianToRadio(itemSize, itemDegree);

        this.camera = new Camera();
        this.matrix = new Matrix();
        this.camera.setLocation(0, 0, -8 * DEF_COFFICIENT);


        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(dividerColor);
        this.dividerSize = dividerSize;

        topOrLeftPaint = new Paint();
        rightOrBottomPaint = new Paint();

        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(orientation);
        this.setLayoutManager(layoutManager);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        centerX = (l + r) * 0.5f;
        centerY = (t + b) * 0.5f;
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        canvas.save();
        if (orientation == LinearLayoutManager.VERTICAL) {
            verticalCanvasForDrawChild(canvas, child, translateX(centerX));
        } else {
            horizontalCanvasForDrawChild(canvas, child);
        }
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return drawChild;
    }

    /**
     * 根据对齐方式,计算出垂直布局时X轴移动的位置
     * @return
     */
    private float translateX(float parentCenterX) {
        switch (gravity) {
            case GRAVITY_LEFT:
                return parentCenterX * (1 + DEF_SCALE);
            case GRAVITY_RIGHT:
                return parentCenterX * (1 - DEF_SCALE);
        }
        return parentCenterX;
    }

    /**
     * 垂直方向旋转canvas
     * @param c
     * @param child
     * @param translateX
     * @return
     */
    void verticalCanvasForDrawChild(Canvas c, View child, float translateX) {
        float itemCenterY = (child.getTop() + child.getBottom()) * 0.5f;
        float scrollOffY = itemCenterY - centerY;
        float rotateDegreeX = rotateLimitRightAngle(scrollOffY * itemDegree / itemSize);//垂直布局时要以X轴为中心旋转
        float rotateSinX = (float) Math.sin(Math.toRadians(rotateDegreeX));
        float rotateOffY = scrollOffY - wheelRadio * rotateSinX;//因旋转导致界面视角的偏移
        //Log.i("you", "drawVerticalItem degree " + rotateDegreeX);

        c.translate(0.0f, -rotateOffY);//因旋转导致界面视角的偏移
        camera.save();

        //旋转时离视角的z轴方向也会变化,先移动Z轴再旋转
        float z = (float) (wheelRadio * (1 - Math.abs(Math.cos(Math.toRadians(rotateDegreeX)))));
        camera.translate(0, 0, z);


        camera.rotateX(-rotateDegreeX);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-translateX, -itemCenterY);
        matrix.postTranslate(translateX, itemCenterY);
        c.concat(matrix);
    }

    /**
     * 水平方向旋转canvas
     * @param c
     * @param child
     * @return
     */
    void horizontalCanvasForDrawChild(Canvas c, View child) {
        float itemCenterX = (child.getLeft() + child.getRight()) * 0.5f;
        float scrollOffX = itemCenterX - centerX;
        float rotateDegreeY = rotateLimitRightAngle(scrollOffX * itemDegree / itemSize);//垂直布局时要以Y轴为中心旋转
        float rotateSinY = (float) Math.sin(Math.toRadians(rotateDegreeY));
        float rotateOffX = scrollOffX - wheelRadio * rotateSinY;//因旋转导致界面视角的偏移
        //Log.i("you", "drawVerticalItem degree " + rotateDegreeY);

        c.translate(-rotateOffX, 0.0f);
        camera.save();

        float z = (float) (wheelRadio * (1 - Math.abs(Math.cos(Math.toRadians(rotateDegreeY)))));
        camera.translate(0, 0, z);

        camera.rotateY(rotateDegreeY);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-itemCenterX, -centerY);
        matrix.postTranslate(itemCenterX, centerY);
        c.concat(matrix);
    }

    /**
     * 旋转的角度绝对值不能大于90度
     * @param degree
     * @return
     */
    float rotateLimitRightAngle(float degree) {
        if (degree >= RIGHTANGLE) return RIGHTANGLE;
        if (degree <= -RIGHTANGLE) return -RIGHTANGLE;
        return degree;
    }

    /**
     * 获取中心点位置
     * @return
     */
    int findCenterItemPosition() {
        if (getAdapter() == null || centerY == 0 || centerX == 0) return -1;
        View centerView = this.findChildViewUnder(centerX, centerY);
        if (centerView != null) {
            int adapterPosition = this.getChildAdapterPosition(centerView) - itemCount;
            if (adapterPosition >= 0){
                return adapterPosition;
            }
        }
        return -1;
    }

    /**
     * 在这里画分割线与遮罩层
     * @param c
     */
    @Override
    protected void dispatchDraw(Canvas c) {
        super.dispatchDraw(c);
        //设置抗锯齿,但是好像无效,有知道如何解决的,请不吝赐教
        c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        if (orientation == LinearLayoutManager.VERTICAL) {
            float dividerOff = (getHeight() - dividerSize) / 2.0f;
            float firstY = getTop() + dividerOff;
            c.drawLine(getLeft(), firstY, getRight(), firstY, dividerPaint);
            float secondY = getBottom() - dividerOff;
            c.drawLine(getLeft(), secondY, getRight(), secondY, dividerPaint);

            //如果需要画渐变阴影,取消注释的代码

        /*
            float rectTop = firstY - wheelRadio;
            float rectBottom = secondY + wheelRadio;
            if (topOrLeftGradient == null) {
                topOrLeftGradient = new LinearGradient(0, firstY, 0, rectTop, CENTER_COLOR, EDGE_COLOR, Shader.TileMode.MIRROR);
                topOrLeftPaint.setShader(topOrLeftGradient);
            }
            if (rightOrBottomGradient == null) {
                rightOrBottomGradient = new LinearGradient(0, secondY, 0, rectBottom, CENTER_COLOR, EDGE_COLOR, Shader.TileMode.MIRROR);
                rightOrBottomPaint.setShader(rightOrBottomGradient);
            }
            c.drawRect(getLeft(), rectTop, getRight(), firstY, topOrLeftPaint);
            c.drawRect(getLeft(), secondY, getRight(), rectBottom, rightOrBottomPaint);*/
        } else {
            float dividerOff = (getWidth() - dividerSize) / 2.0f;
            float firstX = getLeft() + dividerOff;
            c.drawLine(firstX, getTop(), firstX, getBottom(), dividerPaint);
            float secondX = getRight() - dividerOff;
            c.drawLine(secondX, getTop(), secondX, getBottom(), dividerPaint);

            //如果需要画渐变阴影,取消注释的代码
           /* float rectLeft = firstX - wheelRadio;
            float rectRight = secondX + wheelRadio;
            if (topOrLeftGradient == null) {
                topOrLeftGradient = new LinearGradient(firstX, 0, rectLeft, 0, CENTER_COLOR, EDGE_COLOR, Shader.TileMode.MIRROR);
                topOrLeftPaint.setShader(topOrLeftGradient);
            }
            if (rightOrBottomGradient == null) {
                rightOrBottomGradient = new LinearGradient(secondX, 0, rectRight, 0, CENTER_COLOR, EDGE_COLOR, Shader.TileMode.MIRROR);
                rightOrBottomPaint.setShader(rightOrBottomGradient);
            }
            c.drawRect(rectLeft, getTop(), firstX, getBottom(), topOrLeftPaint);
            c.drawRect(secondX, getTop(), rectRight, getBottom(), rightOrBottomPaint);*/
        }
    }

}
