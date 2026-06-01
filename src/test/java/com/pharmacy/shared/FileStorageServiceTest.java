package com.pharmacy.shared;

import com.pharmacy.shared.storage.FileStorageService;
import com.pharmacy.shared.storage.StorageFileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    private Path tempDir;

    @Test
    void init_shouldCreateStorageDirectory() {
        Path nestedStorage = tempDir.resolve("prescriptions").resolve("files");
        FileStorageService service = new FileStorageService(nestedStorage.toString());

        service.init();

        assertThat(nestedStorage).isDirectory();
    }

    @Test
    void store_shouldSaveFileWithHashedNameAndKeepExtension() throws Exception {
        FileStorageService service = new FileStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "prescription.pdf",
                "application/pdf",
                "content".getBytes()
        );

        String storedName = service.store(file);

        assertThat(storedName).endsWith(".pdf");
        assertThat(storedName).isNotEqualTo("prescription.pdf");
        assertThat(Files.readString(tempDir.resolve(storedName))).isEqualTo("content");
    }

    @Test
    void store_shouldSaveFileWithoutExtension() throws Exception {
        FileStorageService service = new FileStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "README",
                "text/plain",
                "notes".getBytes()
        );

        String storedName = service.store(file);

        assertThat(storedName).doesNotContain(".");
        assertThat(Files.readString(tempDir.resolve(storedName))).isEqualTo("notes");
    }

    @Test
    void store_shouldRejectMissingFilesAndBlankNames() {
        FileStorageService service = new FileStorageService(tempDir.toString());

        assertThatThrownBy(() -> service.store(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot store empty file");

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertThatThrownBy(() -> service.store(emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot store empty file");

        MockMultipartFile blankName = new MockMultipartFile("file", " ", "text/plain", "x".getBytes());
        assertThatThrownBy(() -> service.store(blankName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File name must not be blank");
    }

    @Test
    void load_shouldReturnReadableResourceForStoredFile() throws Exception {
        FileStorageService service = new FileStorageService(tempDir.toString());
        Path savedFile = tempDir.resolve("saved.txt");
        Files.writeString(savedFile, "saved content");

        Resource resource = service.load("saved.txt");

        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
        assertThat(resource.getContentAsByteArray()).containsExactly("saved content".getBytes());
    }

    @Test
    void load_shouldThrowWhenFileIsMissing() {
        FileStorageService service = new FileStorageService(tempDir.toString());

        assertThatThrownBy(() -> service.load("missing.pdf"))
                .isInstanceOf(StorageFileNotFoundException.class)
                .hasMessage("File not found: missing.pdf");
    }

    @Test
    void storageFileNotFoundException_shouldKeepMessageAndCause() {
        RuntimeException cause = new RuntimeException("disk");

        StorageFileNotFoundException withMessage = new StorageFileNotFoundException("missing");
        StorageFileNotFoundException withCause = new StorageFileNotFoundException("invalid", cause);

        assertThat(withMessage).hasMessage("missing");
        assertThat(withCause).hasMessage("invalid").hasCause(cause);
    }
}
