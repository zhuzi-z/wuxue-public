package com.wuda.wuxue.bean;

import com.wuda.wuxue.ui.toolkit.YellowPageDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class Contact implements Tool {
    // 黄页
    String name;
    String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getToolName() {
        return "电话详情";
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return YellowPageDetailFragment.class;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getUrl() {
        return null;
    }

    public static List<Contact> getAllContacts() {

        List<Contact> contactList = new ArrayList<>();

        contactList.add(new Contact("校园110", "	68777110"));
        contactList.add(new Contact("火警电话", "68766119"));
        contactList.add(new Contact("校园医院急诊", "68766894"));
        contactList.add(new Contact("中南医院急诊", "67813167"));
        contactList.add(new Contact("人民医院急诊", "88041911"));
        contactList.add(new Contact("校园网运行部", "68773808"));

        contactList.add(new Contact("网上报修平台(1)", "68770677"));
        contactList.add(new Contact("网上报修平台(2)", "68752252"));
        contactList.add(new Contact("水电管理中心(运行)", "68778210"));
        contactList.add(new Contact("水电管理中心(收费)", "68773203"));
        contactList.add(new Contact("电话宽带报修台", "68755112"));
        contactList.add(new Contact("查号咨询台", "	68755114"));

        contactList.add(new Contact("校巴班车服务队(1)", "68752144"));
        contactList.add(new Contact("校巴班车服务队(2)", "68752441"));
        contactList.add(new Contact("医学部服务车队(固话)", "68759589"));
        contactList.add(new Contact("医学部服务车队(手机)", "13971084811"));

        contactList.add(new Contact("桂园餐厅", "68752376"));
        contactList.add(new Contact("枫园食堂", "68752535"));
        contactList.add(new Contact("梅园教工食堂", "68754427"));
        contactList.add(new Contact("珞珈山庄(总台)", "68752935"));
        contactList.add(new Contact("珞珈山庄(订餐)", "68752609"));
        contactList.add(new Contact("星湖园餐厅", "68771521"));

        return contactList;
    }
}
