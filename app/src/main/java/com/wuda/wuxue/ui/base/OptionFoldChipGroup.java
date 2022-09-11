package com.wuda.wuxue.ui.base;

import android.content.Context;

import com.wuda.wuxue.bean.OptionPair;

public class OptionFoldChipGroup extends FoldChipGroup<OptionPair> {
    public OptionFoldChipGroup(Context context) {
        super(context);
        setFold(true);
    }

    @Override
    public CharSequence convert(OptionPair choice) {
        return choice.getName();
    }

    public void select(OptionPair optionPair) {
        if (optionPair == null) return;
        select(optionPair.getValue());
    }

    public void select(String option) {
        for (int i=0; i<choices.size(); i++) {
            if (choices.get(i).getValue().equals(option)) {
                select(i);
                return;
            }
        }
        select(-1);
    }
}
