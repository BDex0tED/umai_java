package com.sayra.umai;

import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.repo_service.WorkDataService;
import com.sayra.umai.service.CloudinaryService;
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
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private WorkServiceImpl workService;

    @Test
    void uploadCover_ShouldUploadToCloudinaryAndSaveWork_WhenFileIsValid() throws Exception {
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

        String expectedCloudinaryUrl = "https://res.cloudinary.com/demo/image/upload/v1234/covers/cover.jpg";

        when(workDataService.findByIdOrThrow(workId)).thenReturn(mockWork);
        when(cloudinaryService.uploadFile(fakeFile, "covers")).thenReturn(expectedCloudinaryUrl);

        String resultUrl = workService.uploadCover(workId, fakeFile);

        assertThat(resultUrl).isEqualTo(expectedCloudinaryUrl);
        assertThat(mockWork.getCoverUrl()).isEqualTo(expectedCloudinaryUrl);

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

        verifyNoInteractions(cloudinaryService);
        verify(workDataService, never()).saveWork(any());
    }

}