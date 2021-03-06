package com.example.whatsapp_clone.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class GenericHelper {

    @TargetApi(Build.VERSION_CODES.P)
    public static Bitmap getDeprecatedBitmap(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         // object that allows convertion to byte array
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);    // compress img
        return baos.toByteArray();                            // convert BAOS to pixels (literally byte array/matrix)
    }
}
