package me.shetj.cling.entity;


import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.Collection;

import me.shetj.cling.util.Utils;


public class ClingDeviceList {

    private static ClingDeviceList INSTANCE = null;

    /**
     * 投屏设备列表 都是引用该 list
     */
    private Collection<ClingDevice> mClingDeviceList;

    private ClingDeviceList(){
        mClingDeviceList = new ArrayList<>();
    }

    public static ClingDeviceList getInstance() {
        if (Utils.isNull(INSTANCE)) {
            INSTANCE = new ClingDeviceList();
        }
        return INSTANCE;
    }

    public void removeDevice(ClingDevice device){
        mClingDeviceList.remove(device);
    }

    public void addDevice(ClingDevice device){
        mClingDeviceList.add(device);
    }

    public ClingDevice getClingDevice(Device device){
        for (ClingDevice clingDevice : mClingDeviceList){
            Device deviceTemp = clingDevice.getDevice();
            if (deviceTemp != null && deviceTemp.equals(device)){
                return clingDevice;
            }
        }
        return null;
    }

    public boolean contain(Device device){
        for (ClingDevice clingDevice : mClingDeviceList){
            Device deviceTemp = clingDevice.getDevice();
            if (deviceTemp != null && deviceTemp.equals(device)){
                return true;
            }
        }
        return false;
    }

    public Collection<ClingDevice> getClingDeviceList(){
        return mClingDeviceList;
    }

    public void setClingDeviceList(Collection<ClingDevice> clingDeviceList) {
        mClingDeviceList = clingDeviceList;
    }

    public void destroy(){
        mClingDeviceList = null;
        INSTANCE = null;
    }
}