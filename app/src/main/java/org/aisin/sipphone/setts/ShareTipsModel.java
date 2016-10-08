package org.aisin.sipphone.setts;

import java.io.Serializable;

/**
 * Created by 76594 on 2016/6/13.
 */
public class ShareTipsModel implements Serializable {


    /**
     * title : 分享说明
     * content : 每成功分享一个新用户成功充值相应的额度之后将获得同城商城相应的分享推广补助，具体分享补助如下：
     分享注册成功充值3000补助400积分（解冻400积分）提现或购物800积分;
     分享注册成功充值5000补助1000积分（解冻1000积分）提现或购物2000积;
     分享注册成功充值10000补助1800积分（解冻1800积分）提现或购物3600积分;
     分享注册成功充值20000补助3000积分（解冻3000积分）提现或购物6000积分;
     */

    private String title;
    private String content;
    private int invite_success_num;
    private String update;
    private String invite_charge_success_num;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public int getInvite_success_num() {
        return invite_success_num;
    }

    public void setInvite_success_num(int invite_success_num) {
        this.invite_success_num = invite_success_num;
    }

    public String getInvite_charge_success_num() {
        return invite_charge_success_num;
    }

    public void setInvite_charge_success_num(String invite_charge_success_num) {
        this.invite_charge_success_num = invite_charge_success_num;
    }
}
