package me.weekbelt.apiserver.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.weekbelt.apiserver.department.dto.DepartmentResponse;
import me.weekbelt.apiserver.directory.dto.DirectoryResponse;
import me.weekbelt.persistence.Phone;
import me.weekbelt.persistence.PhoneType;
import me.weekbelt.persistence.department.Department;
import me.weekbelt.persistence.department.DepartmentTree;
import me.weekbelt.persistence.department.repository.DepartmentRepository;
import me.weekbelt.persistence.department.repository.DepartmentTreeRepository;
import me.weekbelt.persistence.department.service.DepartmentDataService;
import me.weekbelt.persistence.department.service.DepartmentTreeDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class DirectoryQueryServiceTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentTreeRepository departmentTreeRepository;

    private DepartmentDataService departmentDataService;

    private DepartmentTreeDataService departmentTreeDataService;

    private DirectoryQueryService directoryQueryService;

    @BeforeEach
    public void initDirectoryService() {
        departmentDataService = new DepartmentDataService(departmentRepository);
        departmentTreeDataService = new DepartmentTreeDataService(departmentTreeRepository);
        directoryQueryService = new DirectoryQueryService(departmentDataService, departmentTreeDataService);
        createDirectory();
    }

    @Test
    @DisplayName("????????? ?????? - 1?????? ??????")
    public void get_depth1_directory() {
        // given
        String rootDepartmentId = "rootId";

        // when
        DirectoryResponse directoryResponse = directoryQueryService.getDirectory(rootDepartmentId);

        // then
        List<DepartmentResponse> departmentResponses = directoryResponse.getDepartmentResponses();
        assertThat(departmentResponses.size()).isEqualTo(2);
    }

    private void createDirectory() {
        Department rootDepartment = createRootDepartment();
        createChildDepartment(rootDepartment, "???????????????_1????????????_1");
        createChildDepartment(rootDepartment, "???????????????_1????????????_2");
    }

    private Department createRootDepartment() {
        Phone rootPhone = Phone.builder()
            .number("1234")
            .phoneType(PhoneType.INWARD_DIALING)
            .build();
        Department rootDepartment = Department.builder()
            .id("rootId")
            .name("???????????????")
            .phone(rootPhone)
            .branchId("test")
            .build();
        departmentRepository.save(rootDepartment);

        DepartmentTree departmentTree = DepartmentTree.builder()
            .id(UUID.randomUUID().toString())
            .ancestor(rootDepartment.getId())
            .descendant(rootDepartment.getId())
            .depth(0)
            .branchId("test")
            .build();
        departmentTreeRepository.save(departmentTree);

        return rootDepartment;
    }

    private void createChildDepartment(Department rootDepartment, String departmentName) {
        Phone phone = Phone.builder()
            .number("1234")
            .phoneType(PhoneType.INWARD_DIALING)
            .build();
        Department childDepartment = Department.builder()
            .id(UUID.randomUUID().toString())
            .name(departmentName)
            .phone(phone)
            .branchId("test")
            .build();
        departmentRepository.save(childDepartment);

        List<DepartmentTree> parentDepartmentTrees = departmentTreeDataService.getByDescendant(rootDepartment.getId());
        List<DepartmentTree> newDepartmentTrees = parentDepartmentTrees.stream()
            .map(departmentTree -> DepartmentTree.builder()
                .id(UUID.randomUUID().toString())
                .ancestor(departmentTree.getAncestor())
                .descendant(childDepartment.getId())
                .depth(departmentTree.getDepth() + 1)
                .branchId(departmentTree.getBranchId())
                .build())
            .toList();
        List<DepartmentTree> departmentTrees = new ArrayList<>(newDepartmentTrees);

        DepartmentTree departmentTree = DepartmentTree.builder()
            .id(UUID.randomUUID().toString())
            .ancestor(childDepartment.getId())
            .descendant(childDepartment.getId())
            .depth(0)
            .branchId(childDepartment.getBranchId())
            .build();
        departmentTrees.add(departmentTree);

        departmentTreeDataService.saveAll(departmentTrees);
    }
}