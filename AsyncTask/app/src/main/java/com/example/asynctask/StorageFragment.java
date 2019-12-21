package com.example.asynctask;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StorageFragment extends Fragment {
    private static final StorageFragment ourInstance = new StorageFragment();

    public static StorageFragment getInstance() {
        return ourInstance;
    }

    private final Map<String, Object> mStorage = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void put(String key, Object value) {
        mStorage.put(key, value);
    }

    public Object get(String key) {
        return mStorage.get(key);
    }

    public void remove(String key) {
        mStorage.remove(key);
    }
}
