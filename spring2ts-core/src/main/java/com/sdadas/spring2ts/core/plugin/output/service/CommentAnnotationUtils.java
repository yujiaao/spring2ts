package com.sdadas.spring2ts.core.plugin.output.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.sdadas.spring2ts.core.utils.AnnotationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.JavaType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommentAnnotationUtils {

    public static void extractedComment(AnnotationTarget<?> type, Multimap<String, String> mapAll) {
        Arrays.stream(new Class[] {ApiOperation.class, Api.class}).forEach(annotation ->{
            Multimap<String, String> map1 = AnnotationUtils.getAnnotationAsMap(type, annotation);
            if(map1!=null) {
                mapAll.put("comment",toString(map1));
            }
        });

        Arrays.stream(new Class[] {ApiParam.class}).forEach(annotation ->{
            Multimap<String, String> map1 = AnnotationUtils.getAnnotationAsMap(type, annotation);
            if(map1!=null) {
                mapAll.put("description",toString(map1));
            }
        });
    }

    static  String toString(Multimap<String, String> map){
        return  map.asMap().entrySet().stream().map(entry ->
                entry.getKey().equals("value") // `value` is common key, so we don't need to show it
                        ? entry.getValue().stream().collect(Collectors.joining(","))
                        : entry.getKey()+":"+entry.getValue().stream().collect(Collectors.joining(","))).collect(Collectors.joining(","));
    }

    public static String extractedComment(JavaType<?> type) {
        String comment = type.hasJavaDoc()? type.getJavaDoc().getFullText()+"\n":"";
        Multimap<String, String> map = HashMultimap.create();
        extractedComment(type, map);

        return comment +"\n" + toString(map);
    }
}
