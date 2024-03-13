package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
public class Event {
    @Min(0)
    @JsonProperty("eventId")
    private Integer id;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private OperationType operation;
    private Integer entityId;
}