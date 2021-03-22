package com.vahe.filesaver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileResponse {

    private String ref;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("size_bytes")
    private long sizeBytes;
}
