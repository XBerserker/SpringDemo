package soundsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration  //表明这是一个配置类
//@ComponentScan    //注释后，抛出BeanCreation-Exception 异常
public class CDPlayerConfig {
    @Bean
    public CompactDisc sgtPeppers(){
        return new SgtPeppers();
    }

    @Bean
    public CompactDisc randomBeatlesCD(){
        int choice = (int) Math.floor(Math.random()*4);
        if (choice==0){
            return new SgtPeppers();
        }else if (choice==1){
            return new WhiteAlbum();
        }else if (choice==2){
            return new HardDaysNight();
        }else {
            return new Revolver();
        }
    }


    @Bean
    public CDPlayer cdPlayer(){
        return new CDPlayer(sgtPeppers());
    }
}
