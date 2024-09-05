package summer.camp.spring_cloud_kafka_demo.services;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;
import summer.camp.spring_cloud_kafka_demo.entities.PageEvent;

import javax.swing.plaf.PanelUI;
import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PageEventService {
    
    //consumer
    @Bean
    public Consumer<PageEvent> pageEventConsumer(){
        return (input)->{
            System.out.println("**********************");
            System.out.println(input.toString());
            System.out.println("**********************");
        };
    }
    //producer poller
    @Bean
    public Supplier<PageEvent>pageEventSupplier()
    {
        return ()-> new PageEvent(
                Math.random()>0.5?"P1":"P2",
                Math.random()>0.5?"U1":"U2",
                new Date(),
                new Random().nextInt(9000));
    }
    //consumer && producer
    @Bean
    public Function<PageEvent,PageEvent>pageEventFunction()
    {
        return (input)->{
            input.setPage("output page");
            input.setDate(new Date());
            return input;
        };
    }
    //kafka streams
    @Bean
    public Function<KStream<String , PageEvent>,KStream<String ,Long>> kStreamFunction()
    {
        return (input)->{
            KStream<String, Long> stream = input
                    .filter((k, v) -> v.getDuration() > 100)
                    .map((k, v) -> new KeyValue<>(v.getPage(), 0L))
                    .groupBy((k, v) -> k, Grouped.with(Serdes.String(), Serdes.Long()))
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5)))
                    .count(Materialized.as("page-count"))
                    .toStream()
                    .map((k,v)->new KeyValue<>("=>"+ k.window().startTime()+" "+ k.window().endTime()+" "+k.key(),v));
            return stream;
        };
    }
}
