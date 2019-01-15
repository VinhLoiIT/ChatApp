// Generated code from Butter Knife. Do not modify!
package com.app.truongnguyen.chatapp.search_fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.app.truongnguyen.chatapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SearchFragment_ViewBinding implements Unbinder {
  private SearchFragment target;

  @UiThread
  public SearchFragment_ViewBinding(SearchFragment target, View source) {
    this.target = target;

    target.input = Utils.findRequiredViewAsType(source, R.id.input_search, "field 'input'", EditText.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.btn_back, "field 'back'", ImageButton.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.list_people_result, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SearchFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.input = null;
    target.back = null;
    target.recyclerView = null;
  }
}
