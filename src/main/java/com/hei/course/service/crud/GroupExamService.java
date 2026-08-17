package com.hei.course.service.crud;

import com.hei.course.entity.JExam;
import com.hei.course.entity.JGroup;
import com.hei.course.entity.JGroupExam;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.GroupExamMapper;
import com.hei.course.model.GroupExam;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JGroupExamRepository;
import com.hei.course.repository.JGroupRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupExamService {

  private final JGroupExamRepository groupExamRepository;
  private final JGroupRepository groupRepository;
  private final JExamRepository examRepository;

  public GroupExam create(GroupExam model, UUID groupId, UUID examId) {

    JGroup group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

    JExam exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + examId));

    boolean alreadyExists =
        groupExamRepository.findByGroup_Id(groupId).stream()
            .anyMatch(groupExam -> groupExam.getExam().getId().equals(examId));

    if (alreadyExists) {
      throw new ConflictException("This exam is already assigned to this group");
    }

    JGroupExam entity = GroupExamMapper.toEntity(model, group, exam);

    JGroupExam savedEntity = groupExamRepository.save(entity);

    return GroupExamMapper.toModel(savedEntity);
  }

  public List<GroupExam> findAll() {

    return groupExamRepository.findAll().stream().map(GroupExamMapper::toModel).toList();
  }

  public GroupExam findById(UUID id) {

    JGroupExam entity =
        groupExamRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("GroupExam not found: " + id));

    return GroupExamMapper.toModel(entity);
  }

  public List<GroupExam> findByGroup(UUID groupId) {

    if (!groupRepository.existsById(groupId)) {
      throw new NotFoundException("Group not found: " + groupId);
    }

    return groupExamRepository.findByGroup_Id(groupId).stream()
        .map(GroupExamMapper::toModel)
        .toList();
  }

  public GroupExam update(UUID id, GroupExam model, UUID groupId, UUID examId) {

    JGroupExam entity =
        groupExamRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("GroupExam not found: " + id));

    JGroup group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

    JExam exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + examId));

    boolean alreadyExists =
        groupExamRepository.findByGroup_Id(groupId).stream()
            .anyMatch(
                groupExam ->
                    groupExam.getExam().getId().equals(examId) && !groupExam.getId().equals(id));

    if (alreadyExists) {
      throw new ConflictException("This exam is already assigned to this group");
    }

    entity.setGroup(group);
    entity.setExam(exam);

    JGroupExam updatedEntity = groupExamRepository.save(entity);

    return GroupExamMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {

    JGroupExam entity =
        groupExamRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("GroupExam not found: " + id));

    groupExamRepository.delete(entity);
  }
}
