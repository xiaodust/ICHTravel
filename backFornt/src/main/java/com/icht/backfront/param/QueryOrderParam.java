package com.icht.backfront.param;

import com.icht.backfront.model.Paging;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class QueryOrderParam extends Paging {
    private String productDetailId;
@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime time;

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
