package com.cnksi.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/12/7 11:31
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ListUtils {


    public interface Func<T1, T2> {
        T2 action(T1 t);
    }

    public interface Func2<T1, T2, T3> {
        T3 action(T1 t1, T2 t2);
    }

    /**
     * 争对某一字段或关键字建立索引Map。
     *
     * @param src         目标源
     * @param keySelector 关键字选择器
     * @param <K>
     * @param <V>
     * @return 返回关联Map。
     */
    public static <K, V> Map<K, V> indexMap(List<V> src, Func<V, K> keySelector) {
        Map<K, V> rs = new HashMap<>();
        if (src == null || src.size() == 0) return rs;
        for (V v : src) {
            rs.put(keySelector.action(v), v);
        }
        return rs;
    }

    /**
     * List转换为Map
     *
     * @param src         源List
     * @param keySelector key选择器
     * @param <K>         key类型
     * @param <V>
     * @return 返回根据Key 选择器分类后的Map 该Map为LinkedHashMap
     */
    public static <K, V> Map<K, List<V>> groupBy(List<V> src, Func<V, K> keySelector) {
        return groupBy(src, new LinkedHashMap<K, List<V>>(), keySelector);
    }


    /**
     * List转换为Map
     *
     * @param dstMap      目标Map
     * @param src         源List
     * @param keySelector key选择器
     * @param <K>         key类型
     * @param <V>
     * @return 返回根据Key 选择器分类后的Map
     */
    public static <K, V> Map<K, List<V>> groupBy(List<V> src, Map<K, List<V>> dstMap, Func<V, K> keySelector) {
        if (src == null || src.size() == 0) return dstMap;
        List<V> temp;
        for (V v : src) {
            K key = keySelector.action(v);
            if ((temp = dstMap.get(key)) == null) {
                temp = new ArrayList<>();
                dstMap.put(key, temp);
            }
            temp.add(v);
        }
        return dstMap;
    }

    /**
     * 根据条件过滤List.
     *
     * @param src      源List
     * @param selector 选择器，返回true时会被选择
     * @param <T>
     * @return 返回过滤后的新List.
     */
    public static <T> List<T> filiter(List<T> src, Func<T, Boolean> selector) {
        List<T> rs = new ArrayList<>();
        if (src != null) {
            for (T t : src) {
                if (selector.action(t)) {
                    rs.add(t);
                }
            }
        }
        return rs;
    }

    /**
     * List类型变换
     *
     * @param src     源List
     * @param convert 类型转换器
     * @param <T1>    源类型
     * @param <T2>    目标类型
     * @return 返回变换后的List
     */
    public static <T1, T2> List<T2> map(List<T1> src, Func<T1, T2> convert) {
        return map(src, new ArrayList<T2>(), convert);
    }

    /**
     * List类型变换
     *
     * @param src     源List
     * @param dst     目标List，如果为空或与源List是同一个对象则会创建一个ArrayList.
     * @param convert 转换器
     * @param <T1>    源类型
     * @param <T2>    目标类型
     * @return 返回添加后的List。
     */
    public static <T1, T2> List<T2> map(List<T1> src, List<T2> dst, Func<T1, T2> convert) {
        if (src == null) return dst;
        if (src == dst) {
            //目标不能与源是同一个List.
            dst = new ArrayList<>();
        }
        if (dst == null) dst = new ArrayList<>();
        for (T1 t1 : src) {
            dst.add(convert.action(t1));
        }
        return dst;
    }

    /**
     * List 去重。相同的对象会被移除，其与HashSet规则一致。
     *
     * @param src 源List
     * @param <T>
     * @return 返回通过HashSet去重之后的List
     */
    public static <T> List<T> distinct(List<T> src) {
        if (src == null || src.size() == 0) return new ArrayList<>();
        return new ArrayList<>(new LinkedHashSet(src));
    }

    /**
     * List 去重。相同的对象会被移除，其与HashSet规则一致。
     *
     * @param src         源List
     * @param keySelector 根据keySelector返回的Key来进行过滤。
     * @param <T>
     * @return 返回通过HashSet去重之后的List
     */
    public static <T, K> List<T> distinctBy(List<T> src, Func<T, K> keySelector) {
        if (src == null || src.size() == 0) return new ArrayList<>();
        HashSet<K> set = new HashSet<>();
        List<T> rs = new ArrayList<>();
        for (T t : src) {
            if (set.add(keySelector.action(t))) {
                rs.add(t);
            }
        }
        return rs;
    }

    /**
     * 按照转化器转换成String
     *
     * @param src     源List
     * @param convert 转换器 第二个bool值表示是否是最后一个元素
     * @param <T>
     * @return
     */
    public static <T> String toString(List<T> src, Func2<T, Boolean, String> convert) {
        if (src == null || src.size() == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = src.size(); i < size; i++) {
            builder.append(convert.action(src.get(i), i == size - 1));
        }
        return builder.toString();
    }

    /**
     * 根据条件查找第一符合要求的元素
     *
     * @param src      目标源
     * @param selector 条件选择器
     * @param <T>
     * @return 找到第一个就返回，找不到返回null;
     */
    public static <T> T find(List<T> src, Func<T, Boolean> selector) {
        if (src == null || src.size() == 0) return null;
        for (T t : src) {
            if (selector.action(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> LinkedHashMap<String, String> toStringMap(List<T> src, Func2<T, Map<String, String>, Void> convert) {
        LinkedHashMap<String, String> rs = new LinkedHashMap<>();
        if (src != null) {
            for (T t : src) {
                convert.action(t, rs);
            }
        }
        return rs;
    }
}
