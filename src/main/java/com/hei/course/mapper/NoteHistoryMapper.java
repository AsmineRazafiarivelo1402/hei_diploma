package com.hei.course.mapper;

import com.hei.course.entity.JNote;
import com.hei.course.entity.JNoteHistory;
import com.hei.course.entity.JUsers;
import com.hei.course.model.Note;
import com.hei.course.model.NoteHistory;
import com.hei.course.model.Users;

public class NoteHistoryMapper {

  private NoteHistoryMapper() {}

  public static NoteHistory toModel(JNoteHistory entity) {

    Note note = null;
    if (entity.getNote() != null) {
      note = NoteMapper.toModel(entity.getNote());
    }

    Users updatedBy = null;
    if (entity.getUpdatedBy() != null) {
      updatedBy = UserMapper.toModel(entity.getUpdatedBy());
    }

    return NoteHistory.builder()
        .id(entity.getId())
        .oldValue(entity.getOldValue())
        .newValue(entity.getNewValue())
        .updateAt(entity.getUpdateAt())
        .reason(entity.getReason())
        .updatedBy(updatedBy)
        .note(note)
        .build();
  }

  public static JNoteHistory toEntity(NoteHistory model, JNote note, JUsers updatedBy) {

    return JNoteHistory.builder()
        .id(model.getId())
        .oldValue(model.getOldValue())
        .newValue(model.getNewValue())
        .updateAt(model.getUpdateAt())
        .reason(model.getReason())
        .note(note)
        .updatedBy(updatedBy)
        .build();
  }
}
