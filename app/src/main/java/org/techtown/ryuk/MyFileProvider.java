package org.techtown.ryuk;

import androidx.core.content.FileProvider;

public class MyFileProvider extends FileProvider {
    public MyFileProvider() {
        super(R.xml.provider_paths);
    }
}