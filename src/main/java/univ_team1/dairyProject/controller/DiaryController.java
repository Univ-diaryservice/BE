package univ_team1.dairyProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.User;
import univ_team1.dairyProject.dto.AddDiaryRequest;
import univ_team1.dairyProject.dto.DiaryResponse;
import univ_team1.dairyProject.dto.EmotionResponse;
import univ_team1.dairyProject.dto.UpdateDiaryRequest;
import univ_team1.dairyProject.service.DiaryService;

import java.util.List;
import java.util.MissingFormatArgumentException;

import static java.util.Arrays.stream;

@RequiredArgsConstructor
@RestController
@Tag(name="일기 API", description = "일기 CRUD")
public class DiaryController {
    private final DiaryService diaryService;


    @Operation(summary = "일기 작성")
    @PostMapping("/api/diaries")
    public ResponseEntity<?> createDiary(@RequestBody AddDiaryRequest request,
                                         @AuthenticationPrincipal User user){
        try {
            Diary savedDiary = diaryService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedDiary);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("유효하지 않은 감정(emotion) 또는 날씨(weather) 값입니다");
        }
    }


   @Operation(summary = "작성된 일기 전체 리스트 조회" ,description = "제대로 실행되는 지 확인")
    @GetMapping("/api/diaries")
    public ResponseEntity<List<DiaryResponse>> findAllDiaries(
           @AuthenticationPrincipal User user
   ){
        List<DiaryResponse> diaries = diaryService.findAll()
                .stream()
                .map(DiaryResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(diaries);
    }

    @Operation(summary = "작성된 일기 조회")
    @GetMapping("/api/diaries/{id}")
    public ResponseEntity<?> findDiary(@PathVariable long id,
                                       @AuthenticationPrincipal User user){
        try {
            Diary diary = diaryService.findById(id);
            return ResponseEntity.ok()
                    .body(new DiaryResponse(diary));
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("해당 아이디값의 일기를 찾을 수 없습니다 ");
        }
    }
    @Operation(summary = "작성된 일기 삭제")
    @DeleteMapping("/api/diaries/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable long id,
                                            @AuthenticationPrincipal User user){
        diaryService.delete(id);
        return ResponseEntity.ok()
                .build();

    }
    @Operation(summary = "작성된 일기 수정")
    @PutMapping("/api/diaries/{id}")
    public ResponseEntity<Diary> updateDiary(@PathVariable long id,
                                             @RequestBody UpdateDiaryRequest request,
                                             @AuthenticationPrincipal User user){
        Diary updateDiary = diaryService.update(id,request);

        return ResponseEntity.ok()
                .body(updateDiary);
    }

    @Operation(summary = "한 달 감정 목록 조회")
    @GetMapping("/api/diaries/emotions")
    public ResponseEntity<?> getEmotionsByMonth(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User user
    ) {
        try {
            List<EmotionResponse> emotions = diaryService.getEmotionsByMonth(year, month);
            return ResponseEntity.ok()
                    .body(emotions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = " 즐겨찾기 설정")
    @PatchMapping("/api/diaries/{id}/favorite")
    public ResponseEntity<?> updateFavorite(@PathVariable long id,
                                            @AuthenticationPrincipal User user){
        Diary diary = diaryService.updateFavorite(id);
        return ResponseEntity.ok()
                .body("favorite : " + diary.isFavorite());
    }
}
