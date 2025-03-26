package com.sharing.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharing.account.Account;
import com.sharing.account.AccountRepository;
import com.sharing.account.AccountSecurityService;
import com.sharing.permission.Permission;
import com.sharing.permission.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AccountRepository accountRepository;

    @MockitoBean
    FileRepository fileRepository;

    @MockitoBean
    PermissionRepository permissionRepository;

    @MockitoBean
    AccountSecurityService accountSecurityService;

    @Autowired
    ObjectMapper objectMapper;

    Account account1;
    Account account2;

    File file1;
    File file2;

    List<Account> accounts;
    List<File> files;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        account1 = new Account(1L, "u1", "p1");
        account2 = new Account(2L, "u2", "p2");

        file1 = new File(1L, "F1", "F1 Content", 1L, now);
        file2 = new File(2L, "F2", "F2 Content", 2L, now);

        accounts = List.of(account1, account2);
        files = List.of(file1, file2);

    }

    @Test
    void shouldFindAllFiles() throws Exception {
        Mockito.when(fileRepository.findAll()).thenReturn(files);

        Mockito.when(permissionRepository.findByAccountAndFile(1L, 1L))
                .thenReturn(Optional.of(new Permission(1L, 1L, 1L,
                        true, false, true)));

        mockMvc.perform(get("/api/v1/files").with(user("u1").password("p1")))
                .andExpectAll(status().isOk(), content().json(objectMapper.writeValueAsString(List.of(file1))));
    }

    @Test
    void user1ShouldViewFile1() throws Exception {
        Mockito.when(fileRepository.findById(1L)).thenReturn(Optional.of(file1));

        Mockito.when(permissionRepository.findByAccountAndFile(1L, 1L))
                .thenReturn(Optional.of(new Permission(1L, 1L, 1L,
                        true, false, true)));
        mockMvc.perform(get("/api/v1/files/1").with(user("u1").password("p1")))
                .andExpect(status().isOk());
    }

    @Test
    void user1ShouldNotViewFile2() throws Exception {
        Mockito.when(fileRepository.findById(2L)).thenReturn(Optional.of(file2));
        mockMvc.perform(get("/api/v1/files/2").with(user("u1").password("p1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void user1ShouldViewFile2WithPermission() throws Exception {
        Mockito.when(fileRepository.findById(2L)).thenReturn(Optional.of(file2));

        Mockito.when(permissionRepository.findByAccountAndFile(1L, 2L))
                    .thenReturn(Optional.of(new Permission(1L, 1L, 2L,
                            true, false, true)));

        mockMvc.perform(get("/api/v1/files/2").with(user("u1").password("p1")))
                .andExpectAll(status().isOk());
    }


    @Test
    void user1ShouldNotEditFile2() throws Exception {
        Mockito.when(fileRepository.findById(2L)).thenReturn(Optional.of(file2));
        mockMvc.perform(put("/api/v1/files/2").contentType(MediaType.APPLICATION_JSON).
                with(user("u1").password("p1"))
                .content(objectMapper.writeValueAsString(file2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void user1ShouldEditLockedFile2() throws Exception {
        Mockito.when(fileRepository.findById(2L)).thenReturn(Optional.of(file2));

        // Gives all permissions for file2 to user1
        Mockito.when(permissionRepository.findByAccountAndFile(1L, 2L))
                .thenReturn(Optional.of(new Permission(1L, 1L, 2L,
                        true, true, true)));

        // User2 locks file2
        mockMvc.perform(put("/api/v1/files/2/lock").contentType(MediaType.APPLICATION_JSON).
                        with(user("u2").password("p2")));

        // Now user1 cannot edit file2
        mockMvc.perform(put("/api/v1/files/2").contentType(MediaType.APPLICATION_JSON).
                        with(user("u1").password("p1"))
                        .content(objectMapper.writeValueAsString(file2)))
                .andExpect(status().isForbidden());
    }


}