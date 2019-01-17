// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SettingsFragment_ViewBinding implements Unbinder {
  private SettingsFragment target;

  @UiThread
  public SettingsFragment_ViewBinding(SettingsFragment target, View source) {
    this.target = target;

    target.changePassword = Utils.findRequiredViewAsType(source, R.id.change_password, "field 'changePassword'", LinearLayout.class);
    target.loginHistory = Utils.findRequiredViewAsType(source, R.id.login_history, "field 'loginHistory'", LinearLayout.class);
    target.aboutUs = Utils.findRequiredViewAsType(source, R.id.about_us, "field 'aboutUs'", LinearLayout.class);
    target.logout = Utils.findRequiredViewAsType(source, R.id.logout, "field 'logout'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SettingsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.changePassword = null;
    target.loginHistory = null;
    target.aboutUs = null;
    target.logout = null;
  }
}
