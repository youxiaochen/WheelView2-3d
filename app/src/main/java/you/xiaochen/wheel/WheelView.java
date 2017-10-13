package you.xiaochen.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import you.xiaochen.R;

/**
 * Created by you on 2017/10/11.
 */

public class WheelView extends FrameLayout {
    /**
     * 无效的位置
     */
    public static final int IDLE_POSITION = -1;
    /**
     * 垂直与水平布局两种状态
     */
    public static final int WHEEL_VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int WHEEL_HORIZONTAL = LinearLayoutManager.HORIZONTAL;

    private WheelRecyclerView mRecyclerView;
    /**
     * 分割线颜色
     */
    private int dividerColor = Color.BLACK;
    /**
     * item数量
     */
    private int itemCount = 3;
    /**
     * item大小
     */
    private int itemSize = 90;
    /**
     * 分割线之间距离
     */
    private int dividerSize = 90;
    /**
     * 布局方向
     */
    private int orientation = WHEEL_VERTICAL;

    /**
     * 对齐方式
     */
    private int gravity = WheelRecyclerView.GRAVITY_CENTER;

    private int lastSelectedPosition = IDLE_POSITION;
    private int selectedPosition = IDLE_POSITION;

    public WheelView(Context context) {
        super(context);
        init(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
            itemCount = a.getInt(R.styleable.WheelView_wheelItemCount, itemCount);
            dividerColor = a.getColor(R.styleable.WheelView_dividerColor, dividerColor);
            itemSize = a.getDimensionPixelOffset(R.styleable.WheelView_wheelItemSize, itemSize);
            dividerSize = a.getDimensionPixelOffset(R.styleable.WheelView_wheelDividerSize, dividerSize);
            orientation = a.getInt(R.styleable.WheelView_wheelOrientation, orientation);
            gravity = a.getInt(R.styleable.WheelView_wheelGravity, gravity);
            a.recycle();
        }
        initRecyclerView(context);
    }

    private void initRecyclerView(Context context) {
        mRecyclerView = new WheelRecyclerView(context, gravity, itemCount, itemSize, orientation, dividerColor, dividerSize);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        int totolItemSize = (itemCount * 2 + 1) * itemSize;
        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);//让滑动结束时都能定到中心位置
        this.addView(mRecyclerView, WheelUtils.createLayoutParams(orientation, totolItemSize));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (listener == null || newState != RecyclerView.SCROLL_STATE_IDLE) return;
                int centerItemPosition = mRecyclerView.findCenterItemPosition();
                if (centerItemPosition == IDLE_POSITION) return;
                selectedPosition = centerItemPosition;
                if (selectedPosition != lastSelectedPosition) {
                    listener.onItemSelected(centerItemPosition);
                    lastSelectedPosition = selectedPosition;
                }
            }
        });
    }


    private OnItemSelectedListener listener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * item selected
     */
    public interface OnItemSelectedListener {
        void onItemSelected(int index);
    }

    public void setAdapter(WheelAdapter adapter) {
        if (adapter == null) {
            mRecyclerView.setAdapter(null);
            return;
        }
        SimpleWheelAdapter wheelAdapter = new SimpleWheelAdapter(adapter, orientation, itemSize, itemCount);
        adapter.adapter = wheelAdapter;
        mRecyclerView.setAdapter(wheelAdapter);
    }

    /**
     * 适配器
     */
    public abstract static class WheelAdapter<VH extends ViewHolder> {

        SimpleWheelAdapter adapter;

        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public abstract VH onCreateViewHolder(LayoutInflater inflater, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public void onViewRecycled(VH holder) {
        }

        public void onViewAttachedToWindow(VH holder) {
        }

        public void onViewDetachedFromWindow(VH holder) {
        }

        public final void notifyDataSetChanged() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
