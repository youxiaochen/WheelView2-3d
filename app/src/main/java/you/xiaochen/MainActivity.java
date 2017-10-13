package you.xiaochen;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import you.xiaochen.adapter.TestAdapter;
import you.xiaochen.adapter.TestAdapter2;
import you.xiaochen.adapter.UserBean;
import you.xiaochen.wheel.WheelView;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private WheelView wv1, wv2, wv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        context = this;

    }

    /**
     * 如果需要刷新时可以使用WheelAdapter.notifyDataSetChanged()
     */
    private void initView() {
        wv1 = (WheelView) findViewById(R.id.wv1);
        final List<UserBean> userBeanList1 = TestDatas.createTestDatas();
        wv1.setAdapter(new TestAdapter(userBeanList1));
        wv1.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Toast.makeText(context, userBeanList1.get(index).name, Toast.LENGTH_SHORT).show();
            }
        });

        wv2 = (WheelView) findViewById(R.id.wv2);
        final List<UserBean> userBeanList2 = TestDatas.createTestDatas();
        wv2.setAdapter(new TestAdapter(userBeanList2));
        wv2.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Toast.makeText(context, userBeanList2.get(index).name, Toast.LENGTH_SHORT).show();
            }
        });

        wv3 = (WheelView) findViewById(R.id.wv3);
        final List<UserBean> userBeanList3 = TestDatas.createTestDatas();
        wv3.setAdapter(new TestAdapter2(userBeanList3));
        wv3.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Toast.makeText(context, userBeanList3.get(index).name, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
