 package com.zhj.example;
import android.webkit.MimeTypeMap;

import java.util.Locale;
/**
 * Created by Fred Zhao on 2016/12/22.
 */

public class MimeTypeHelper {

    public static String getMimeTypeForExtension(String path) {

        String extension = path;
        int lastDot = extension.lastIndexOf('.');
        if (lastDot != -1) {
            extension = extension.substring(lastDot + 1);
        }
        // Convert the URI string to lower case to ensure compatibility with MimeTypeMap (see CB-2185).
        extension = extension.toLowerCase(Locale.getDefault());
        if (extension.equals("3ga")) {
            return "audio/3gpp";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
