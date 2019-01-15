// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MyProfileActivity_ViewBinding implements Unbinder {
  private MyProfileActivity target;

  @UiThread
  public MyProfileActivity_ViewBinding(MyProfileActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MyProfileActivity_ViewBinding(MyProfileActivity target, View source) {
    this.target = target;

    target.avatar = Utils.findRequiredViewAsType(source, R.id.avatar, "field 'avatar'", RoundedImageView.class);
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tv_user_name, "field 'tvName'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyProfileActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.avatar = null;
    target.tvName = null;
  }
}
