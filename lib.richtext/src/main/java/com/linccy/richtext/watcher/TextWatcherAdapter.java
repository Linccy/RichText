package com.linccy.richtext.watcher;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextWatcherAdapter implements TextWatcher {
    public TextWatcherAdapter() {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
    }
}