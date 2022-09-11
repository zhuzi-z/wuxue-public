package com.wuda.wuxue.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.wuda.wuxue.R;

import java.util.ArrayList;
import java.util.List;

public abstract class FoldChipGroup<T> extends LinearLayout {

    List<T> choices = new ArrayList<>();

    MaterialTextView label_tv;
    ChipGroup chipGroup;

    Drawable foldDrawable;
    Drawable unfoldDrawable;

    boolean isFold;

    OnSelectionChangedListener<T> selectionChangedListener;

    public interface OnSelectionChangedListener<T> {
        void onSelectionChanged(View view, T selection, int index);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public FoldChipGroup(Context context) {
        super(context);

        setOrientation(VERTICAL);

        foldDrawable = context.getDrawable(R.drawable.ic_arrow_down);
        foldDrawable.setBounds(0, 0, foldDrawable.getMinimumWidth(), foldDrawable.getMinimumHeight());

        unfoldDrawable = context.getDrawable(R.drawable.ic_arrow_up);
        unfoldDrawable.setBounds(0, 0, foldDrawable.getMinimumWidth(), foldDrawable.getMinimumHeight());

        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        label_tv = new MaterialTextView(context);
        label_tv.setCompoundDrawables(unfoldDrawable, null, null, null);
        label_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        label_tv.setGravity(Gravity.CENTER_VERTICAL);
        label_tv.setCompoundDrawablePadding(16);
        label_tv.setLayoutParams(params);
        label_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFold = !isFold;
                setFold(isFold);
            }
        });
        addView(label_tv);

        chipGroup = new ChipGroup(context);
        chipGroup.setSingleSelection(true);
        chipGroup.setSelectionRequired(true);
        chipGroup.setLayoutParams(params);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (selectionChangedListener != null) {
                    int id = checkedIds.get(0);
                    selectionChangedListener.onSelectionChanged(FoldChipGroup.this, choices.get(id), id);
                }
            }
        });

        addView(chipGroup);
    }

    public void setLabel(String label) {
        label_tv.setText(label);
    }

    public void addChipItem(T choice) {
        choices.add(choice);
        CharSequence text = convert(choice);

        Chip chip = new Chip(getContext());
        ChipDrawable drawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Choice);
        chip.setChipDrawable(drawable);
        chip.setText(text);
        chip.setId(choices.size()-1);
        chipGroup.addView(chip);
    }

    public void setItems(List<T> items) {
        choices.clear();
        chipGroup.removeAllViews();
        if (items == null) return;
        for (T item: items) {
            addChipItem(item);
        }
    }

    public void select(int index) {
        if (index == -1) return;
        chipGroup.check(index);
        selectionChangedListener.onSelectionChanged(this, choices.get(index), index);
    }

    public boolean isFold() {
        return isFold;
    }

    public void setFold(boolean fold) {
        isFold = fold;
        if (isFold) {
            chipGroup.setVisibility(GONE);
        } else {
            chipGroup.setVisibility(VISIBLE);
        }
        label_tv.setCompoundDrawables(isFold? foldDrawable: unfoldDrawable, null, null, null);
    }

    public abstract CharSequence convert(T choice);

    public void setSelectionChangedListener(OnSelectionChangedListener<T> selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }
}
