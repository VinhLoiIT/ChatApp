// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.inout;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SignIn_ViewBinding implements Unbinder {
  private SignIn target;

  @UiThread
  public SignIn_ViewBinding(SignIn target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SignIn_ViewBinding(SignIn target, View source) {
    this.target = target;

    target.btnLogin = Utils.findRequiredViewAsType(source, R.id.btn_login, "field 'btnLogin'", Button.class);
    target.mSignUp = Utils.findRequiredViewAsType(source, R.id.link_to_signup, "field 'mSignUp'", TextView.class);
    target.ediEmail = Utils.findRequiredViewAsType(source, R.id.edi_email, "field 'ediEmail'", TextInputLayout.class);
    target.txtEmail = Utils.findRequiredViewAsType(source, R.id.input_email, "field 'txtEmail'", TextInputEditText.class);
    target.ediPassword = Utils.findRequiredViewAsType(source, R.id.edi_password, "field 'ediPassword'", TextInputLayout.class);
    target.txtPassword = Utils.findRequiredViewAsType(source, R.id.input_password, "field 'txtPassword'", TextInputEditText.class);
    target.chbRemember = Utils.findRequiredViewAsType(source, R.id.chb_remember, "field 'chbRemember'", CheckBox.class);
    target.mForgot = Utils.findRequiredView(source, R.id.forgot_password, "field 'mForgot'");
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.signin_progressBar, "field 'progressBar'", ProgressBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SignIn target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnLogin = null;
    target.mSignUp = null;
    target.ediEmail = null;
    target.txtEmail = null;
    target.ediPassword = null;
    target.txtPassword = null;
    target.chbRemember = null;
    target.mForgot = null;
    target.progressBar = null;
  }
}
