package univ_team1.dairyProject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.domain.enums.Weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Weather weather;

    /*@Column
    private boolean isFavorite;*/

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Builder
    public Diary(String title,
                 String content,
                 Emotion emotion,
                 Weather weather){
        this.title = title;
        this.content = content;
        this.emotion = emotion;
        this.weather = weather;
    }

    public void update(String title,
                       String content,
                       Emotion emotion,
                       Weather weather){
        this.title = title;
        this.content = content;
        this.emotion = emotion;
        this.weather = weather;
    }

}
