package com.lannstark.body;

import com.lannstark.dto.Grade;
import com.lannstark.dto.PersonalInfo;
import com.lannstark.dto.StudentDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import static com.lannstark.utils.SuperClassReflectionUtils.getField;

public class BodyGenerationTest {
    //
    @Test
    @DisplayName("Getting value of DTO by using field path")
    public void getValueOfDto() throws Exception {
        //
        StudentDto studentDto = new StudentDto(
                new PersonalInfo("Youngeui", 30, "Female"),
                new Grade(80, 90, 100)
        );

        Object value = getDtoValue("personalInfo,name", studentDto);

        Assertions.assertEquals(value, "Youngeui");
    }

    /**
     * Field path를 사용하여 nested field의 값을 가져오는 메서드
     * @param fieldPath
     * @param dtoData
     * @return
     * @throws Exception
     */
    private static Object getDtoValue(String fieldPath, Object dtoData) throws Exception {
        //
        Queue<String> fieldNameQueue = new LinkedList<>(Arrays.asList(fieldPath.split(",")));
        Object data = dtoData;
        Field field = null;

        while (!fieldNameQueue.isEmpty()) {
            field = getField(data.getClass(), fieldNameQueue.poll());
            field.setAccessible(true);
            data = field.get(data);
        }

        return data;
    }
}
