package com.sdadas.spring2ts.examples.simple;

import com.sdadas.spring2ts.annotations.SharedModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sławomir Dadas
 */
@SharedModel
public class HelloResponse {

    /**
     * 这里是id注释
     */
    @ApiModelProperty(value = "这里是id注解")
    private int id;

    private String greeting;

    public HelloResponse() {
    }

    public HelloResponse(int id, String greeting) {
        this.id = id;
        this.greeting = greeting;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
