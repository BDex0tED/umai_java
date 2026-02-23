package com.sayra.umai;

import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.repo_service.WorkDataService;
import com.sayra.umai.service.DropboxService;
import com.sayra.umai.service.impl.WorkServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkServiceTest {

    @Mock
    private WorkDataService workDataService;

    @Mock
    private DropboxService dropboxService;

    @InjectMocks
    private WorkServiceImpl workService;

    @Test
    void uploadCover_ShouldUploadToDropboxAndSaveWork_WhenFileIsValid() throws Exception {
        Long workId = 1L;
        Work mockWork = new Work();
        mockWork.setId(workId);
        mockWork.setTitle("Манас");

        MultipartFile fakeFile = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        String expectedDropboxUrl = "https://dropbox.com/fake-url/cover.jpg";

        when(workDataService.findByIdOrThrow(workId)).thenReturn(mockWork);
        when(dropboxService.uploadFile(fakeFile, "covers")).thenReturn(expectedDropboxUrl);

        String resultUrl = workService.uploadCover(workId, fakeFile);

        assertThat(resultUrl).isEqualTo(expectedDropboxUrl);
        assertThat(mockWork.getCoverUrl()).isEqualTo(expectedDropboxUrl); // Убеждаемся, что в объект Work записался URL

        verify(workDataService, times(1)).saveWork(mockWork);
    }

    @Test
    void uploadCover_ShouldThrowIllegalArgumentException_WhenFileIsEmpty() {
        // Arrange
        Long workId = 1L;
        Work mockWork = new Work();

        when(workDataService.findByIdOrThrow(workId)).thenReturn(mockWork);

        MultipartFile emptyFile = new MockMultipartFile("cover", new byte[0]);

        assertThatThrownBy(() -> workService.uploadCover(workId, emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cover image is required");

        verifyNoInteractions(dropboxService);
        verify(workDataService, never()).saveWork(any());
    }

}