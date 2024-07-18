package com.brunorv.commonbase.dto.filter;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ListFilterDto {
    @NotNull
    Pagination pagination;

    @NotEmpty
    @Size(min = 1,max = 30)
    private List<String> fields;
    @NotNull
    private List<List<Condition>> conditions;

    Sorting sorting;

    @Data
    public static class Pagination {
        private int page;
        private int size;

        private int totalRows;

    }
    @Data
    public static class Condition {
        private String field;
        private String operator;
        private Object value;
    }

    @Data
    public static class Sorting {
        private String field;
        private int order;


        public String getField(){
            return this.field==null?"":this.field;
        }

        public String getOrderDirection() {
            return order == 1 ? " ASC " : order == -1 ? " DESC " : "";
        }

    }
}
