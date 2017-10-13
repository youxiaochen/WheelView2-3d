package you.xiaochen.adapter;

import android.view.LayoutInflater;

import java.util.List;

import you.xiaochen.R;
import you.xiaochen.wheel.WheelView;

/**
 * Created by you on 2017/10/13.
 */

public class TestAdapter extends WheelView.WheelAdapter<UserViewHolder> {

    private final List<UserBean> userBeanList;

    public TestAdapter(List<UserBean> userBeanList) {
        this.userBeanList = userBeanList;
    }

    @Override
    public int getItemCount() {
        return userBeanList.size();
    }

    @Override
    public UserViewHolder onCreateViewHolder(LayoutInflater inflater, int viewType) {
        return new UserViewHolder(inflater.inflate(R.layout.wheelview_item, null, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserBean userBean = userBeanList.get(position);
        holder.iv_head.setImageResource(userBean.drawable);
        holder.tv_name.setText(userBean.name);
    }
}
