package com.sdadas.spring2ts.examples.simple;

import com.sdadas.spring2ts.annotations.SharedModel;

@SharedModel("record")
public record HelloRecord(int id, String name) {

}
