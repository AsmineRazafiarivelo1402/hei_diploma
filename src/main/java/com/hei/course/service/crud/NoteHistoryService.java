package com.hei.course.service.crud;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JNoteHistory;
import com.hei.course.entity.JUsers;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.NoteHistoryMapper;
import com.hei.course.model.NoteHistory;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Users;
import com.hei.course.repository.JNoteHistoryRepository;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.repository.JTeachingRepository;
import com.hei.course.repository.JUsersRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteHistoryService {

    private final JNoteHistoryRepository noteHistoryRepository;
    private final JNoteRepository noteRepository;
    private final JUsersRepository usersRepository;
    private final JTeachingRepository teachingRepository;

    public NoteHistory create(
            NoteHistory model,
            UUID noteId,
            Users currentUser) {

        JNote note =
                noteRepository
                        .findById(noteId)
                        .orElseThrow(() -> new NotFoundException("Note not found: " + noteId));

        checkNoteAccess(note, currentUser);

        JUsers updatedBy =
                usersRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () -> new NotFoundException("User not found: " + currentUser.getId()));

        if (model.getUpdateAt() == null) {
            model.setUpdateAt(Instant.now());
        }

        JNoteHistory entity =
                NoteHistoryMapper.toEntity(model, note, updatedBy);

        JNoteHistory savedEntity =
                noteHistoryRepository.save(entity);

        return NoteHistoryMapper.toModel(savedEntity);
    }

    public List<NoteHistory> findAll(Users currentUser) {

        if (currentUser.getRole() == RoleEnum.STUDENT) {
            throw new AccessDeniedException("Students cannot access note histories");
        }

        return noteHistoryRepository.findAll().stream()
                .filter(history -> canAccessHistory(history, currentUser))
                .map(NoteHistoryMapper::toModel)
                .toList();
    }

    public NoteHistory findById(
            UUID id,
            Users currentUser) {

        JNoteHistory entity =
                noteHistoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("Note history not found: " + id));

        checkHistoryAccess(entity, currentUser);

        return NoteHistoryMapper.toModel(entity);
    }

    public NoteHistory update(
            UUID id,
            NoteHistory model,
            UUID noteId,
            Users currentUser) {

        JNoteHistory entity =
                noteHistoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("Note history not found: " + id));

        checkHistoryAccess(entity, currentUser);

        JNote note =
                noteRepository
                        .findById(noteId)
                        .orElseThrow(
                                () -> new NotFoundException("Note not found: " + noteId));

        checkNoteAccess(note, currentUser);

        entity.setOldValue(model.getOldValue());
        entity.setNewValue(model.getNewValue());
        entity.setReason(model.getReason());
        entity.setNote(note);

        if (model.getUpdateAt() != null) {
            entity.setUpdateAt(model.getUpdateAt());
        }

        JUsers updatedBy =
                usersRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () -> new NotFoundException("User not found: " + currentUser.getId()));

        entity.setUpdatedBy(updatedBy);

        JNoteHistory updatedEntity =
                noteHistoryRepository.save(entity);

        return NoteHistoryMapper.toModel(updatedEntity);
    }

    public void delete(
            UUID id,
            Users currentUser) {

        JNoteHistory entity =
                noteHistoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("Note history not found: " + id));

        checkHistoryAccess(entity, currentUser);

        noteHistoryRepository.delete(entity);
    }

    private void checkHistoryAccess(
            JNoteHistory history,
            Users currentUser) {

        if (currentUser.getRole() == RoleEnum.ADMIN) {
            return;
        }

        if (currentUser.getRole() != RoleEnum.TEACHER) {
            throw new AccessDeniedException(
                    "You cannot access this note history");
        }

        checkNoteAccess(history.getNote(), currentUser);
    }

    private void checkNoteAccess(
            JNote note,
            Users currentUser) {

        if (currentUser.getRole() == RoleEnum.ADMIN) {
            return;
        }

        if (currentUser.getRole() != RoleEnum.TEACHER) {
            throw new AccessDeniedException(
                    "You cannot manage note histories");
        }

        JExam exam = note.getExam();
        JCourses course = exam.getCourses();

        boolean teachesCourse =
                teachingRepository.existsByCourses_IdAndTeacher_Id(
                        course.getId(),
                        currentUser.getId());

        if (!teachesCourse) {
            throw new AccessDeniedException(
                    "You cannot manage note histories for this course");
        }
    }

    private boolean canAccessHistory(
            JNoteHistory history,
            Users currentUser) {

        if (currentUser.getRole() == RoleEnum.ADMIN) {
            return true;
        }

        if (currentUser.getRole() != RoleEnum.TEACHER) {
            return false;
        }

        JNote note = history.getNote();
        JExam exam = note.getExam();
        JCourses course = exam.getCourses();

        return teachingRepository.existsByCourses_IdAndTeacher_Id(
                course.getId(),
                currentUser.getId());
    }
}