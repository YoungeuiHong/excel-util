# 1-2. Import

## Server

* `multipart/form-data` 형식으로 업로드된 엑셀 파일에서 데이터를 읽어와서, DTO 클래스의 인스턴스 리스트로 반환합니다.
    ```java
    @PostMapping(value = "/import-excel-via-dto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ExcelSampleDto> importExcel(@RequestPart(name = "file") MultipartFile multipartFile) throws IOException {
        ExcelFile<ExcelSampleDto> excelFile = new OneSheetXSSFExcel<>(multipartFile.getInputStream(), ExcelSampleDto.class);
        List<ExcelSampleDto> importedData = excelFile.read(ExcelSampleDto.class);
        return importedData;
    }
    ```

## Frontend
* Axios를 사용하여 업로드된 엑셀 파일을 서버로 전송하고, DTO의 리스트를 반환 받습니다.
  ```tsx
  import React, {useRef} from "react";
  import {Button} from "@mui/material";
  import axios from "axios";
  
  interface PersonalInfo {
      name: string;
      age: number;
      gender: string;
  }
  
  interface Grade {
      korean: number;
      english: number;
      math: number;
  }
  
  interface ExcelSampleDto {
      personalInfo: PersonalInfo;
      grade: Grade;
  }
  
  export default function BackImportPage() {
  
      const fileInputRef = useRef<HTMLInputElement>(null);
  
      const onClickImport = () => {
          fileInputRef?.current?.click();
      }
  
      const handleFileInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
          const selectedFile = event.target.files?.[0];
          if (selectedFile) {
              const form = new FormData();
              form.append('file', selectedFile);
              axios.post<ExcelSampleDto[]>(
                  '/import-excel-via-dto',
                  form
              ).then((response) => {
                  console.log(response.data);
              });
  
          }
      };
  
      return (
          <>
              <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileInputChange}
                  style={{display: 'none'}}
              />
              <Button
                  onClick={onClickImport}
              >
                  Import
              </Button>
          </>
      );
  }
  ```