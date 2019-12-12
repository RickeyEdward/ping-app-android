package com.Dialog.vpdntestapp;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.example.vpdntestapp.R;

/**
 * @author panqi
 */
public class IpDialog extends Dialog implements View.OnClickListener {

  Activity context;

  private Button btnAdd;
  public EditText ipText;

  private IpDialogListener ipDialogListener;

  public IpDialog(Activity context, int themeResId,
    IpDialogListener ipDialogListener) {
    super(context, themeResId);
    this.context = context;
    this.ipDialogListener = ipDialogListener;

  }

  public IpDialog(Activity context) {
    super(context);
    this.context = context;
  }

  public interface IpDialogListener {

    public void onClick(View view);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.add_address_dialog);

    ipText = findViewById(R.id.ip);

    Window dialogWindow = this.getWindow();
    WindowManager windowManager = context.getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
    layoutParams.width = (int) (display.getWidth() * 0.7);
    layoutParams.height = (int) (display.getHeight() * 0.3);
    dialogWindow.setAttributes(layoutParams);

    btnAdd = findViewById(R.id.addIp);

    btnAdd.setOnClickListener(this);
    this.setCancelable(true);

  }

  @Override
  public void onClick(View view) {
    ipDialogListener.onClick(view);
  }
}
