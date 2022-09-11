package com.wuda.wuxue.ui.base;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigationTo(Fragment fragment, boolean addToBackstack);
}
