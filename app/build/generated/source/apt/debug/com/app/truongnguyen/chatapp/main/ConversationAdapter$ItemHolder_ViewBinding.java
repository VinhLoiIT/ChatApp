// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ConversationAdapter$ItemHolder_ViewBinding implements Unbinder {
  private ConversationAdapter.ItemHolder target;

  @UiThread
  public ConversationAdapter$ItemHolder_ViewBinding(ConversationAdapter.ItemHolder target,
      View source) {
    this.target = target;

    target.cvsName = Utils.findRequiredViewAsType(source, R.id.conversation_name, "field 'cvsName'", TextView.class);
    target.messContent = Utils.findRequiredViewAsType(source, R.id.last_message_content, "field 'messContent'", TextView.class);
    target.messTime = Utils.findRequiredViewAsType(source, R.id.last_message_time, "field 'messTime'", TextView.class);
    target.cvsIcon = Utils.findRequiredViewAsType(source, R.id.cvs_avatar, "field 'cvsIcon'", RoundedImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ConversationAdapter.ItemHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cvsName = null;
    target.messContent = null;
    target.messTime = null;
    target.cvsIcon = null;
  }
}
