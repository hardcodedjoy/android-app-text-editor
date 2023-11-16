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
import android.content.SharedPreferences;

public class Settings {

    static private final float FONT_SIZE_DEFAULT = 20.0f;
    static private final String THEME_DEFAULT = Keys.system;

    private float fontSize;
    private String theme;

    private final SharedPreferences sp;

    public Settings(SharedPreferences sp) {
        this.sp = sp;
        setFontSize(sp.getFloat(Keys.fontSize, FONT_SIZE_DEFAULT));
        setTheme(sp.getString(Keys.theme, THEME_DEFAULT));
    }

    public void setFontSize(float fontSize) { this.fontSize = fontSize; }
    public void setFontSize(String s) {
        try {
            setFontSize(Float.parseFloat(s));
        } catch (Exception e) {
            setFontSize(FONT_SIZE_DEFAULT);
        }
    }
    public float getFontSize() { return fontSize; }

    public void setTheme(String theme) { this.theme = theme; }
    public String getTheme() { return theme; }

    @SuppressLint("ApplySharedPref")
    void save() {
        sp.edit()
                .putFloat(Keys.fontSize, getFontSize())
                .putString(Keys.theme, getTheme())
                .commit();
    }
}