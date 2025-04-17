package com.demo.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class Main {

    public static void main(String[] args) {
        try {
            Entry entry = SphU.entry("aaa");
        } catch (BlockException e) {
            throw new RuntimeException(e);
        }

    }
}
