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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hardcodedjoy.util.FileUriUtil;
import com.hardcodedjoy.util.GuiUtil;
import com.hardcodedjoy.util.IntentUtil;
import com.hardcodedjoy.util.SoftKeyboard;
import com.hardcodedjoy.util.ThemeUtil;

public class MainActivity extends Activity {

    static private final int RQ_CODE_OPEN = 1;
    static private final int RQ_CODE_SAVE_AS = 2;
    static private final int RQ_CODE_SETTINGS = 3;

    private Settings settings;

    private TextView tvTitle;
    private LinearLayout llMenuOptions;
    private EditText etText;
    private Uri initialUri;
    private String initialFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
        ThemeUtil.set(this, settings.getTheme());
        restoreState(savedInstanceState);
        initGUI();
        processIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Keys.initialFileName, initialFileName);
    }

    private void restoreState(Bundle savedInstanceState) {
        if(savedInstanceState == null) { return; }
        initialFileName = savedInstanceState.getString(Keys.initialFileName, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGUI() {

        // we use our own title bar in "layout_main"
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tv_title);
        if(initialFileName != null) { tvTitle.setText(initialFileName); }

        etText = findViewById(R.id.et_text);
        etText.setTextSize(settings.getFontSize()); // size in ScaledPixels

        llMenuOptions = findViewById(R.id.ll_menu_options);
        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            if(llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
            } else if(llMenuOptions.getVisibility() == View.GONE) {
                llMenuOptions.setVisibility(View.VISIBLE);
                etText.clearFocus();
                SoftKeyboard.hide(etText);
            }
        });
        llMenuOptions.setVisibility(View.GONE);

        etText.setOnTouchListener((v, event) -> {
            llMenuOptions.setVisibility(View.GONE);
            return false;
        });

        findViewById(R.id.sv_main).setOnTouchListener((v, event) -> {
            View child = ((ScrollView) v).getChildAt(0);
            if(event.getAction() == MotionEvent.ACTION_DOWN && event.getY() > child.getHeight()) {
                // touched on empty space inside ScrollView
                llMenuOptions.setVisibility(View.GONE);
                etText.requestFocus();
                SoftKeyboard.show(etText);
                etText.setSelection(etText.getText().length());
                return true;
            }
            return false;
        });

        GuiUtil.setOnClickListenerToAllButtons(llMenuOptions, view -> {
            llMenuOptions.setVisibility(View.GONE);
            int id = view.getId();
            if(id == R.id.btn_new) { onNew(); }
            if(id == R.id.btn_open) { onOpen(); }
            if(id == R.id.btn_save_as) { onSaveAs(); }
            if(id == R.id.btn_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            if(id == R.id.btn_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, RQ_CODE_SETTINGS);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) { processIntent(intent); }

    private void processIntent(Intent intent) {
        String action = intent.getAction();
        if(action == null) { return; }

        // to not process twice, for example at screen orientation change,
        // we clear the intent action:
        intent.setAction(null);

        switch(action) {
            case Intent.ACTION_MAIN:
                onNew();
                break;
            case Intent.ACTION_VIEW:
            case Intent.ACTION_EDIT:
            case Intent.ACTION_SEND:
                open(intent);
                break;
            default:
                break;
        }
    }

    private void open(Intent intent) {

        initialUri = IntentUtil.getUri(intent);

        // if what was shared to the app is a selected text and not a file, retrieve that text:
        if(initialUri == null) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(text != null) { etText.setText(text); }
            return;
        }

        initialFileName = FileUriUtil.getFileName(this, initialUri);
        if(initialFileName != null) { tvTitle.setText(initialFileName); }

        String fileContent = FileUriUtil.getContentAsString(this, initialUri);
        if(fileContent != null) { etText.setText(fileContent); }
    }

    private void onOpen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent = Intent.createChooser(intent, getString(R.string.open));
        startActivityForResult(intent, RQ_CODE_OPEN);
    }

    private void onNew() {
        initialUri = null;
        initialFileName = getString(R.string.untitled);
        tvTitle.setText(initialFileName);
        etText.setText("");
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
            String content = etText.getText().toString();
            FileUriUtil.setContent(this, uri, content);
            initialFileName = FileUriUtil.getFileName(this, uri);
            tvTitle.setText(initialFileName);
        } else if(requestCode == RQ_CODE_SETTINGS) {
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        if(llMenuOptions.getVisibility() == View.VISIBLE) {
            llMenuOptions.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
        super.finish();
    }
}