package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorilla.gim.backend.controller.api.AvatarControllerApi;
import ru.gorilla.gim.backend.dto.AccountDto;
import ru.gorilla.gim.backend.dto.FileMetaDto;
import ru.gorilla.gim.backend.dto.FileRegistrationRequest;
import ru.gorilla.gim.backend.dto.UploadRegistrationResponse;
import ru.gorilla.gim.backend.service.AccountService;
import ru.gorilla.gim.backend.service.FileMetaService;
import ru.gorilla.gim.backend.service.MinoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController implements AvatarControllerApi {

    private final MinoService minoService;
    private final FileMetaService fileMetaService;
    private final AccountService accountService;

    @PostMapping("/register-upload")
    public ResponseEntity<UploadRegistrationResponse> registerUpload(@RequestBody FileRegistrationRequest request) {
        try {
            AccountDto accountDto = accountService.findById(request.accountId());

            if (accountDto != null) {
                String uniqueId = UUID.randomUUID().toString();
                String objectKey = "uploads/" + uniqueId + "-" + request.fileName();

                String presignedUrl = minoService.getPreSignedUploadUrlForAvatar(objectKey);

                FileMetaDto fileMetaDto = fileMetaService.add(new FileMetaDto(
                        request.fileName(),
                        objectKey,
                        request.contentType(),
                        "PENDING"
                ));

                accountService.setAvatar(request.accountId(), fileMetaDto.getId());

                return ResponseEntity.ok(new UploadRegistrationResponse(presignedUrl, objectKey));

            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FileMetaDto>> findAll() {
        return ResponseEntity.ok(fileMetaService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FileMetaDto>> findPage(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(fileMetaService.findPage(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMetaDto> findById(@PathVariable Long id) {
        FileMetaDto dto = fileMetaService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileMetaDto> update(@PathVariable Long id, @RequestBody FileMetaDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(fileMetaService.update(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FileMetaDto> patchUpdate(@PathVariable Long id,
                                                    @RequestBody Map<String, Object> fields) {
        FileMetaDto dto = fileMetaService.patchUpdate(id, fields);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        fileMetaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
