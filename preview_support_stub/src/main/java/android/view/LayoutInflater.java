package android.view;

import android.content.Context;
import android.util.AttributeSet;

public class LayoutInflater {

  public Factory2 getFactory2(){
    throw new RuntimeException("Stub!");
  }

  public void setFactory2(Factory2 factory) {
    throw new RuntimeException("Stub!");
  }

  public View createView(String name, String prefix, AttributeSet attrs) throws ClassNotFoundException {
    throw new RuntimeException("Stub!");
  }

  public Context getContext(){
    throw new RuntimeException("Stub!");
  }

  public interface Factory2 {
    View onCreateView(View parent, String name, Context context, AttributeSet attrs);

    View onCreateView(String name, Context context, AttributeSet attrs);
  }


}
