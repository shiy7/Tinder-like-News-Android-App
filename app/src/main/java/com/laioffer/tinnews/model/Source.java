package com.laioffer.tinnews.model;

import java.util.Objects;

public class Source {
    public String id;
    public String name;

    // to achieve equals, if not override, the comparison will be wrong
    // equals() 是用来比较两个 object 是否相同的。
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Objects.equals(id, source.id) &&
                Objects.equals(name, source.name);
    }

    // reduce collision
    // 首先我们有一个假设：任何两个 object 的 hashCode 都是不同的。
    // 那么在这个条件下，有两个 object 是相等的，那如果不重写 hashCode()，
    // 算出来的哈希值都不一样，就会去到不同的 buckets 了，就迷失在茫茫人海中了，
    // 再也无法相认，就和 equals() 条件矛盾了，证毕。
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    @Override
    public String toString() {
        return "Source{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
