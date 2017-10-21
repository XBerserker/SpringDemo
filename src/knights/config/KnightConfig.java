package knights.config;

import knights.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by faker on 2017/10/20.
 */
@Configuration
public class KnightConfig {
    @Bean
    public Knight knight(){
        return new BraveKnight(quest(), new Minstrel(System.out));
    }

    @Bean
    public Quest quest(){
        return new SlayDragonQuest(System.out);
    }
}
