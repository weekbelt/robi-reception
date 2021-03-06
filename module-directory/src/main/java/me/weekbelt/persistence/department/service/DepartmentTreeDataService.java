package me.weekbelt.persistence.department.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.weekbelt.persistence.department.DepartmentTree;
import me.weekbelt.persistence.department.repository.DepartmentTreeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DepartmentTreeDataService {

    private final DepartmentTreeRepository departmentTreeRepository;

    @Transactional(readOnly = true)
    public List<DepartmentTree> getByDescendant(String descendant) {
        return departmentTreeRepository.findByDescendant(descendant);
    }

    @Transactional
    public void saveAll(List<DepartmentTree> departmentTrees) {
        departmentTreeRepository.saveAll(departmentTrees);
    }

    @Transactional(readOnly = true)
    public List<DepartmentTree> getByAncestorAndDepth(String parentDepartmentId, int depth) {
        return departmentTreeRepository.findByAncestorAndDepth(parentDepartmentId, depth);
    }

    @Transactional(readOnly = true)
    public List<DepartmentTree> getByAncestor(String ancestor) {
        return departmentTreeRepository.findByAncestor(ancestor);
    }

    @Transactional
    public void deleteAll(List<DepartmentTree> departmentTrees) {
        departmentTreeRepository.deleteAll(departmentTrees);
    }
}
