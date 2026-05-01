package com.devtino.livesync.domain.sample.service;

import com.devtino.livesync.domain.sample.dto.SampleCreateRequest;
import com.devtino.livesync.domain.sample.dto.SampleResponse;
import com.devtino.livesync.domain.sample.dto.SampleUpdateRequest;
import com.devtino.livesync.domain.sample.entity.SampleEntity;
import com.devtino.livesync.domain.sample.repository.SampleRepository;
import com.devtino.livesync.global.common.exception.CustomException;
import com.devtino.livesync.global.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 샘플 비즈니스 로직
 */
@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    // 생성
    @Transactional
    public SampleResponse create(SampleCreateRequest request) {
        SampleEntity entity = SampleEntity.builder()
                .title(request.title())
                .content(request.content())
                .build();

        SampleEntity saved = sampleRepository.save(entity);

        return SampleResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public SampleResponse get(Long id) {
        SampleEntity entity = sampleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SAMPLE_NOT_FOUND));

        return SampleResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }

    // 수정
    @Transactional
    public SampleResponse update(Long id, SampleUpdateRequest request) {
        SampleEntity entity = sampleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SAMPLE_NOT_FOUND));

        entity.update(request.title(), request.content());

        return SampleResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        if (!sampleRepository.existsById(id)) {
            throw new CustomException(ErrorCode.SAMPLE_NOT_FOUND);
        }
        sampleRepository.deleteById(id);
    }
}

