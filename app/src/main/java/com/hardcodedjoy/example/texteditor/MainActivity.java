/*

MIT License

Copyright © 2023 HARDCODED JOY S.R.L. (https://hardcodedjoy.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package com.hardcodedjoy.example.texteditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    static private final int RQ_CODE_OPEN = 1;
    static private final int RQ_CODE_SAVE_AS = 2;

    private TextView tvTitle;
    private LinearLayout llMenuOptions;
    private EditText etText;
    private Uri initialUri;
    private String initialFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
        processIntent(getIntent());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGUI() {

        // we use our own title bar in "layout_main"
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.layout_main);
        tvTitle = findViewById(R.id.tv_title);
        etText = findViewById(R.id.et_text);

        llMenuOptions = findViewById(R.id.ll_menu_options);
        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            if(llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
            } else if(llMenuOptions.getVisibility() == View.GONE) {
                llMenuOptions.setVisibility(View.VISIBLE);
                etText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);
            }
        });
        llMenuOptions.setVisibility(View.GONE);

        View.OnTouchListener menuOptionsHider = (v, event) -> {
            llMenuOptions.setVisibility(View.GONE);
            return false;
        };
        etText.setOnTouchListener(menuOptionsHider);
        findViewById(R.id.sv_main).setOnTouchListener(menuOptionsHider);

        findViewById(R.id.btn_open).setOnClickListener(view -> {
            llMenuOptions.setVisibility(View.GONE);
            onOpen();
        });
        findViewById(R.id.btn_save_as).setOnClickListener(view -> {
            llMenuOptions.setVisibility(View.GONE);
            onSaveAs();
        });
        findViewById(R.id.btn_about).setOnClickListener(view -> {
            llMenuOptions.setVisibility(View.GONE);
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) { processIntent(intent); }

    private void processIntent(Intent intent) {
        String action = intent.getAction();
        if(action == null) { return; }

        switch(action) {
            case Intent.ACTION_MAIN:
                etText.setText("");
                break;
            case Intent.ACTION_VIEW:
            case Intent.ACTION_EDIT:
            case Intent.ACTION_SEND:
                try {
                    open(intent);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                break;
            default:
                break;
        }
    }

    private void open(Intent intent) {

        Log.d("vblog", "open() called");

        initialUri = UriFromIntent.get(intent);

        // if what was shared to the app is a selected text and not a file, retrieve that text:
        String text = null;
        if(initialUri == null) { text = intent.getStringExtra(Intent.EXTRA_TEXT); }

        // to not call open() again at screen orientation change, we clear the intent action:
        intent.setAction(null);

        if(initialUri == null) {
            if(text != null) { etText.setText(text); }
            return;
        }

        initialFileName = FileNameFromUri.get(this, initialUri);
        if(initialFileName != null) { tvTitle.setText(initialFileName); }

        try {
            InputStream is = getContentResolver().openInputStream(initialUri);
            String fileContent = StringFromInputStream.get(is);
            if(fileContent != null) { etText.setText(fileContent); }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void onOpen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent = Intent.createChooser(intent, getString(R.string.open));
        startActivityForResult(intent, RQ_CODE_OPEN);
    }

    private void onSaveAs() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/*");
        if(initialUri != null) {
            intent.putExtra("android.provider.extra.INITIAL_URI", initialUri);
        }
        if(initialFileName != null) {
            intent.putExtra(Intent.EXTRA_TITLE, initialFileName);
        }
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent = Intent.createChooser(intent, getString(R.string.save_as));
        startActivityForResult(intent, RQ_CODE_SAVE_AS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) { return; }

        if(requestCode == RQ_CODE_OPEN) {
            open(data);
        } else if(requestCode == RQ_CODE_SAVE_AS) {
            Uri uri = data.getData();
            String text = etText.getText().toString();
            try {
                OutputStream os = getContentResolver().openOutputStream(uri, "wt");
                os.write(text.getBytes(StandardCharsets.UTF_8));
                os.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(llMenuOptions.getVisibility() == View.VISIBLE) {
            llMenuOptions.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }
}