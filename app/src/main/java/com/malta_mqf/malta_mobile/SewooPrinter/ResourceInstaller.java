package com.malta_mqf.malta_mobile.SewooPrinter;


import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceInstaller
{
    private final static String TAG = "ResourceInstaller";

    public void copyAssets(AssetManager assetManager, String assetPath)
    {
        String[] files = null;
        String output_path = "/sdcard/"+assetPath+"/test";
        File sdPath = null;

        try
        {
            files = assetManager.list(assetPath);
            sdPath = new File(output_path);

            if(!sdPath.isDirectory())
            {
                sdPath.mkdirs();
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(),e);
        }
        for (int i = 0; i < files.length; i++)
        {
            InputStream in = null;
            OutputStream out = null;
            try
            {
                in = assetManager.open(assetPath+"/"+files[i]);
                out = new FileOutputStream(sdPath.getPath()+"/"+ files[i]);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage(),e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}
