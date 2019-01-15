// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ViewProfileFragment_ViewBinding implements Unbinder {
  private ViewProfileFragment target;

  @UiThread
  public ViewProfileFragment_ViewBinding(ViewProfileFragment target, View source) {
    this.target = target;

    target.avatar = Utils.findRequiredViewAsType(source, R.id.avatar, "field 'avatar'", RoundedImageView.class);
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tv_user_name, "field 'tvName'", TextView.class);
    target.btnAddFrienf = Utils.findRequiredViewAsType(source, R.id.btn_addfriend, "field 'btnAddFrienf'", Button.class);
    target.btnToChat = Utils.findRequiredViewAsType(source, R.id.btn_to_chat, "field 'btnToChat'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ViewProfileFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.avatar = null;
    target.tvName = null;
    target.btnAddFrienf = null;
    target.btnToChat = null;
  }
}