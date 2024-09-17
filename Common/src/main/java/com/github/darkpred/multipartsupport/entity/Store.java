package com.github.darkpred.multipartsupport.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private final List<MultiPart> parts = new ArrayList<>();
    private final Map<String, MultiPart> partsByRef = new HashMap<>();

}
