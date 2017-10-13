package you.xiaochen.wheel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by you on 2017/10/11.
 */

class SimpleWheelAdapter extends RecyclerView.Adapter {
    /**
     * 空白状态
     */
    public static final int EMPTY_TYPE = 0x00000222;

    final WheelView.WheelAdapter adapter;

    /**
     * recyclerview 布局方向
     */
    final int orientation;
    /**
     * 每个item大小
     */
    final int itemSize;
    /**
     * wheelview 滑动时上或下的空白数量
     */
    final int itemCount;
    /**
     * wheelview 滑动时上或下空白总数量
     */
    private final int totalItemCount;

    private LayoutInflater inflater;

    SimpleWheelAdapter(WheelView.WheelAdapter adapter, int orientation, int itemSize, int itemCount) {
        this.adapter = adapter;
        this.orientation = orientation;
        this.itemSize = itemSize;
        this.itemCount = itemCount;
        this.totalItemCount = itemCount * 2;
    }

    @Override
    public int getItemCount() {
        return totalItemCount + adapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < itemCount || position >= adapter.getItemCount() + itemCount) {
            return EMPTY_TYPE;
        }
        return adapter.getItemViewType(actualPosition(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ViewGroup.LayoutParams params = WheelUtils.createLayoutParams(orientation, itemSize);
        if (viewType == EMPTY_TYPE) {
            View view = new View(parent.getContext());
            view.setLayoutParams(params);
            return new EmptyViewHolder(view);
        }
        RecyclerView.ViewHolder vh = adapter.onCreateViewHolder(inflater, viewType);
        vh.itemView.setLayoutParams(params);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == EMPTY_TYPE) return;
        adapter.onBindViewHolder(holder, actualPosition(position));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == EMPTY_TYPE) return;
        adapter.onViewRecycled(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == EMPTY_TYPE) return;
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == EMPTY_TYPE) return;
        adapter.onViewDetachedFromWindow(holder);
    }

    /**
     * 实际位置
     * @param position
     * @return
     */
    private int actualPosition(int position) {
        return position - itemCount;
    }

    /**
     * 空白ViewHolder
     */
    class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
