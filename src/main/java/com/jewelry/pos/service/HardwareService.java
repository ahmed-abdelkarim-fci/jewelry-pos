package com.jewelry.pos.service;

public interface HardwareService {
    void openCashDrawer();
    void printReceipt(String content);
}