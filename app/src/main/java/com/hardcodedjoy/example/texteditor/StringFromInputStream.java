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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StringFromInputStream {

    static public String get(InputStream is) {

        // NOTE: intended for small strings
        // this will read the entire stream to baos (to RAM)

        if(is == null) { return null; }

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
}