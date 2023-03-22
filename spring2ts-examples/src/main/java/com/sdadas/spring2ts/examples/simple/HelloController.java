package com.sdadas.spring2ts.examples.simple;

import com.sdadas.spring2ts.annotations.SharedService;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sławomir Dadas
 */
@CrossOrigin
@RestController
@SharedService
public class HelloController {

    private final AtomicInteger id = new AtomicInteger();

    @RequestMapping("/hello")
    public HelloResponse hello(@RequestParam(value = "name", defaultValue = "world") String name) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + name);
    }

    @PostMapping("/hello-json")
    public HelloResponse helloJson(  @Validated
                                     @ApiParam(value = "InfoCarRelations 数据对象", required = true)
                                     @RequestBody
                                     List<HelloDto> his) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + his.size());
    }

    @PostMapping("/hello-json")
    public HelloResponse helloJson2(  @Validated
                                     @ApiParam(value = "InfoCarRelations 数据对象", required = true)
                                     @RequestBody
                                     HelloDto his) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + his.toString());
    }
}
