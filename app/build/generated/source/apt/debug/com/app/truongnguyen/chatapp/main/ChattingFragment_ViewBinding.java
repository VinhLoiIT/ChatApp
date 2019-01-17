// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChattingFragment_ViewBinding implements Unbinder {
  private ChattingFragment target;

  @UiThread
  public ChattingFragment_ViewBinding(ChattingFragment target, View source) {
    this.target = target;

    target.textInputMsg = Utils.findRequiredViewAsType(source, R.id.text_input_msg, "field 'textInputMsg'", TextInputLayout.class);
    target.editTextMsg = Utils.findRequiredViewAsType(source, R.id.edit_text_msg, "field 'editTextMsg'", TextInputEditText.class);
    target.btnSendMess = Utils.findRequiredViewAsType(source, R.id.btn_send_mess, "field 'btnSendMess'", ImageView.class);
    target.listView = Utils.findRequiredViewAsType(source, R.id.list_of_message, "field 'listView'", ListView.class);
    target.chatToolbar = Utils.findRequiredViewAsType(source, R.id.chatToolbar, "field 'chatToolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChattingFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.textInputMsg = null;
    target.editTextMsg = null;
    target.btnSendMess = null;
    target.listView = null;
    target.chatToolbar = null;
  }
}
