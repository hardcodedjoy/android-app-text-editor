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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FileNameFromUri {

    static public String get(Context context, Uri uri) {
        String result = getFileNameFromContentURI(context, uri);
        if(result == null) { result = getFileNameFromFileURI(uri); }
        return result;
    }

    static private String getFileNameFromContentURI(Context context, Uri uri) {
        if(!uri.getScheme().equals("content")) { return null; }
        Cursor cursor = context.getContentResolver().query(uri,
                null, null, null, null);
        if(cursor == null) { return null; }
        if(!cursor.moveToFirst()) { cursor.close(); return null; }
        int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if(columnIndex == -1) { cursor.close(); return null; }
        String result = cursor.getString(columnIndex);
        cursor.close();
        return result;
    }

    static private String getFileNameFromFileURI(Uri uri) {
        String result = uri.getPath();
        if(result == null) { return null; }
        if(result.contains("/")) {
            result = result.substring(result.lastIndexOf("/") + 1);
        }
        return result;
    }
}