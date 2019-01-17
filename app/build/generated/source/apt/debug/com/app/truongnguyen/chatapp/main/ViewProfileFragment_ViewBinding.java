// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
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

    target.avatarImageView = Utils.findRequiredViewAsType(source, R.id.user_profile_photo, "field 'avatarImageView'", RoundedImageView.class);
    target.tvName = Utils.findRequiredViewAsType(source, R.id.user_profile_name, "field 'tvName'", TextView.class);
    target.textAddress = Utils.findRequiredViewAsType(source, R.id.address, "field 'textAddress'", TextView.class);
    target.tvtEmail = Utils.findRequiredViewAsType(source, R.id.email, "field 'tvtEmail'", TextView.class);
    target.tvtphoneNumber = Utils.findRequiredViewAsType(source, R.id.phone_number, "field 'tvtphoneNumber'", TextView.class);
    target.gender = Utils.findRequiredViewAsType(source, R.id.edittext_sex, "field 'gender'", TextView.class);
    target.btnAddFrienf = Utils.findRequiredViewAsType(source, R.id.btn_addfriend, "field 'btnAddFrienf'", ImageView.class);
    target.btnToChat = Utils.findRequiredViewAsType(source, R.id.btn_to_chat, "field 'btnToChat'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ViewProfileFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.avatarImageView = null;
    target.tvName = null;
    target.textAddress = null;
    target.tvtEmail = null;
    target.tvtphoneNumber = null;
    target.gender = null;
    target.btnAddFrienf = null;
    target.btnToChat = null;
  }
}
