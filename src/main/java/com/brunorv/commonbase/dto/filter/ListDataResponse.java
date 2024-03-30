package com.brunorv.commonbase.dto.filter;

import lombok.Data;

@Data
public class ListDataResponse {
    ListFilterDto.Pagination pagination;

    private Object data;

    public ListDataResponse() {
    }

    public ListDataResponse(ListFilterDto.Pagination pagination, Object data) {
        this.pagination = pagination;
        this.data=data;
    }
}
