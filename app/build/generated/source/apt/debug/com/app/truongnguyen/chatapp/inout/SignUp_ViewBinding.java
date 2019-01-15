// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.inout;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SignUp_ViewBinding implements Unbinder {
  private SignUp target;

  @UiThread
  public SignUp_ViewBinding(SignUp target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SignUp_ViewBinding(SignUp target, View source) {
    this.target = target;

    target.ediName = Utils.findRequiredViewAsType(source, R.id.edi_name, "field 'ediName'", TextInputLayout.class);
    target.ediEmail = Utils.findRequiredViewAsType(source, R.id.edi_email, "field 'ediEmail'", TextInputLayout.class);
    target.ediBirthday = Utils.findRequiredViewAsType(source, R.id.edi_birthday, "field 'ediBirthday'", TextInputLayout.class);
    target.ediPassword = Utils.findRequiredViewAsType(source, R.id.edi_password, "field 'ediPassword'", TextInputLayout.class);
    target.ediRetypePass = Utils.findRequiredViewAsType(source, R.id.edi_retype_password, "field 'ediRetypePass'", TextInputLayout.class);
    target.chbConfirm = Utils.findRequiredViewAsType(source, R.id.chb_terms_privacy, "field 'chbConfirm'", CheckBox.class);
    target.txtConfirm = Utils.findRequiredViewAsType(source, R.id.txt_terms_privacy, "field 'txtConfirm'", TextView.class);
    target.mSignIn = Utils.findRequiredViewAsType(source, R.id.link_to_signin, "field 'mSignIn'", TextView.class);
    target.btnSignUp = Utils.findRequiredViewAsType(source, R.id.btn_signup, "field 'btnSignUp'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SignUp target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ediName = null;
    target.ediEmail = null;
    target.ediBirthday = null;
    target.ediPassword = null;
    target.ediRetypePass = null;
    target.chbConfirm = null;
    target.txtConfirm = null;
    target.mSignIn = null;
    target.btnSignUp = null;
  }
}
