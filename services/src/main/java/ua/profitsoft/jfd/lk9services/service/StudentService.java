package ua.profitsoft.jfd.lk9services.service;

import org.springframework.web.multipart.MultipartFile;
import ua.profitsoft.jfd.lk9services.dto.StudentDetailsDto;
import ua.profitsoft.jfd.lk9services.dto.StudentInfoDto;
import ua.profitsoft.jfd.lk9services.dto.StudentQueryDto;
import ua.profitsoft.jfd.lk9services.dto.StudentSaveDto;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface StudentService {

  int createStudent(StudentSaveDto dto);

  StudentDetailsDto getStudent(int id);

  void updateStudent(int id, StudentSaveDto dto);

  List<StudentInfoDto> search(StudentQueryDto query);

  int uploadFromFile(MultipartFile multipart);

  void generateReport(HttpServletResponse httpServletResponse);
}
