// Generated by view binder compiler. Do not edit!
package com.mwangi.real.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.mwangi.real.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentUploadDisplayBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final EditText describeUpload;

  @NonNull
  public final ImageView imageFind;

  @NonNull
  public final ImageView imagePush;

  @NonNull
  public final ImageView imageViewDisplay;

  private FragmentUploadDisplayBinding(@NonNull LinearLayout rootView,
      @NonNull EditText describeUpload, @NonNull ImageView imageFind, @NonNull ImageView imagePush,
      @NonNull ImageView imageViewDisplay) {
    this.rootView = rootView;
    this.describeUpload = describeUpload;
    this.imageFind = imageFind;
    this.imagePush = imagePush;
    this.imageViewDisplay = imageViewDisplay;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentUploadDisplayBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentUploadDisplayBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_upload_display, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentUploadDisplayBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.describe_upload;
      EditText describeUpload = ViewBindings.findChildViewById(rootView, id);
      if (describeUpload == null) {
        break missingId;
      }

      id = R.id.imageFind;
      ImageView imageFind = ViewBindings.findChildViewById(rootView, id);
      if (imageFind == null) {
        break missingId;
      }

      id = R.id.imagePush;
      ImageView imagePush = ViewBindings.findChildViewById(rootView, id);
      if (imagePush == null) {
        break missingId;
      }

      id = R.id.imageViewDisplay;
      ImageView imageViewDisplay = ViewBindings.findChildViewById(rootView, id);
      if (imageViewDisplay == null) {
        break missingId;
      }

      return new FragmentUploadDisplayBinding((LinearLayout) rootView, describeUpload, imageFind,
          imagePush, imageViewDisplay);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
