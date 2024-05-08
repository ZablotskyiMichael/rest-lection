package ua.profitsoft.jfd.lk9services.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.profitsoft.jfd.lk9services.data.GroupData;
import ua.profitsoft.jfd.lk9services.data.StudentData;
import ua.profitsoft.jfd.lk9services.dto.IdentifiedName;
import ua.profitsoft.jfd.lk9services.dto.StudentDetailsDto;
import ua.profitsoft.jfd.lk9services.dto.StudentInfoDto;
import ua.profitsoft.jfd.lk9services.dto.StudentQueryDto;
import ua.profitsoft.jfd.lk9services.dto.StudentSaveDto;
import ua.profitsoft.jfd.lk9services.dto.StudentUploadDto;
import ua.profitsoft.jfd.lk9services.exceptions.FileUploadException;
import ua.profitsoft.jfd.lk9services.exceptions.NotFoundException;
import ua.profitsoft.jfd.lk9services.repository.GroupRepository;
import ua.profitsoft.jfd.lk9services.repository.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;

  private final GroupRepository groupRepository;

  private final ObjectMapper objectMapper;

  @Override
  public int createStudent(StudentSaveDto dto) {
    validateStudent(dto);
    StudentData data = new StudentData();
    updateDataFromDto(data, dto);
    return studentRepository.save(data);
  }

  @Override
  public StudentDetailsDto getStudent(int id) {
    StudentData data = getOrThrow(id);
    return convertToDetails(data);
  }

  @Override
  public void updateStudent(int id, StudentSaveDto dto) {
    StudentData data = getOrThrow(id);
    updateDataFromDto(data, dto);
    studentRepository.save(data);
  }

  @Override
  public List<StudentInfoDto> search(StudentQueryDto query) {
    return studentRepository.search(query).stream()
        .map(this::toInfoDto)
        .toList();
  }

  @Override
  public int uploadFromFile(MultipartFile file) {
    try {
      byte[] fileBytes = file.getBytes();
      List<StudentData> students = objectMapper.readValue(fileBytes, new TypeReference<List<StudentUploadDto>>() {})
              .stream()
              .map(this::convertFromUpload)
              .toList();
      return studentRepository.saveAll(students);
    } catch (IOException e) {
      throw new FileUploadException(e.getMessage());
    }
  }

    @Override
    public void generateReport(HttpServletResponse response) {
      response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.txt");
      try {
        StringBuilder sb = new StringBuilder();
        List<StudentData> students = studentRepository.getAll();
        for (StudentData student : students) {
          sb.append(student.getName())
                  .append(" ")
                  .append(student.getGroup().getName())
                  .append("\n");
        }
        response.getOutputStream().write(sb.toString().getBytes());
        response.getOutputStream().flush();
      } catch (IOException e) {
        throw new RuntimeException("Error generating text file", e);
      }
    }

    private StudentData convertFromUpload(StudentUploadDto uploadDto) {
    StudentData data = new StudentData();
    data.setName(uploadDto.getName());
    data.setSurname(uploadDto.getSurname());
    data.setGroup(resolveGroup(uploadDto.getGroupId()));
    data.setLastUpdateTime(Instant.now());
    return data;
  }

  private static void validateStudent(StudentSaveDto dto) {
    if (dto.getBirthDate() != null && dto.getBirthDate().isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("birthDate should be before now");
    }
  }

  private StudentData getOrThrow(int id) {
    return studentRepository.get(id)
        .orElseThrow(() -> new NotFoundException("Student with id %d not found".formatted(id)));
  }

  private StudentInfoDto toInfoDto(StudentData data) {
    return StudentInfoDto.builder()
        .id(data.getId())
        .fullName(data.getName() + " " + data.getSurname())
        .groupName(data.getGroup() != null ? data.getGroup().getName() : null)
        .build();
  }

  private void updateDataFromDto(StudentData data, StudentSaveDto dto) {
    data.setName(dto.getName());
    data.setSurname(dto.getSurname());
    data.setBirthDate(dto.getBirthDate());
    data.setGroup(resolveGroup(dto.getGroupId()));
    data.setLastUpdateTime(Instant.now());
  }

  private GroupData resolveGroup(Integer groupId) {
    if (groupId == null) {
      return null;
    }
    return groupRepository.get(groupId)
        .orElseThrow(() -> new IllegalArgumentException("Group with id %d not found".formatted(groupId)));
  }

  private StudentDetailsDto convertToDetails(StudentData data) {
    return StudentDetailsDto.builder()
        .id(data.getId())
        .name(data.getName())
        .surname(data.getSurname())
        .birthDate(data.getBirthDate())
        .group(groupToIdentifiedName(data.getGroup()))
        .lastUpdateTime(data.getLastUpdateTime())
        .build();
  }

  private static IdentifiedName groupToIdentifiedName(GroupData group) {
    if (group == null) {
      return null;
    }
    return new IdentifiedName(group.getId(), group.getName());
  }
}
