package com.sdadas.spring2ts.core.plugin.output.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sdadas.spring2ts.core.utils.AnnotationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.JavaType;

import java.util.*;
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

    // `value` is common key, so we don't need to show it
    static Set<String> omitable = new HashSet<>(
            Arrays.asList("description",
            "comment", "tags","value","name"));

    static boolean canOmitKey(String key){return omitable.contains(key);}

    static  String toString(Multimap<String, String> map){
        return String.join(",",
                map.asMap().entrySet()
                        .stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry ->
                        canOmitKey(entry.getKey())
                                ? getString(entry.getValue())
                                : entry.getKey() + ":" + getString(entry.getValue()))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet()));

    }

    private static String getString(Collection<String> entry) {
        return entry.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }

    public static String extractedComment(JavaType<?> type) {
        String comment = type.hasJavaDoc()? type.getJavaDoc().getFullText()+"\n":"";
        Multimap<String, String> map = HashMultimap.create();
        extractedComment(type, map);

        return comment +"\n" + toString(map);
    }
}
