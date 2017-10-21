package knights;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by faker on 2017/10/20.
 */
public class KnightMain {

    public static void main(String[] args)throws Exception{
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/knight.xml");

        ApplicationContext context = new AnnotationConfigApplicationContext(knights.config.KnightConfig.class);

        Knight knight = context.getBean(Knight.class);
        knight.embarkOnQuest();
        //context.close();
    }

}
