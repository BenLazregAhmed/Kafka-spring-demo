package summer.camp.spring_cloud_kafka_demo.entities;

import lombok.*;

import java.util.Date;

@AllArgsConstructor@NoArgsConstructor@Getter@Setter@ToString
public class PageEvent {
    private String page;
    private String user;
    private Date date;
    private int duration;
}
