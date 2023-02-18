package com.lannstark.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportFieldInfo {
    private String jsonPointer;
    private String fieldSimpleName;
    private int columnIndex;
}
