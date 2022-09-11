package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.CampusBuilding;
import com.wuda.wuxue.bean.adapterHelper.RootNode;
import com.wuda.wuxue.bean.adapterHelper.TextItemNode;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.FreeRoomAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FreeRoomFragment extends ToolFragment {

    Map<String, List<CampusBuilding>> campusBuilds;
    Button campus_btn;
    Button building_btn;
    Button date_btn;
    Button search_btn;

    RecyclerView recyclerView;
    FreeRoomAdapter adapter;

    FreeRoomViewModel mViewModel;

    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public FreeRoomFragment() {
        campusBuilds = new LinkedHashMap<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        View header = inflater.inflate(R.layout.header_free_room, recyclerView, false);
        campus_btn = header.findViewById(R.id.freeRoom_campus_btn);
        building_btn = header.findViewById(R.id.freeRoom_building_btn);
        date_btn = header.findViewById(R.id.freeRoom_date_btn);
        search_btn = header.findViewById(R.id.freeRoom_search_btn);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        adapter = new FreeRoomAdapter();
        adapter.addHeaderView(header);
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FreeRoomViewModel.class);
        mViewModel.date = sdf.format(System.currentTimeMillis());
        date_btn.setText(mViewModel.date);

        eventBinding();

        if (mViewModel.getCampusBuilding().getValue() == null) {
            showProgressBar();
            mViewModel.queryCampusBuilding();
        }
    }

    private void eventBinding() {

        mViewModel.getCampusBuilding().observe(getViewLifecycleOwner(), new Observer<List<CampusBuilding>>() {
            @Override
            public void onChanged(List<CampusBuilding> campusBuildings) {
                for (CampusBuilding building: campusBuildings) {
                    if(!campusBuilds.containsKey(building.getCampus())) {
                        campusBuilds.put(building.getCampus(), new ArrayList<>());
                    }
                    campusBuilds.get(building.getCampus()).add(building);
                }
                closeProgressBar();
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<List<String>>>() {
            @Override
            public void onChanged(List<List<String>> lists) {
                adapter.setList(getNodes(lists));
                closeProgressBar();
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return;
                DialogFactory.errorInfoDialog(requireContext(), result).show();
                mViewModel.clearFailResponse();
            }
        });

        // 校区
        campus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu campusMenu = new PopupMenu(requireContext(), campus_btn);
                for (String campusItem: campusBuilds.keySet()) {
                    campusMenu.getMenu().add(campusItem);
                }
                campusMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mViewModel.campus = (String) item.getTitle();
                        campus_btn.setText(mViewModel.campus);
                        return false;
                    }
                });
                campusMenu.show();
            }
        });

        // 选择教学楼
        building_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu buildingMenu = new PopupMenu(requireContext(), building_btn);

                List<CampusBuilding> buildings;
                if (mViewModel.campus != null) {
                    buildings = campusBuilds.get(mViewModel.campus);
                    if (buildings == null)
                        return;
                    for (int i=0; i<buildings.size(); i++) {
                        buildingMenu.getMenu().add(1, Menu.NONE, i, buildings.get(i).getName());
                    }
                } else {
                    // 没有选择校区的时候在教学楼前加上校区名
                    buildings = new ArrayList<>();
                    for (String key: campusBuilds.keySet()) {
                        buildings.addAll(campusBuilds.get(key));
                    }
                    for (int i=0; i<buildings.size(); i++) {
                        buildingMenu.getMenu().add(1, Menu.NONE, i, buildings.get(i).getCampus() + " | " + buildings.get(i).getName());
                    }
                }
                buildingMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        building_btn.setText(item.getTitle());
                        mViewModel.building = buildings.get(item.getOrder()).getId();
                        return false;
                    }
                });
                buildingMenu.show();
            }
        });

        // 选择日期
        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        mViewModel.date = sdf.format(selection);
                        date_btn.setText(mViewModel.date);
                    }
                });

                datePicker.show(getParentFragmentManager(), datePicker.toString());
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 楼层和日期必须
                if (mViewModel.building==null || mViewModel.date==null) {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("请选择教学楼和日期")
                            .setCancelable(false)
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    showProgressBar();
                    mViewModel.requestFreeRooms();
                }
            }
        });
    }

    private List<BaseNode> getNodes(List<List<String>> rooms) {
        List<BaseNode> lessons = new ArrayList<>();

        // 合并连续课时
        List<String> lesson1_2 = intersection(rooms.get(0), rooms.get(1));
        lessons.add(new RootNode(wrapString2Node(lesson1_2), "1-2节"));

        List<String> lesson3_5 = intersection(rooms.get(2), intersection(rooms.get(3), rooms.get(4)));
        lessons.add(new RootNode(wrapString2Node(lesson3_5), "3-5节"));

        List<String> lesson6_8 = intersection(rooms.get(5), intersection(rooms.get(6), rooms.get(7)));
        lessons.add(new RootNode(wrapString2Node(lesson6_8), "6-8节"));

        List<String> lesson11_13 = intersection(rooms.get(10), intersection(rooms.get(11), rooms.get(12)));
        lessons.add(new RootNode(wrapString2Node(lesson11_13), "11-12节"));

        // 单节
        for (int i=0; i< rooms.size(); i++) {
            List<BaseNode> items = new ArrayList<>();
            for (int j=0; j<rooms.get(i).size(); j++) {
                items.add(new TextItemNode(rooms.get(i).get(j)));
            }
            lessons.add(new RootNode(items, "第" + (i+1) + "节"));
        }

        return lessons;
    }

    private List<String> intersection(List<String> l1, List<String> l2) {
        // 取交集
        List<String> rst = new ArrayList<>(l1);
        rst.removeAll(l2);
        rst.addAll(l1);
        return rst;
    }

    private List<BaseNode> wrapString2Node(List<String> texts) {
        List<BaseNode> nodes = new ArrayList<>();
        for (String text: texts) {
            nodes.add(new TextItemNode(text));
        }
        return nodes;
    }
}