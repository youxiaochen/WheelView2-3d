package you.xiaochen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import you.xiaochen.R;

/**
 * Created by you on 2017/10/13.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    public final ImageView iv_head;

    public final TextView tv_name;

    public UserViewHolder(View itemView) {
        super(itemView);
        iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
        tv_name = (TextView) itemView.findViewById(R.id.tv_name);
    }
}
