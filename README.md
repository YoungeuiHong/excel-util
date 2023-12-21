# Excel Export / Import Module

## About

우아한형제들의 소스코드를 Fork하여 아래 기능들을 추가한 모듈입니다.

✔️ Header 영역 Column Grouping 기능   
✔️ Excel Import 기능   
✔️ 클라이언트단에서 정의한 Header를 통한 Excel Export / Import 기능

* 기존 소스코드: https://github.com/lannstark/excel-download
* 참고자료: https://techblog.woowahan.com/2698/

## Getting Started

### Dependencies

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
  implementation('com.github.YoungeuiHong:excel-util:master-SNAPSHOT')
}
```

## 엑셀 Export / Import 방법

### [1. Export](./docs/EXPORT_BY_BACKEND.md)
### [2. Import](./docs/IMPORT_BY_BACKEND.md)

## 엑셀 스타일 적용 방법

You can create custom ExcelCellStyle with Class or Enum

If you use Class, you don't need to designate enumName of @ExcelColumnStyle

```java
public class BlueHeaderStyle implements ExcelCellStyle {

    @Override
    public void apply(CellStyle cellStyle) {
        // Do anything you want to change style
        cellStyle.setBlaBla();
    }

} 
``` 

For convenient custom style class, we provide template style class, CustomExcelCellStyle
You can set

- cell color
- 4-side borders type
- cell contents align
  Other features will be updated gradually.

```java
public class BlueHeaderStyle extends CustomExcelCellStyle {

    @Override
    public void configure(ExcelCellStyleConfigurer configurer) {
        configurer.foregroundColor(223, 235, 246)
                .excelBorders(DefaultExcelBorders.newInstance(ExcelBorderStyle.THIN))
                .excelAlign(DefaultExcelAlign.CENTER_CENTER);
    }

}
```

```java

@DefaultHeaderStyle(
        style = @ExcelColumnStyle(excelCellStyleClass = BlueHeaderStyle.class)
)
public class ExcelDto {

    private String field1;

}
```

If you use Enum, you have to specify enumName of @ExcelColumnStyle

```java
public enum CustomCellStyle implements ExcelCellStyle {

    CUSTOM_HEADER(field1, field2);

    // Whatever fields you need to configure cell style
    private final String field1;
    private final int field2;

    @Override
    public void apply(CellStyle cellStyle) {
        // Do anything you want to change style with defined enum fields.
        cellStyle.setBlaBla();
    }

}
```  

```java
public class ExcelDto {

    @ExcelColumn(headerName = "Field Header Title",
            bodyStyle = @ExcelColumnStyle(excelCellStyleClass = CustomCellStyle.class, enumName = "CUSTOM_HEADER")
    )
    private String field1;

}
```
