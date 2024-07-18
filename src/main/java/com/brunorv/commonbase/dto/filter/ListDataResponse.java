package com.brunorv.commonbase.dto.filter;

import lombok.Data;

@Data
public class ListDataResponse {
    ListFilterDto.Pagination pagination;
    ListFilterDto.Sorting sorting;

    private Object data;

    public ListDataResponse() {
    }

    public ListDataResponse(ListFilterDto.Pagination pagination,ListFilterDto.Sorting sorting, Object data) {
        this.pagination = pagination;
        this.data=data;
    }
}
