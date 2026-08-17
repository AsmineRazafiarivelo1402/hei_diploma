package com.hei.course.service.crud;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.NoteMapper;
import com.hei.course.model.Note;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Users;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.repository.JStudentRepository;
import com.hei.course.repository.JTeachingRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

  private final JNoteRepository noteRepository;
  private final JExamRepository examRepository;
  private final JStudentRepository studentRepository;
  private final JTeachingRepository teachingRepository;

  public Note create(Note model, UUID examId, UUID studentId, Users currentUser) {

    JExam exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + examId));

    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

    checkTeacherAccess(exam, currentUser);

    if (model.getDate() == null) {
      model.setDate(Instant.now());
    }

    JNote entity = NoteMapper.toEntity(model, exam, student);

    JNote savedEntity = noteRepository.save(entity);

    return NoteMapper.toModel(savedEntity);
  }

  public List<Note> findAll(Users currentUser) {

    if (currentUser.getRole() == RoleEnum.STUDENT) {
      throw new AccessDeniedException("Students cannot access notes");
    }

    return noteRepository.findAll().stream()
        .filter(note -> canAccessNote(note, currentUser))
        .map(NoteMapper::toModel)
        .toList();
  }

  public Note findById(UUID id, Users currentUser) {

    JNote entity =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Note not found: " + id));

    checkNoteAccess(entity, currentUser);

    return NoteMapper.toModel(entity);
  }

  public Note update(UUID id, Note model, UUID examId, UUID studentId, Users currentUser) {

    JNote entity =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Note not found: " + id));

    checkNoteAccess(entity, currentUser);

    JExam exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + examId));

    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

    checkTeacherAccess(exam, currentUser);

    entity.setValue(model.getValue());
    entity.setDate(model.getDate());
    entity.setExam(exam);
    entity.setStudent(student);

    JNote updatedEntity = noteRepository.save(entity);

    return NoteMapper.toModel(updatedEntity);
  }

  public void delete(UUID id, Users currentUser) {

    JNote entity =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Note not found: " + id));

    checkNoteAccess(entity, currentUser);

    noteRepository.delete(entity);
  }

  private void checkNoteAccess(JNote note, Users currentUser) {

    if (currentUser.getRole() == RoleEnum.ADMIN) {
      return;
    }

    if (currentUser.getRole() != RoleEnum.TEACHER) {
      throw new AccessDeniedException("You cannot access this note");
    }

    checkTeacherAccess(note.getExam(), currentUser);
  }

  private void checkTeacherAccess(JExam exam, Users currentUser) {

    if (currentUser.getRole() == RoleEnum.ADMIN) {
      return;
    }

    if (currentUser.getRole() != RoleEnum.TEACHER) {
      throw new AccessDeniedException("You cannot manage notes");
    }

    UUID teacherId = currentUser.getId();
    JCourses course = exam.getCourses();

    boolean teachesCourse =
        teachingRepository.existsByCourses_IdAndTeacher_Id(course.getId(), teacherId);

    if (!teachesCourse) {
      throw new AccessDeniedException("You cannot manage notes for this course");
    }
  }

  private boolean canAccessNote(JNote note, Users currentUser) {

    if (currentUser.getRole() == RoleEnum.ADMIN) {
      return true;
    }

    if (currentUser.getRole() != RoleEnum.TEACHER) {
      return false;
    }

    JCourses course = note.getExam().getCourses();

    return teachingRepository.existsByCourses_IdAndTeacher_Id(course.getId(), currentUser.getId());
  }
}
