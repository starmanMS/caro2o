package cn.ms.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class CarO2OApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CarO2OApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  米瑞斯CarO2O平台启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                        "_____________________________________________________________________ \n" +
                        "           ,                                                    / \n" +
                       "---_--_---------)__----)__-----__----)__----__-----__-----__----/-__- \n" +
                       "  / /  )  /    /   )  /   )  /   )  /   )  (_ `  /___)  /___)  /(  \n" +
                      " _/_/__/__/____/______/______(___/__/______(__)__(___ __(___ __/_____ \n");
    }
}
