package you.xiaochen.adapter;

import java.io.Serializable;

/**
 * Created by you on 2017/10/13.
 */

public class UserBean implements Serializable {

    public int drawable;

    public String name;

    public UserBean() {}

    public UserBean(int drawable, String name) {
        this.drawable = drawable;
        this.name = name;
    }

}
