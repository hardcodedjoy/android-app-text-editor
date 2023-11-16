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

package com.hardcodedjoy.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUriUtil {

    static public void setContent(Context context, Uri uri, String content) {
        try {
            OutputStream os = context.getContentResolver().openOutputStream(uri, "wt");
            os.write(content.getBytes("UTF-8"));
            os.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    static public String getContentAsString(Context context, Uri uri) {

        // NOTE: intended for small strings
        // this will get the InputStream
        // and read the entire stream to baos (to RAM)

        InputStream is;

        try {
            is = context.getContentResolver().openInputStream(uri);
            if(is == null) { return null; }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] block = new byte[1024];
        int bytesRead;
        try {
            while((bytesRead = is.read(block)) != -1) {
                baos.write(block, 0, bytesRead);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

        String s;
        try {
            s = baos.toString("UTF-8");
            baos.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        return s;
    }

    static public String getFileName(Context context, Uri uri) {
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
