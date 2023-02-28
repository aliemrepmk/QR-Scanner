package com.aliemrepamuk.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {
    private CodeScanner codeScanner;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        CodeScannerView scannerView = findViewById(R.id.scanner_view); // scannerView
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);  // Scanning continues after detection

        Button exitButton = (Button) findViewById(R.id.btn1);  // exit button

        Button clipBoardButton = (Button) findViewById(R.id.btn2);  // clipboard button

        TextView resultText = (TextView) findViewById(R.id.scanner_text);  // text
        resultText.setText("SCANNING");
        resultText.setTextSize(40);

        // blinking animation for 'SCANNING' text
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(350);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        resultText.startAnimation(anim);  // adding the animation to the text

        ImageView img = (ImageView) findViewById(R.id.rectangle);  // rectangle around the text

        // closing the app if exit button is touched
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        // Checking for CAMERA permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(), "CAMERA PERMISSION NECESSARY", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            codeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText.clearAnimation();  // stopping the blinking animation
                            resultText.setText((result.getText()).toString());  // showing the decoded data
                            int size = (result.getText().length());

                            // changing the text size
                            if (size > 0 && size <= 10){
                                resultText.setTextSize(50);
                            }
                            else if (size > 10 && size <= 15){
                                resultText.setTextSize(40);
                            }
                            else if (size > 15 && size <= 20){
                                resultText.setTextSize(30);
                            }
                            else if (size > 20){
                                resultText.setTextSize(25);
                            }

                            resultText.setTextColor(Color.GREEN);
                            img.setColorFilter(Color.GREEN);

                            clipBoardButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Scanned data", result.getText().toString());
                                    clipboard.setPrimaryClip(clip);  // copying the decoded data to clipboard if button is touched
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause () {
        codeScanner.setFlashEnabled(false);
        codeScanner.releaseResources();
        super.onPause();
    }
}