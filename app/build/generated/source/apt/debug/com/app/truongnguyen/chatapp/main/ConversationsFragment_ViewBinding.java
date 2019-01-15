// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ConversationsFragment_ViewBinding implements Unbinder {
  private ConversationsFragment target;

  @UiThread
  public ConversationsFragment_ViewBinding(ConversationsFragment target, View source) {
    this.target = target;

    target.avatar = Utils.findRequiredViewAsType(source, R.id.avatar, "field 'avatar'", RoundedImageView.class);
    target.searchBar = Utils.findRequiredViewAsType(source, R.id.search_bar, "field 'searchBar'", RelativeLayout.class);
    target.swipeLayout = Utils.findRequiredViewAsType(source, R.id.swipe_layout, "field 'swipeLayout'", SwipeRefreshLayout.class);
    target.mErrorTextView = Utils.findRequiredViewAsType(source, R.id.messError, "field 'mErrorTextView'", TextView.class);
    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.list_of_conversation, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ConversationsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.avatar = null;
    target.searchBar = null;
    target.swipeLayout = null;
    target.mErrorTextView = null;
    target.mRecyclerView = null;
  }
}