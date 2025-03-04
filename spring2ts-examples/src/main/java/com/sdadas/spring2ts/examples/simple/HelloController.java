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

    /**
     * Construct a new HelloController
     * @param name The name of the controller
     * @return  The created controller
     */
    @RequestMapping("/hello")
    public HelloResponse hello(@RequestParam(value = "name", defaultValue = "world") String name) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + name);
    }

    @RequestMapping("/helloWithoutAtt")
    public HelloResponse helloWithoutAtt(String name) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + name);
    }

    @PostMapping("/hello-json1")
    public HelloResponse helloJson1(  @Validated
                                     @ApiParam(value = "InfoCarRelations 数据对象", required = true)
                                     @RequestBody
                                     List<HelloDto> his) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + his.size());
    }

    @PostMapping("/hello-json2")
    public HelloResponse helloJson2(  @Validated
                                     @ApiParam(value = "InfoCarRelations 数据对象", required = true)
                                     @RequestBody
                                     HelloDto his) {
        return new HelloResponse(id.incrementAndGet(), "Hello " + his.toString());
    }

    /**
     * this must bypass!
     * @return number
     */
    private int getInt(){
        return 1;
    }
}
